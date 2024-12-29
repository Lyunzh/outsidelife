package org.se.outl.Service.impl;

import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;
import org.se.outl.Mapper.UserMapper;
import org.se.outl.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceimpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User userAdd(UserDto userDto) {
        userDto.setPassword(BCrypt.hashpw(userDto.getPassword()));
        userMapper.userAdd(userDto);
        return userMapper.userFindByEmail(userDto.getEmail());
    }

    @Override
    public User userFindByEmail(String email) {
        return userMapper.userFindByEmail(email);
    }

    @Override
    public String getCode(String email) {
        return userMapper.getCode(email);
    }


    @Override
    public User userlogin(String account, String password) {
        User u =userMapper.findUserByAccount(account);
        log.info("用户登录u:{}",u);
        if(u!=null&&BCrypt.checkpw(password,u.getPasswordHash())){
            return u;
        }
        return null;
    }
}
