package org.ac.cst8277.zakoor.phil.ums.controllers;

import org.ac.cst8277.zakoor.phil.ums.dao.UmsRepository;
import org.ac.cst8277.zakoor.phil.ums.dtos.Constants;
import org.ac.cst8277.zakoor.phil.ums.dtos.Roles;
import org.ac.cst8277.zakoor.phil.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
public class RolesController {

    @Autowired
    private UmsRepository umsRepository;

    Map<String, Object> response = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = "/roles")
    public Mono<ResponseEntity<Map<String, Object>>> getAllRoles(@RequestHeader(Constants.TOKEN) UUID token) {

        User user = umsRepository.findUserByID(token);
        Map<String, Roles> roles = new HashMap<>();

        if (user.hasRoles(Roles.Role.ADMIN)){
            roles = umsRepository.findAllRoles();
        } else {
            return get403();
        }

        if (roles.size() == 0) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Roles have not been retrieved");
            response.put(Constants.DATA, new HashMap<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of Roles has been requested successfully");
            response.put(Constants.DATA, new ArrayList<>(roles.values()));
        }

        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/roles/{user-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getRolesByUser(@RequestHeader(Constants.TOKEN) UUID token, @PathVariable (value = "user-id", required = true) String userID) {

        User user = umsRepository.findUserByID(token);
        List<Roles> roles = new ArrayList<>();

        if (user.hasRoles(Roles.Role.ADMIN)){
            roles = umsRepository.getUserRoles(UUID.fromString(userID));
        } else {
            return get403();
        }

        if (roles.isEmpty()) {
            response.put(Constants.CODE, "404");
            response.put(Constants.MESSAGE, "Roles have not been found");
            response.put(Constants.DATA, new ArrayList<>());
        } else {
            response.put(Constants.CODE, "200");
            response.put(Constants.MESSAGE, "List of Roles has been requested successfully");
            response.put(Constants.DATA, roles);
        }
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }

    private Mono<ResponseEntity<Map<String, Object>>> get403() {
        response.put(Constants.CODE, "403");
        response.put(Constants.MESSAGE, "User does not have the correct permission");
        response.put(Constants.DATA, new ArrayList<>());
        return Mono.just(ResponseEntity.ok().header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .header(Constants.ACCEPT, Constants.APPLICATION_JSON).body(response));
    }
}
