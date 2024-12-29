package org.se.outl.Service.impl;


import org.se.outl.Dto.TeamDto;
import org.se.outl.Entity.Team;
import org.se.outl.Service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.se.outl.Mapper.TeamMapper;
import org.se.outl.AllData.GlobalData;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceimpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Override
    public List<TeamDto> getTeam(int routeId , int userId)
    {
        List<TeamDto> joined = teamMapper.getJoinedTeam(routeId , userId);
        List<TeamDto> unjoined = teamMapper.getUnjoinedTeam(routeId , userId);
        for(TeamDto team : joined)
        {
            team.setIsJoined(1);
        }
        for(TeamDto team : unjoined)
        {
            team.setIsJoined(0);
        }
        List<TeamDto> all = new ArrayList<>();
        all.addAll(joined);
        all.addAll(unjoined);
        return all;
    }


    @Override
    public void joinTeam(int userId , int teamId)
    {
        teamMapper.teamAdd(teamId);
        teamMapper.joinTeam(userId,teamId);

    }

    @Override
    public void createTeam(int userId , int routeId , Team team)
    {
        teamMapper.createTeam(userId,team);
        int timeId = teamMapper.getTeamId(team.getTeamName());
        teamMapper.bindRoute(routeId,timeId);
        teamMapper.bindUser(userId,timeId);
    }

    @Override
    public void reportTeam(int teamId , String description){
        teamMapper.setStatus(teamId,GlobalData.getPending());
        teamMapper.addReport(teamId,description);
    }

}
