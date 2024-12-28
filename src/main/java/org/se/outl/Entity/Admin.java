package org.se.outl.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private int id;
    private String passwordHash;
    private String email;
    private String avatarUrl;
    private String admin_name;
}
