package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeamMapper {



    @Select("select from route_team rt , team t, team")



}
