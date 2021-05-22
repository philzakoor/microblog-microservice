package org.ac.cst8277.zakoor.phil.ums.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSession {
    int lastLoginTimeStamp;
    int lastLogoutTimeStamp;
    UUID token;
}
