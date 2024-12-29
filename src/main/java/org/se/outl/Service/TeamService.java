package org.se.outl.Service;

import org.se.outl.Dto.TeamDto;
import org.se.outl.Entity.Team;

import java.util.List;

public interface TeamService {
    List<TeamDto> getTeam(int routeId , int userId);

    void joinTeam(int userId , int teamId);

    void createTeam(int userId,int routeId , Team team);
}
