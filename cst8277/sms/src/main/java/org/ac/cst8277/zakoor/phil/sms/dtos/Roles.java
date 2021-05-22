package org.ac.cst8277.zakoor.phil.sms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    public enum Role{
        SUBSCRIBER,
        PRODUCER,
        ADMIN
    }

    UUID roleId;
    String role;
    String description;
}