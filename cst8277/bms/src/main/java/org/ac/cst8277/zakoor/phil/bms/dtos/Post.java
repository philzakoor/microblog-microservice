package org.ac.cst8277.zakoor.phil.bms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Comparable<Post>{
    UUID id;
    UUID uid;
    String content;
    int created;

    @Override
    public int compareTo(Post o) {
        if (this.created > o.created){
            return -1;
        } else {
            return 1;
        }
    }
}
