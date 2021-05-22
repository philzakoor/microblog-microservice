package org.ac.cst8277.zakoor.phil.ums.dao;

import org.ac.cst8277.zakoor.phil.ums.dtos.Roles;
import org.ac.cst8277.zakoor.phil.ums.dtos.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UmsRepository {

    Map<UUID, User> findAllUsers();

    Map<String, Roles> findAllRoles();

    User findUserByID(UUID userId);

    User findUserByEmail(String email);

    UUID createUser(User user);

    int deleteUser(UUID userId);

    List<Roles> getUserRoles(UUID userId);

    UUID login(String username, String password);

    boolean createLogin(UUID userId);

    boolean deleteLogin(UUID userId);
}
