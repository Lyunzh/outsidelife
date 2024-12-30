package org.se.outl.Mapper;

import org.apache.ibatis.annotations.*;
import org.se.outl.Dto.TeamDto;
import org.se.outl.Entity.Team;
import java.util.List;

@Mapper
public interface TeamMapper {
    
    @Select("SELECT t.* FROM team t " +
           "JOIN user_team ut ON t.team_id = ut.team_id " +
           "JOIN route_team rt ON t.team_id = rt.team_id " +
           "WHERE rt.route_id = #{routeId} AND ut.user_id = #{userId} AND t.status = 'active'")
    List<TeamDto> getJoinedTeam(@Param("routeId") int routeId, @Param("userId") int userId);
    
    @Select("SELECT t.* FROM team t " +
           "JOIN route_team rt ON t.team_id = rt.team_id " +
           "WHERE rt.route_id = #{routeId} AND t.status = 'active'" +
           "AND t.team_id NOT IN (SELECT team_id FROM user_team WHERE user_id = #{userId})")
    List<TeamDto> getUnjoinedTeam(@Param("routeId") int routeId, @Param("userId") int userId);
    
    @Update("UPDATE team SET current_num = current_num + 1 WHERE team_id = #{teamId}")
    void teamAdd(@Param("teamId") int teamId);
    
    @Insert("INSERT INTO user_team (user_id, team_id) VALUES (#{userId}, #{teamId})")
    void joinTeam(@Param("userId") int userId, @Param("teamId") int teamId);
    
    @Insert("INSERT INTO team (team_name, limitcnt, current_num, status, description, endtime) " +
           "VALUES (#{team.teamName}, #{team.limitCnt}, 1, 'active', #{team.description}, #{team.endtime})")
    void createTeam(@Param("userId") int userId, @Param("team") Team team);
    
    @Select("SELECT team_id FROM team WHERE team_name = #{teamName}")
    int getTeamId(@Param("teamName") String teamName);
    
    @Insert("INSERT INTO route_team (route_id, team_id) VALUES (#{routeId}, #{teamId})")
    void bindRoute(@Param("routeId") int routeId, @Param("teamId") int teamId);
    
    @Insert("INSERT INTO user_team (user_id, team_id) VALUES (#{userId}, #{teamId})")
    void bindUser(@Param("userId") int userId, @Param("teamId") int teamId);


    @Update("update team set team.status=#{stat} where team_id = #{teamId}")
    void setStatus(@Param("teamId") int teamId, @Param("stat") String stat);

    @Insert("insert into report_team(team_id, description) values(#{teamId},#{description}) ")
    void addReport(@Param("teamId") int teamId, @Param("description") String description);

}
