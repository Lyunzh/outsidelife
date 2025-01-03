package org.se.outl.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String nickname;
    private String account;
    private String password;
    private String email;
    private String avatarUrl;
    private String userId;
    private String code;
}
