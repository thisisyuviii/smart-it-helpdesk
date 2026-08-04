-- Seed initial categories
INSERT INTO categories (id, name, description) VALUES 
(1, 'HARDWARE', 'Physical equipment issues like laptops, monitors, printers'),
(2, 'SOFTWARE', 'Operating system, IDEs, office suite, and internal tool bugs'),
(3, 'NETWORK', 'VPN, Wi-Fi, DNS, firewall, and internet connectivity issues'),
(4, 'ACCESS', 'Password resets, role permission requests, SSO authentication')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed SLA rules (Category, Priority, Max Hours)
-- HARDWARE
INSERT INTO sla_rules (category_id, priority, max_resolution_hours) VALUES
(1, 'CRITICAL', 4),
(1, 'HIGH', 12),
(1, 'MEDIUM', 24),
(1, 'LOW', 72)
ON DUPLICATE KEY UPDATE max_resolution_hours=VALUES(max_resolution_hours);

-- SOFTWARE
INSERT INTO sla_rules (category_id, priority, max_resolution_hours) VALUES
(2, 'CRITICAL', 6),
(2, 'HIGH', 24),
(2, 'MEDIUM', 48),
(2, 'LOW', 96)
ON DUPLICATE KEY UPDATE max_resolution_hours=VALUES(max_resolution_hours);

-- NETWORK
INSERT INTO sla_rules (category_id, priority, max_resolution_hours) VALUES
(3, 'CRITICAL', 2),
(3, 'HIGH', 8),
(3, 'MEDIUM', 24),
(3, 'LOW', 48)
ON DUPLICATE KEY UPDATE max_resolution_hours=VALUES(max_resolution_hours);

-- ACCESS
INSERT INTO sla_rules (category_id, priority, max_resolution_hours) VALUES
(4, 'CRITICAL', 2),
(4, 'HIGH', 4),
(4, 'MEDIUM', 12),
(4, 'LOW', 24)
ON DUPLICATE KEY UPDATE max_resolution_hours=VALUES(max_resolution_hours);

-- Seed default users (passwords are BCrypt hash of "Password@123")
-- BCrypt for "Password@123": $2a$10$e7v1K.wW/c8V4n4eZ2O0xeK1C5m8V1qJ5jH4N0F1vG.h7I3L9k2SO
INSERT INTO users (id, username, email, password, full_name, role, enabled) VALUES
(1, 'admin', 'admin@helpdesk.com', '$2a$10$e7v1K.wW/c8V4n4eZ2O0xeK1C5m8V1qJ5jH4N0F1vG.h7I3L9k2SO', 'System Administrator', 'ADMIN', TRUE),
(2, 'agent_john', 'john.agent@helpdesk.com', '$2a$10$e7v1K.wW/c8V4n4eZ2O0xeK1C5m8V1qJ5jH4N0F1vG.h7I3L9k2SO', 'John Agent', 'AGENT', TRUE),
(3, 'emp_alice', 'alice.emp@company.com', '$2a$10$e7v1K.wW/c8V4n4eZ2O0xeK1C5m8V1qJ5jH4N0F1vG.h7I3L9k2SO', 'Alice Employee', 'EMPLOYEE', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);
