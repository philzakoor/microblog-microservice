package org.ac.cst8277.zakoor.phil.bms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    UUID id;
    String name;
    String email;
    String password;
    int created;
    List<Roles> roles = new ArrayList<>();
    LastSession lastSession;

    public void addRole(Roles role) {
        this.roles.add(role);
    }
    public boolean hasRoles(Roles.Role role){

        for (Roles rRole: roles){
            if (rRole.getRole().equalsIgnoreCase(role.toString())){
                return true;
            }
        }

        return false;
    }
}
