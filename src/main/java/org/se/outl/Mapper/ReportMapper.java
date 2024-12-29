package org.se.outl.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.se.outl.Dto.ReportData;

import java.util.List;

@Mapper
public interface ReportMapper {

    @Update("update team set status = 'banned' where team_id = #{teamId}")
    void banReport(@Param("teamId") int teamId);

    @Update("update team set status = 'active' where team_id = #{teamId}")
    void activeReport(@Param("teamId") int teamId);

    @Select("select t.team_name as teamName, rt.description as description ,t.team_id as teamId from team t inner join report_team rt on t.team_id = rt.team_id where t.tatus = 'pending'")
    List<ReportData> getReport();

}
