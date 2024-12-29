package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;
import org.se.outl.Service.MailService;
import org.se.outl.Service.UserService;
import org.se.outl.util.JwtUtils;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto)
    {
        if(userDto.getAccount()==null||userDto.getPassword()==null||userDto.getAccount().isEmpty()||userDto.getPassword().isEmpty()){
            return Result.error(0,"账号或密码为空");
        }
        else{
            User e =userService.userlogin(userDto.getAccount(),userDto.getPassword());

            log.info("用户登录1:{}",e);
            //登录成功，生成并下发令牌
            if(e!=null){
                Map<String,Object> claims = new HashMap<>();
                claims.put("userId",e.getUserId());
                claims.put("identity","user");
                String jwt=JwtUtils.generateJwt(claims);
                log.info("用户登录2:{}",JwtUtils.parseJwt(jwt));
                log.info("fasong");
                return Result.success(1,jwt);
            }

            //登陆失败，返回错误信息


            return Result.error(0,"账号或密码错误");
        }
    }


    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto) {
        User user = userService.userFindByEmail(userDto.getEmail());

        if(user!=null) {
            return Result.error(400,"用户已被注册!");

        }
        else{
            log.info("用户注册:{}",userDto);
            String eml = userDto.getEmail();
            String code = userService.getCode(eml);
            if(Objects.equals(userDto.getCode(),code)) {

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

/**
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
    **/
}
