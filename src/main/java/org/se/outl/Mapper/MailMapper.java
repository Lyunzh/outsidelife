package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MailMapper {

    @Insert("insert into identity(email,code) values (#{email},#{code})")
    void userAdd(@Param("email") String email, @Param("code")String code);


}
