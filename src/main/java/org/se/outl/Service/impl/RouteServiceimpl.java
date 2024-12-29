package org.se.outl.Service.impl;

import org.se.outl.Dto.NodeDto;
import org.se.outl.Dto.RouteDto;
import org.se.outl.Entity.Route;
import org.se.outl.Mapper.NodeMapper;
import org.se.outl.Mapper.RouteMapper;
import org.se.outl.Service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RouteServiceimpl implements RouteService {
    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public List<Route> getSpotRoute(int spotId)
    {
        return routeMapper.getSpotRoute(spotId);
    }

    @Override
    public RouteDto getRoute(int routeId)
    {
        Route route = routeMapper.getRoute(routeId);
        List<NodeDto> nodes = nodeMapper.getNodes(routeId);
        RouteDto routeDto = new RouteDto();
        routeDto.setDescription(route.getDescription());
        routeDto.setTime(route.getTime());
        routeDto.setRouteId(routeId);
        routeDto.setDifficulty(route.getDifficulty());
        routeDto.setNodes(nodes);
        return routeDto;
    }

}
