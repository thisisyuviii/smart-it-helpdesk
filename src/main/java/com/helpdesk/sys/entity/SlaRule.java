package com.helpdesk.sys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sla_rules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_id", "priority"})
})
public class SlaRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Column(name = "max_resolution_hours", nullable = false)
    private Integer maxResolutionHours;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SlaRule() {
    }

    public SlaRule(Long id, Category category, Priority priority, Integer maxResolutionHours) {
        this.id = id;
        this.category = category;
        this.priority = priority;
        this.maxResolutionHours = maxResolutionHours;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
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
