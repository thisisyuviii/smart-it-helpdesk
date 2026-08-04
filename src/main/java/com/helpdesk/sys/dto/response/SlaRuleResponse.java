package com.helpdesk.sys.dto.response;

import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.entity.SlaRule;

import java.time.LocalDateTime;

public class SlaRuleResponse {
    private Long id;
    private CategoryResponse category;
    private Priority priority;
    private Integer maxResolutionHours;
    private LocalDateTime createdAt;

    public SlaRuleResponse() {
    }

    public SlaRuleResponse(Long id, CategoryResponse category, Priority priority, Integer maxResolutionHours, LocalDateTime createdAt) {
        this.id = id;
        this.category = category;
        this.priority = priority;
        this.maxResolutionHours = maxResolutionHours;
        this.createdAt = createdAt;
    }

    public static SlaRuleResponse fromEntity(SlaRule rule) {
        if (rule == null) return null;
        return new SlaRuleResponse(
                rule.getId(),
                CategoryResponse.fromEntity(rule.getCategory()),
                rule.getPriority(),
                rule.getMaxResolutionHours(),
                rule.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getMaxResolutionHours() {
        return maxResolutionHours;
    }

    public void setMaxResolutionHours(Integer maxResolutionHours) {
        this.maxResolutionHours = maxResolutionHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
