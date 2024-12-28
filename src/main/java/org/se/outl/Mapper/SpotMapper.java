package org.se.outl.Mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Entity.Spot;

import java.util.List;


@Mapper
public interface SpotMapper {

    @Select("select * from spot ")
    List<Spot> getRecentSpot();

    @Select("select * from spot where type = 'bike'")
    List<Spot> getHikeSpot();

    @Select("select * from spot where type = 'hike'")
    List<Spot> getBikeSpot();



}
