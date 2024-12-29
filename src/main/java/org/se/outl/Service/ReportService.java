package org.se.outl.Service;

import org.se.outl.Dto.ReportData;

public interface ReportService {
    void banReport(int teamId);
    void activeReport(int teamId);

    ReportData getReport();
}
