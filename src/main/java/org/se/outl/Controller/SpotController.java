package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Entity.Spot;
import org.se.outl.Service.SpotService;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/spot")
public class SpotController {

    @Autowired
    private SpotService spotService;

    @GetMapping("/list")
    public Result getSpot(@RequestHeader("Authorization") String token) {
        if(token == null) {
            return Result.error("请先登录");
        }
        log.info("已发送地点");
        return Result.success(spotService.getRecentSpot());
    }

    @GetMapping("/hike")
    public Result getHike(@RequestHeader("Authorization") String token) {
        if(token == null) {
            return Result.error("请先登录");
        }
        return Result.success(spotService.getHikeSpot());
    }

    @GetMapping("/bike")
    public Result getBike(@RequestHeader("Authorization") String token) {
        if(token == null) {
            return Result.error("请先登录");
        }
        return Result.success(spotService.getBikeSpot());
    }
}
