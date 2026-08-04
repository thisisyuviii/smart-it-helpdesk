package com.helpdesk.sys.service;

import com.helpdesk.sys.dto.response.SlaRuleResponse;
import com.helpdesk.sys.entity.Priority;

import java.util.List;

public interface SlaRuleService {
    List<SlaRuleResponse> getAllSlaRules();
    List<SlaRuleResponse> getSlaRulesByCategory(Long categoryId);
    SlaRuleResponse createOrUpdateSlaRule(Long categoryId, Priority priority, Integer maxResolutionHours);
}
