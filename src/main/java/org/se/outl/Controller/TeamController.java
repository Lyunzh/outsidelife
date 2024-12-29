package org.se.outl.Controller;


import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.se.outl.Entity.Team;
import org.se.outl.Service.TeamService;
import org.se.outl.util.JwtUtils;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {


    @Autowired
    private TeamService teamService;

    @GetMapping("/all/{routeId}")
    public Result getteam(@PathVariable int routeId , @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Claims claims = JwtUtils.parseJwt(token);
        int userId = claims.get("userId", Integer.class);
        return Result.success(teamService.getTeam(routeId,userId));
    }

    @PostMapping("/join/{teamId}")
    public Result join(@PathVariable int teamId , @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Claims claims = JwtUtils.parseJwt(token);
        int userId = claims.get("userId", Integer.class);
        return Result.success(teamService.joinTeam(userId,teamId));
    }


    @PostMapping("/create/{routeId}")
    public Result create(@RequestBody Team team , @RequestHeader("Authorization") String token,@PathVariable int routeId) {
        token = token.substring(7);
        Claims claims = JwtUtils.parseJwt(token);
        int userId = claims.get("userId", Integer.class);
        return Result.success(teamService.createTeam(userId,routeId,team));
    }



}
