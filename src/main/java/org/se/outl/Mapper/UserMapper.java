package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;

@Mapper
public interface UserMapper {

    @Insert("insert into user(email,status,password_hash,nickname,avatar_url) values (#{email},#{status},#{password},#{nickname},#{avatarUrl})")
    void userAdd(UserDto userDto);


    @Select("select * from user where email=#{email}")
    User userFindByEmail(@Param("email")String email);
}
