package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Dto.RouteDto;
import org.se.outl.Entity.Route;

import java.util.List;

@Mapper
public interface RouteMapper {

    @Select("select  * from route   where route_id = #{routeId}")
    public Route getRoute(@Param("routeId") int routeId);


    @Select("select r.* from route r inner join spot_route.s on r.route_id = s.route_id where s.spot_id = #{spotId}")
    public List<Route> getSpotRoute(@Param("spotId") int spotId);


}
