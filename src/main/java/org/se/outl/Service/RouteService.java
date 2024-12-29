package org.se.outl.Service;

import org.se.outl.Dto.RouteDto;
import org.se.outl.Entity.Route;

import java.util.List;

public interface RouteService {
    List<Route> getSpotRoute(int spotId);
    RouteDto getRoute(int routeId);
}
