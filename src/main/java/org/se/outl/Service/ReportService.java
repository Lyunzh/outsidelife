package org.se.outl.Service;

import org.se.outl.Dto.ReportData;

import java.util.List;

public interface ReportService {
    void banReport(int teamId);
    void activeReport(int teamId);

    List<ReportData> getReport();
}
