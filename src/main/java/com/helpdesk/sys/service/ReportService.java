package com.helpdesk.sys.service;

import com.helpdesk.sys.dto.response.TicketReportResponse;

public interface ReportService {
    TicketReportResponse getHelpdeskMetrics();
}
