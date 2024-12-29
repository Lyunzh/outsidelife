package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Entity.Admin;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where account = #{account} ")
    Admin findAdminByAccount(@Param("account")String account);
}
