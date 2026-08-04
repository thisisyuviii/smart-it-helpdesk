package com.helpdesk.sys.service.impl;

import com.helpdesk.sys.dto.response.TicketReportResponse;
import com.helpdesk.sys.entity.TicketStatus;
import com.helpdesk.sys.repository.TicketRepository;
import com.helpdesk.sys.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final TicketRepository ticketRepository;

    public ReportServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketReportResponse getHelpdeskMetrics() {
        try {
            List<Object[]> rawResults = ticketRepository.executeGetHelpdeskMetricsProcedure();
            if (rawResults != null && !rawResults.isEmpty()) {
                Object[] row = rawResults.get(0);
                return new TicketReportResponse(
                        ((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue(),
                        ((Number) row[5]).longValue(),
                        ((Number) row[6]).longValue(),
                        ((Number) row[7]).longValue()
                );
            }
        } catch (Exception e) {
            logger.warn("Native stored procedure call failed or not supported by memory DB, falling back to JPA count queries: {}", e.getMessage());
        }

        // Fallback using JPA repositories if stored procedure is not present in in-memory DB
        long total = ticketRepository.count();
        long created = ticketRepository.findByStatus(TicketStatus.CREATED).size();
        long open = ticketRepository.findByStatus(TicketStatus.OPEN).size();
        long inProgress = ticketRepository.findByStatus(TicketStatus.IN_PROGRESS).size();
        long resolved = ticketRepository.findByStatus(TicketStatus.RESOLVED).size();
        long closed = ticketRepository.findByStatus(TicketStatus.CLOSED).size();
        long slaBreached = ticketRepository.findByIsSlaBreached(true).size();
        long aiOverridden = ticketRepository.findAll().stream().filter(t -> Boolean.TRUE.equals(t.getIsAiOverridden())).count();

        return new TicketReportResponse(total, created, open, inProgress, resolved, closed, slaBreached, aiOverridden);
    }
}
