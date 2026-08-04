package com.helpdesk.sys.service.impl;

import com.helpdesk.sys.dto.response.SlaRuleResponse;
import com.helpdesk.sys.entity.Category;
import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.entity.SlaRule;
import com.helpdesk.sys.exception.ResourceNotFoundException;
import com.helpdesk.sys.repository.CategoryRepository;
import com.helpdesk.sys.repository.SlaRuleRepository;
import com.helpdesk.sys.service.SlaRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlaRuleServiceImpl implements SlaRuleService {

    private final SlaRuleRepository slaRuleRepository;
    private final CategoryRepository categoryRepository;

    public SlaRuleServiceImpl(SlaRuleRepository slaRuleRepository, CategoryRepository categoryRepository) {
        this.slaRuleRepository = slaRuleRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlaRuleResponse> getAllSlaRules() {
        return slaRuleRepository.findAll().stream()
                .map(SlaRuleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlaRuleResponse> getSlaRulesByCategory(Long categoryId) {
        return slaRuleRepository.findByCategoryId(categoryId).stream()
                .map(SlaRuleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SlaRuleResponse createOrUpdateSlaRule(Long categoryId, Priority priority, Integer maxResolutionHours) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Optional<SlaRule> existing = slaRuleRepository.findByCategoryIdAndPriority(categoryId, priority);
        SlaRule rule;
        if (existing.isPresent()) {
            rule = existing.get();
            rule.setMaxResolutionHours(maxResolutionHours);
        } else {
            rule = new SlaRule(null, category, priority, maxResolutionHours);
        }

        SlaRule saved = slaRuleRepository.save(rule);
        return SlaRuleResponse.fromEntity(saved);
    }
}
