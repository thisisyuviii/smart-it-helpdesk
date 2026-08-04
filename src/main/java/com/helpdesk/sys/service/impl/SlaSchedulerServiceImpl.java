package com.helpdesk.sys.service.impl;

import com.helpdesk.sys.repository.TicketRepository;
import com.helpdesk.sys.service.SlaSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class SlaSchedulerServiceImpl implements SlaSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SlaSchedulerServiceImpl.class);

    private final TicketRepository ticketRepository;

    public SlaSchedulerServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Scheduled(cron = "${helpdesk.sla.cron:0 */15 * * * *}")
    public void checkSlaBreachesScheduled() {
        logger.info("Executing PL/SQL Stored Procedure sp_check_sla_breaches() via Spring Scheduled Job...");
        try {
            ticketRepository.executeSlaBreachDetectionProcedure();
            logger.info("SLA Breach detection stored procedure completed successfully.");
        } catch (Exception e) {
            logger.error("Error calling sp_check_sla_breaches procedure: {}", e.getMessage(), e);
        }
    }
}
