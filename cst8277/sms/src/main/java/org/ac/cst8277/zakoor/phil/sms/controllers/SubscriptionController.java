package org.ac.cst8277.zakoor.phil.sms.controllers;

import org.ac.cst8277.zakoor.phil.sms.dao.SmsRepository;
import org.ac.cst8277.zakoor.phil.sms.dtos.*;
import org.ac.cst8277.zakoor.phil.sms.services.HttpResponseExtractor;
import org.ac.cst8277.zakoor.phil.sms.services.MicroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class SubscriptionController {

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private MicroService microService;

    @Value("${ums.paths.user}")
    private String uriUser;

    @Value("${sms.paths.subscriber}")
    private String uriSubscriber;

    Map<String, Object> response = new HashMap<>();

    private Mono<? extends ResponseEntity<Map<String, Object>>> get403() {
        response.put(Constants.CODE, "403");
        response.put(Constants.MESSAGE, "User does not have the correct permission");
        response.put(Constants.DATA, new ArrayList<>());
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/subscriptions")
    public Mono<ResponseEntity<Map<String, Object>>> getAllSubscriptions(@RequestHeader(Constants.TOKEN) UUID token) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            Map<UUID, Subscriber> subscriber = smsRepository.getAllSubscriptions();

            if (user.hasRoles(Roles.Role.ADMIN)){
                subscriber = smsRepository.getAllSubscriptions();
            } else {
                return get403();
            }

            if (subscriber.size() == 0) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Subscriptions not found or no subscriptions");
                response.put(Constants.DATA, new HashMap<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of Subscriptions has been requested successfully");
                response.put(Constants.DATA, subscriber);
            }
            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.GET, path = "/subscriptions/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> getSubscriptions(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "userId", required = true) String userId) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            Subscriber subscriber = null;

            if (user.hasRoles(Roles.Role.SUBSCRIBER)){
                subscriber = smsRepository.getSubscriptions(UUID.fromString(userId));
            } else {
                return get403();
            }


            if (subscriber.getId() == null) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Subscriptions not found or no subscriptions");
                response.put(Constants.DATA, new HashMap<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of Posts has been requested successfully");
                response.put(Constants.DATA, subscriber);
            }
            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.POST, path = "/subscriptions", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> subscribe(@RequestHeader(Constants.TOKEN) UUID token, @RequestBody Subscription subscription) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

            UUID id = null;

            if (user.hasRoles(Roles.Role.SUBSCRIBER)){
                if (subscription.getSubId().equals(user.getId())){
                    id = smsRepository.subscribe(subscription);
                } else {
                    return get403();
                }
            } else {
                return get403();
            }

            if (id == null) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Subscribe unsuccessful");
                response.put(Constants.DATA, "");
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "Successful Subscription");
                response.put(Constants.DATA, id);
            }

            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/subscriptions", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> unsubscribe(@RequestHeader(Constants.TOKEN) UUID token, @RequestBody Subscription subscription) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

            int rsp = -1;

            if (user.hasRoles(Roles.Role.SUBSCRIBER)){
                if (subscription.getSubId().equals(user.getId())){
                    rsp = smsRepository.unsubscribe(subscription);
                } else {
                    return get403();
                }
            } else {
                return get403();
            }

            if (rsp != 1) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Unsubscribe unsuccessful");
                response.put(Constants.DATA, "");
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "Successful unsubscription");
                response.put(Constants.DATA, subscription);
            }
            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }
}
