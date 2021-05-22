package org.ac.cst8277.zakoor.phil.ums.controllers;

import org.ac.cst8277.zakoor.phil.ums.dao.UmsRepository;
import org.ac.cst8277.zakoor.phil.ums.dtos.Constants;
import org.ac.cst8277.zakoor.phil.ums.dtos.Roles;
import org.ac.cst8277.zakoor.phil.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
public class LoginController {

    @Autowired
    private UmsRepository umsRepository;

    Map<String, Object> response = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/user/verify")
    public Mono<ResponseEntity<Map<String, Object>>> verify(@RequestHeader(Constants.TOKEN) UUID token) {
        User user = umsRepository.findUserByID(token);

        if (user.getLastSession().getLastLoginTimeStamp() > user.getLastSession().getLastLogoutTimeStamp()){
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Token expired");
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "token verified");
            response.put(Constants.DATA, user);
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON, Constants.TOKEN, user.getLastSession().getToken().toString()).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@AuthenticationPrincipal OAuth2User principal) {
        User user = umsRepository.findUserByEmail(principal.getAttribute("email"));

        if (user != null){
            umsRepository.createLogin(user.getId());
        } else {
            user.setEmail(principal.getAttribute("email"));
            user.setName(principal.getAttribute("name"));

            Map<String, Roles> roles = umsRepository.findAllRoles();
            List<Roles> userRoles = new ArrayList<>();
            userRoles.add(roles.get(Roles.Role.SUBSCRIBER));

            umsRepository.createUser(user);
            user = umsRepository.findUserByEmail(principal.getAttribute("email"));
            umsRepository.createLogin(user.getId());
        }

        if (user.getLastSession().getLastLoginTimeStamp() > user.getLastSession().getLastLogoutTimeStamp()){
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Token expired");
            response.put(Constants.DATA, "");
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "login successful here is a token");
            response.put(Constants.DATA, user.getLastSession().getToken());
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON, Constants.TOKEN, user.getLastSession().getToken().toString()).body(response));
    }

        @RequestMapping(method = RequestMethod.GET, path = "/login/{email}")
    public Mono<ResponseEntity<Map<String, Object>>> login(@PathVariable(value = "email", required = true) String email, @RequestBody String password ) {

        UUID userId = umsRepository.login(email, password);

        if (userId == null){
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Wrong credentials");
            response.put(Constants.DATA, "");
        } else {
            boolean login = umsRepository.createLogin(userId);
            if (login) {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "login successful here is a token");
                response.put(Constants.DATA, userId);
            } else {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Wrong credentials");
                response.put(Constants.DATA, "");
            }
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/logout/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> logout(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable(value = "userId", required = true) UUID userId) {

        User user = umsRepository.findUserByID(userId);
        boolean logout = false;

        if (user.getId().equals(token)){
            logout = umsRepository.deleteLogin(userId);
        }

        if (!logout){
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Wrong credentials");
            response.put(Constants.DATA, "");
        } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "logout successful");
                response.put(Constants.DATA, "");
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}
