package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Entity.Admin;
import org.se.outl.Service.AdminService;
import org.se.outl.util.JwtUtils;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;


    @PostMapping("/login")
    public Result adminlogin(@RequestBody Admin admin){
        log.info("管理员登录:{}",admin);
        if(admin.getAccount()==null||admin.getPassword()==null||admin.getAccount().isEmpty()||admin.getPassword().isEmpty()){
            return Result.error(0,"账号或密码为空");
        }

        Admin e =adminService.adminlogin(admin.getAccount(),admin.getPassword());

        log.info("管理员登录1:{}",e);
        //登录成功，生成并下发令牌
        if(e!=null){
            Map<String,Object> claims = new HashMap<>();
            claims.put("adminId",e.getAdminId());
            claims.put("identity","admin");
            String jwt= JwtUtils.generateJwt(claims);
            log.info("管理员登录2:{}",JwtUtils.parseJwt(jwt));
            return Result.success(1,jwt);
        }

        //登陆失败，返回错误信息


        return Result.error(0,"账号或密码错误");


    }
}
