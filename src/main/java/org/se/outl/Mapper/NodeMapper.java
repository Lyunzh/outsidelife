package org.se.outl.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Dto.NodeDto;

import java.util.List;

@Mapper
public interface NodeMapper {


    @Select("select n.node_id as nodeId , n.description as description , n.name as name ,n.image_url as imageUrl, n.location as location , r.seq_cnt as seqCnt from node n inner join route_node r on n.node_id = r.node_id where r.route_id = #{routId}")
    List<NodeDto> getNodes(@Param("routeId")int routeId);
}
