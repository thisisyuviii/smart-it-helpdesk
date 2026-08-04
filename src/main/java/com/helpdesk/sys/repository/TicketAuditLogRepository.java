package com.helpdesk.sys.repository;

import com.helpdesk.sys.entity.TicketAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAuditLogRepository extends JpaRepository<TicketAuditLog, Long> {
    List<TicketAuditLog> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}
