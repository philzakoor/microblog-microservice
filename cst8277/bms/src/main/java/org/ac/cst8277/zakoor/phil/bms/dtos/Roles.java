package org.ac.cst8277.zakoor.phil.bms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

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