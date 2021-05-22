package org.ac.cst8277.zakoor.phil.bms.controllers;

import org.ac.cst8277.zakoor.phil.bms.dao.BmsRepository;
import org.ac.cst8277.zakoor.phil.bms.dtos.*;
import org.ac.cst8277.zakoor.phil.bms.services.HttpResponseExtractor;
import org.ac.cst8277.zakoor.phil.bms.services.MicroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
public class PostController {

    @Autowired
    private BmsRepository bmsRepository;

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

    @RequestMapping(method = RequestMethod.GET, path = "/posts")
    public Mono<ResponseEntity<Map<String, Object>>> getAllPosts(@RequestHeader(Constants.TOKEN) UUID token){
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

            Map<UUID, Post> posts = new HashMap<>();

            if (user.hasRoles(Roles.Role.ADMIN)) {
                posts = bmsRepository.getAllPosts();
            } else {
                return get403();
            }

            if (posts.size() == 0) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Posts not found or no posts found");
                response.put(Constants.DATA, new ArrayList<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of Posts has been requested successfully");
                response.put(Constants.DATA, new ArrayList<>(posts.values()));
            }
            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.GET, path = "/posts/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> getAllPostsByUser(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "userId", required = true) String userId) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            Map<UUID, Post> posts = new HashMap<>();

            if (user.hasRoles(Roles.Role.SUBSCRIBER) || user.hasRoles(Roles.Role.ADMIN)) {
                posts = bmsRepository.getAllPostsByUser(UUID.fromString(userId));
            } else {
                return get403();
            }

            if (posts.size() == 0) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Posts not found or zero posts found");
                response.put(Constants.DATA, new ArrayList<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of Posts has been requested successfully");
                response.put(Constants.DATA, new ArrayList<>(posts.values()));
            }
            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.GET, path = "/posts/{postId}")
    public Mono<ResponseEntity<Map<String, Object>>> getPostById(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "postId", required = true) UUID postId) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            Post post = null;

            //accepts admin and subscriber roles
            if (user.hasRoles(Roles.Role.ADMIN) || user.hasRoles(Roles.Role.SUBSCRIBER)) {
                post = bmsRepository.getPostById(postId);

            //accepts producer role if it's user's post
            } else if (user.hasRoles(Roles.Role.PRODUCER)) {
                post = bmsRepository.getPostById(postId);
                if (!post.getUid().equals(user.getId())){
                    return get403();
                }
            } else {
                return get403();
            }

            if (post == null) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Posts not found or zero posts found");
                response.put(Constants.DATA, new ArrayList<>());
            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "List of Posts has been requested successfully");
                response.put(Constants.DATA, post);
            }

            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.POST, path = "/posts", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createPost(@RequestHeader(Constants.TOKEN) UUID token, @RequestBody Post post) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            UUID postId = null;

            if (user.hasRoles(Roles.Role.PRODUCER)) {
                postId = bmsRepository.createPost(post);
                response.put(Constants.CODE, "201");
                response.put(Constants.MESSAGE, "Post created");
                response.put(Constants.DATA, postId.toString());
            } else {
                return get403();
            }

            if (postId.toString().isEmpty()) {
                response.put(Constants.CODE, "500");
                response.put(Constants.MESSAGE, "Error post not created try again later");
                response.put(Constants.DATA, new ArrayList<>());
            }

            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/posts/{postId}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable(value = "postId", required = true) UUID postId) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            int result = -1;

            if (user.hasRoles(Roles.Role.ADMIN)) {
                result = bmsRepository.deletePost(postId);
            }

            if (user.hasRoles(Roles.Role.PRODUCER)) {
                Post post = bmsRepository.getPostById(postId);
                if (post.getId() != null) {
                    if (post.getUid().equals(user.getId())) {
                        result = bmsRepository.deletePost(postId);
                    }
                }
            } else {
                return get403();
            }

            if (result != 1) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Post not found");
                response.put(Constants.DATA, new ArrayList<>());

            } else {
                response.put(Constants.CODE, "200");
                response.put(Constants.MESSAGE, "post deleted");
                response.put(Constants.DATA, postId);
            }

            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/posts/{postId}", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> editPost(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "postId", required = true) UUID pid, @RequestBody Post post) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(res -> {

            User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);
            UUID postId = null;

            if (user.hasRoles(Roles.Role.ADMIN)) {
                postId = bmsRepository.editPost(pid, post);
            }

            if (user.hasRoles(Roles.Role.PRODUCER)) {
                Post rPost = bmsRepository.getPostById(pid);
                if (rPost.getId() != null) {
                    if (rPost.getUid().equals(user.getId())) {
                        postId = bmsRepository.editPost(pid, post);
                    }
                }
            } else {
                return get403();
            }

            if (postId == null) {
                response.put(Constants.CODE, "404");
                response.put(Constants.MESSAGE, "Post not found");
                response.put(Constants.DATA, "");
            } else {
                response.put(Constants.CODE, "201");
                response.put(Constants.MESSAGE, "Post Edited");
                response.put(Constants.DATA, postId.toString());
            }

            return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                    .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
        });
    }

    @RequestMapping(method = RequestMethod.GET, path = "/posts/{userId}/feed")
    public Mono<ResponseEntity<Map<String, Object>>> getFeed(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "userId", required = true) UUID userId) {
        return microService.retrieveUmsData(uriUser + "/" + token.toString(), token).flatMap(umsRes -> {
            return microService.retrieveSmsData(uriSubscriber + "/" + token.toString(), token).flatMap(smsRes -> {
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(umsRes, User.class);
                Map<UUID, Post> posts = new HashMap<>();

                if (user.hasRoles(Roles.Role.SUBSCRIBER)) {
                    Subscriber subscriber = HttpResponseExtractor.extractDataFromHttpClientResponse(smsRes, Subscriber.class);
                    List<UUID> subs = subscriber.getSubscriptions();

                    for (UUID sid : subs){
                        posts.putAll(bmsRepository.getAllPostsByUser(sid));
                    }

                    posts = PostHelper.sortByValue(posts);
                } else {
                    return get403();
                }

                if (posts.size() == 0) {
                    response.put(Constants.CODE, "500");
                    response.put(Constants.MESSAGE, "Error posts haven't been retrieved, try again later");
                    response.put(Constants.DATA, new HashMap<>());
                } else {
                    response.put(Constants.CODE, "200");
                    response.put(Constants.MESSAGE, "List of Posts has been requested successfully");
                    response.put(Constants.DATA, new ArrayList<>(posts.values()));
                }

                return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                        .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
            });
        });
    }
}
