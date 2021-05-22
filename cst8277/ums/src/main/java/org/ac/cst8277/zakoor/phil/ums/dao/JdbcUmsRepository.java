package org.ac.cst8277.zakoor.phil.ums.dao;


import org.ac.cst8277.zakoor.phil.ums.dtos.Constants;
import org.ac.cst8277.zakoor.phil.ums.dtos.LastSession;
import org.ac.cst8277.zakoor.phil.ums.dtos.Roles;
import org.ac.cst8277.zakoor.phil.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Repository
public class JdbcUmsRepository implements UmsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<UUID, User> findAllUsers() {
        Map<UUID, User> users = new HashMap<>();

        List<Object> oUsers = jdbcTemplate.query(Constants.GET_ALL_USERS,
                (rs, rowNum) -> new User(DaoHelper.bytesArrayToUuid(rs.getBytes("users.id")), rs.getString("users.name"),
                        rs.getString("users.email"), rs.getString("users.password"), rs.getInt("users.created"),
                        Arrays.asList(new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                                rs.getString("roles.name"), rs.getString("roles.description"))),
                        new LastSession(rs.getInt("last_visit.in"), rs.getInt("last_visit.out"), DaoHelper.bytesArrayToUuid(rs.getBytes("last_visit.token")))));

        for (Object oUser : oUsers) {
            if (!users.containsKey(((User) oUser).getId())) {
                User user = new User();
                user.setId(((User) oUser).getId());
                user.setName(((User) oUser).getName());
                user.setEmail(((User) oUser).getEmail());
                user.setPassword(((User) oUser).getPassword());
                user.setCreated(((User) oUser).getCreated());
                user.setLastSession(((User) oUser).getLastSession());
                users.put(((User) oUser).getId(), user);
            }
            users.get(((User) oUser).getId()).addRole(((User) oUser).getRoles().get(0));
        }
        return users;
    }

    @Override
    public User findUserByID(UUID userId) {
        User user = new User();
        List<Object> users = jdbcTemplate.query(Constants.GET_USER_BY_ID_FULL,
                (rs, rowNum) -> new User(DaoHelper.bytesArrayToUuid(rs.getBytes("users.id")), rs.getString("users.name"),
                        rs.getString("users.email"), rs.getString("users.password"), rs.getInt("users.created"),
                        Arrays.asList(new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                                rs.getString("roles.name"), rs.getString("roles.description"))),
                        new LastSession(rs.getInt("last_visit.in"), rs.getInt("last_visit.out"), DaoHelper.bytesArrayToUuid(rs.getBytes("last_visit.token")))),
                userId.toString());
        for (Object oUser : users) {
            if (user.getId() == null) {
                user.setId(((User) oUser).getId());
                user.setName(((User) oUser).getName());
                user.setEmail(((User) oUser).getEmail());
                user.setPassword(((User) oUser).getPassword());
                user.setCreated(((User) oUser).getCreated());
                user.setLastSession(((User) oUser).getLastSession());
            }
            user.addRole(((User) oUser).getRoles().get(0));
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {

        User user = new User();

        List<Object> users = jdbcTemplate.query(Constants.GET_USER_BY_EMAIL_FULL,
                (rs, rowNum) -> new User(DaoHelper.bytesArrayToUuid(rs.getBytes("users.id")), rs.getString("users.name"),
                        rs.getString("users.email"), rs.getString("users.password"), rs.getInt("users.created"),
                        Arrays.asList(new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                                rs.getString("roles.name"), rs.getString("roles.description"))),
                        new LastSession(rs.getInt("last_visit.in"), rs.getInt("last_visit.out"), DaoHelper.bytesArrayToUuid(rs.getBytes("last_visit.token")))),
                email);

        for (Object oUser : users) {
            if (user.getId() == null) {
                user.setId(((User) oUser).getId());
                user.setName(((User) oUser).getName());
                user.setEmail(((User) oUser).getEmail());
                user.setPassword(((User) oUser).getPassword());
                user.setCreated(((User) oUser).getCreated());
                user.setLastSession(((User) oUser).getLastSession());
            }
            user.addRole(((User) oUser).getRoles().get(0));
        }
        return user;
    }

    @Override
    public UUID createUser(User user) {
        long timestamp = Instant.now().getEpochSecond();
        Map<String, Roles> roles = this.findAllRoles();
        UUID userId = UUID.randomUUID();

        try {
            jdbcTemplate.update(Constants.CREATE_USER, userId.toString(), user.getName(), user.getEmail(),
                    user.getPassword(), timestamp, null);
            for (Roles role : user.getRoles()) {
                jdbcTemplate.update(Constants.ASSIGN_ROLE, userId.toString(),
                        roles.get(role.getRole()).getRoleId().toString());
            }
        } catch (Exception e) {
            return null;
        }

        return userId;
    }

    @Override
    public int deleteUser(UUID userId) {
        return jdbcTemplate.update(Constants.DELETE_USER, userId.toString());
    }

    @Override
    public List<Roles> getUserRoles(UUID userId) {
        List<Roles> roles = new ArrayList<>();

        List<Object> oRoles = jdbcTemplate.query(Constants.GET_USER_ROLE,
                (rs, rowNum) -> new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                        rs.getString("roles.name"), rs.getString("roles.description")), (Object) DaoHelper.uuidToBytesArray(userId));

        for (Object oRole : oRoles) {
                Roles role = new Roles();
                role.setRoleId(((Roles) oRole).getRoleId());
                role.setRole(((Roles) oRole).getRole());
                role.setDescription(((Roles) oRole).getDescription());
                roles.add(role);
        }

        if (roles.size() == 0l){
            return null;
        }

        return roles;
    }

    @Override
    public UUID login(String username, String password) {
        return jdbcTemplate.query(Constants.LOGIN, rs -> {
            return DaoHelper.bytesArrayToUuid(rs.getBytes("id"));
        });
    }

    @Override
    public boolean createLogin(UUID userId) {
        long inTimestamp = Instant.now().getEpochSecond();
        long outTimestamp = Instant.now().plus(30, ChronoUnit.DAYS).getEpochSecond();
        UUID token = UUID.randomUUID();

        try {
            jdbcTemplate.update(Constants.CREATE_LOGIN, userId.toString(), inTimestamp, outTimestamp, token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteLogin(UUID userId) {
        try {
            jdbcTemplate.update(Constants.DELETE_LAST_VISIT, userId.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Roles> findAllRoles() {
        Map<String, Roles> roles = new HashMap<>();
        jdbcTemplate.query(Constants.GET_ALL_ROLES, rs -> {
            Roles role = new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")), rs.getString("roles.name"),
                    rs.getString("roles.description"));
            roles.put(rs.getString("roles.name"), role);
        });
        return roles;
    }
}
