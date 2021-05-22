package org.ac.cst8277.zakoor.phil.bms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSession {
    int lastLoginTimeStamp;
    int lastLogoutTimeStamp;
}
