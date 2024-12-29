package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.Service.ReportService;
import org.se.outl.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/list")
    public Result getReport() {
        return Result.success(reportService.getReport());
    }

    @PostMapping("/active/{teamId}")
    public Result activeReport(@PathVariable int teamId) {
        reportService.activeReport(teamId);
        return Result.success();
    }

    @PostMapping("/ban/{teamId}")
    public Result banReport(@PathVariable int teamId) {
        reportService.banReport(teamId);
        return Result.success();
    }
}
