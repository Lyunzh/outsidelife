package org.se.outl.Service.impl;

import org.se.outl.Dto.ReportData;
import org.se.outl.Mapper.ReportMapper;
import org.se.outl.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReportServiceimpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;


    @Override
    public void banReport(int teamId){
        reportMapper.banReport(teamId);
    }

    @Override
    public void activeReport(int teamId){
        reportMapper.activeReport(teamId);
    }

    @Override
    public List<ReportData> getReport(){
        return reportMapper.getReport();
    }
}
