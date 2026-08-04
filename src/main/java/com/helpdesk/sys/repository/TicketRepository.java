package com.helpdesk.sys.repository;

import com.helpdesk.sys.entity.Ticket;
import com.helpdesk.sys.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedById(Long userId);
    List<Ticket> findByAssignedAgentId(Long agentId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByIsSlaBreached(Boolean isSlaBreached);

    @Modifying
    @Transactional
    @Query(value = "CALL sp_check_sla_breaches()", nativeQuery = true)
    void executeSlaBreachDetectionProcedure();

    @Query(value = "CALL sp_get_helpdesk_metrics()", nativeQuery = true)
    List<Object[]> executeGetHelpdeskMetricsProcedure();
}
