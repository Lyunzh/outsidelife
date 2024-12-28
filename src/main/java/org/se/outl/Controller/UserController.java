package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;
import org.se.outl.Service.MailService;
import org.se.outl.Service.UserService;
import org.se.outl.util.JwtUtils;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;


    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto) {
        User user = userService.userFindByEmail(userDto.getEmail());

        if(user!=null) {
            return Result.error(400,"用户已被注册!");

        }
        else{
            log.info("用户注册:{}",userDto);
            String eml = userDto.getEmail();
            if(Objects.equals(s,userDto.getCode())) {

                User u = userService.userAdd(userDto);

                if (u != null) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("user_id", u.getUserId());
                    claims.put("identity", "user");
                    log.info(claims.toString());

                    String jwt = JwtUtils.generateJwt(claims);
                    return Result.success(1, jwt);

                } else {
                    return Result.error(0, "未成功注册，请询问后台程序！");

                }
            }
            else{
                return Result.error(2,"验证失败");
            }
        }
    }


    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto) {
        User user = userService.userFindByEmail(userDto.getEmail());
        if(user!=null) {
            Map<String,Object> claims = new HashMap<>();
            claims.put("user_id",user.getUserId());
            claims.put("identity","user");
            log.info(claims.toString());
            String jwt= JwtUtils.generateJwt(claims);
            return Result.success(1,jwt);
        }
        else{
            return Result.error(0,"用户为注册！");
        }
    }
}
