package org.se.outl.Service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.se.outl.Entity.Admin;
import org.se.outl.Mapper.AdminMapper;
import org.se.outl.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AdminServiceimpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin adminlogin(String account , String password)
    {
        Admin a = adminMapper.findAdminByAccount(account);
        log.info("管理员登录a:{}",a);
        if(a!=null&& BCrypt.checkpw(password ,a.getPassword())){
            return a;
        }
        return null;
    }
}
