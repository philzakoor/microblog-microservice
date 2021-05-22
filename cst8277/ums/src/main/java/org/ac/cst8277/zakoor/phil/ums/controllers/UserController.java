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
public class UserController {

    @Autowired
    private UmsRepository umsRepository;

    Map<String, Object> response = new HashMap<>();

    private Mono<ResponseEntity<Map<String, Object>>> get403() {
        response.put(Constants.CODE, "403");
        response.put(Constants.MESSAGE, "User does not have the correct permission");
        response.put(Constants.DATA, new ArrayList<>());
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users")
    public Mono<ResponseEntity<Map<String, Object>>> getAllUsers(@RequestHeader(Constants.TOKEN) UUID token) {

        User user = umsRepository.findUserByID(token);
        Map<UUID, User> users = new HashMap<>();

        if (user.hasRoles(Roles.Role.ADMIN)){
            users = umsRepository.findAllUsers();
        } else {
            return get403();
        }

        if (users == null) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Users have not been retrieved");
            response.put(Constants.DATA, new HashMap<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of Users has been requested successfully");
            response.put(Constants.DATA, new ArrayList<>(users.values()));
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/users/user/{user-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getUser(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable(value = "user-id", required = true) UUID userId) {

        User user = umsRepository.findUserByID(token);
        User rUser = null;

        if (user.hasRoles(Roles.Role.ADMIN)){
            rUser = umsRepository.findUserByID(userId);
        } else if (user.getId().equals(userId)){
            rUser = umsRepository.findUserByID(userId);
        } else {
            return get403();
        }

        if (user.getId() == null) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "User have not been found");
            response.put(Constants.DATA, new User());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "User has been retrieved successfully");
            response.put(Constants.DATA, user);
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/users/user", consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody User user) {

        UUID userId = umsRepository.createUser(user);

        if (userId == null) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "User has not been created");
            response.put(Constants.DATA, "Check email for duplicates first");
        } else {
            response.put(Constants.CODE, "201");
            response.put(Constants.MESSAGE, "User created");
            response.put(Constants.DATA, userId.toString());
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/users/user/{user-id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable(value = "user-id", required = true) UUID userId) {

        User user = umsRepository.findUserByID(token);
        int result = -1;

        if (user.hasRoles(Roles.Role.ADMIN)){
            result = umsRepository.deleteUser(userId);
        } else if (user.getId().equals(userId)) {
            result = umsRepository.deleteUser(userId);
        } else {
            return get403();
        }

        if (result != 1) {
            response.put(Constants.CODE, "500");
            response.put(Constants.MESSAGE, "Error happened while deleting user");
            response.put(Constants.DATA, userId);
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "User deleted");
            response.put(Constants.DATA, userId.toString());
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}