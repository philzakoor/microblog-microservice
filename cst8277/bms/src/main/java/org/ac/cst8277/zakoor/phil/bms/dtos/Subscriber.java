package org.ac.cst8277.zakoor.phil.bms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {
    UUID id;
    ArrayList<UUID> subscriptions;
}
