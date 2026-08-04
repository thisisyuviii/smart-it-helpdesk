package com.helpdesk.sys.repository;

import com.helpdesk.sys.entity.SlaRule;
import com.helpdesk.sys.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlaRuleRepository extends JpaRepository<SlaRule, Long> {
    Optional<SlaRule> findByCategoryIdAndPriority(Long categoryId, Priority priority);
    List<SlaRule> findByCategoryId(Long categoryId);
}
