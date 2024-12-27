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
    private String password;
    private String email;
    private String status;
    private String avatarUrl;
}
