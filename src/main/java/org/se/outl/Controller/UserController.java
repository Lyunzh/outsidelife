package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;
import org.se.outl.Service.UserService;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public Result register(@RequestBody UserDto userDto) {
        User user = userService.findUserByNickname(userDto.getNickname());

        if(user!=null) {
            return Result.error(400,"用户已被注册!");

        }
        else{
            log.info("用户注册:{}",userDto);
            userService.register(userDto);

            if(userDto!=null) {
                Map<String,Object> claims = new HashMap<>();
                claims.put("userId",userDto)
            }
        }
    }
}
