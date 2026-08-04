-- Stored Procedures for Business Logic and Reporting
-- Procedure 1: SLA Breach Detection Engine
-- Flags tickets as SLA breached if elapsed time since creation exceeds the max_resolution_hours defined in sla_rules.

DROP PROCEDURE IF EXISTS sp_check_sla_breaches;

DELIMITER //

CREATE PROCEDURE sp_check_sla_breaches()
BEGIN
    -- Update tickets where status is OPEN or IN_PROGRESS and elapsed hours > SLA threshold
    UPDATE tickets t
    JOIN sla_rules r ON t.category_id = r.category_id AND t.priority = r.priority
    SET t.is_sla_breached = TRUE
    WHERE t.status IN ('OPEN', 'IN_PROGRESS', 'CREATED')
      AND t.is_sla_breached = FALSE
      AND TIMESTAMPDIFF(HOUR, t.created_at, NOW()) > r.max_resolution_hours;
END //

DELIMITER ;


-- Procedure 2: Helpdesk Executive Aggregated Metrics Report
-- Returns key aggregate statistics across tickets, categories, and agents.

DROP PROCEDURE IF EXISTS sp_get_helpdesk_metrics;

DELIMITER //

CREATE PROCEDURE sp_get_helpdesk_metrics()
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM tickets) AS total_tickets,
        (SELECT COUNT(*) FROM tickets WHERE status = 'CREATED') AS total_created,
        (SELECT COUNT(*) FROM tickets WHERE status = 'OPEN') AS total_open,
        (SELECT COUNT(*) FROM tickets WHERE status = 'IN_PROGRESS') AS total_in_progress,
        (SELECT COUNT(*) FROM tickets WHERE status = 'RESOLVED') AS total_resolved,
        (SELECT COUNT(*) FROM tickets WHERE status = 'CLOSED') AS total_closed,
        (SELECT COUNT(*) FROM tickets WHERE is_sla_breached = TRUE) AS total_sla_breached,
        (SELECT COUNT(*) FROM tickets WHERE is_ai_overridden = TRUE) AS total_ai_overridden;
END //

DELIMITER ;
