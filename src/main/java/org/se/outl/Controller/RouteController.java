package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Service.RouteService;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/all/{spotId}")
    public Result getSpotRoute(@PathVariable int spotId) {
        log.info("getSpotRoute spotId: {}", spotId);
        return Result.success(routeService.getSpotRoute(spotId));
    }

    @GetMapping("/{routeId}")
    public Result getRoute(@PathVariable int routeId) {
        log.info("getRoute routeId: {}", routeId);
        return Result.success(routeService.getRoute(routeId));
    }
}
