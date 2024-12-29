package org.se.outl.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;
    private String nickname;
    private String account;
    private String passwordHash;
    private String email;
    private String avatarUrl;
}
