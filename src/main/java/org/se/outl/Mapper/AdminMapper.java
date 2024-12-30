package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Entity.Admin;

@Mapper
public interface AdminMapper {

    @Select("select password_hash as password , account as account from admin where account = #{account} ")
    Admin findAdminByAccount(@Param("account")String account);


    @Insert("insert into admin(password_hash, email, avatar_url, admin_name, account) values(#{password},#{email},'null',#{admin_name},#{account})")
    void adminRegister(Admin admin);
}
