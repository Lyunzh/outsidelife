package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;

@Mapper
public interface UserMapper {

    @Insert("insert into user(email,password_hash,nickname,avatar_url,account) values (#{email},#{password},#{nickname},#{avatarUrl},#{account})")
    void userAdd(UserDto userDto);


    @Select("select * from user where email=#{email}")
    User userFindByEmail(@Param("email")String email);

    @Select("select i.code as code from identity i where email=#{email}")
    String getCode(@Param("email") String email);

    @Select("select * from user where account= #{account}")
    User findUserByAccount(@Param("account")String account);
}
