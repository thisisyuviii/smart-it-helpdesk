package com.helpdesk.sys.controller;

import com.helpdesk.sys.dto.response.SlaRuleResponse;
import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.service.SlaRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sla-rules")
@Tag(name = "SLA Rules", description = "Endpoints for SLA configuration and rules")
@SecurityRequirement(name = "bearerAuth")
public class SlaRuleController {

    private final SlaRuleService slaRuleService;

    public SlaRuleController(SlaRuleService slaRuleService) {
        this.slaRuleService = slaRuleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get all configured SLA rules")
    public ResponseEntity<List<SlaRuleResponse>> getAllSlaRules() {
        return ResponseEntity.ok(slaRuleService.getAllSlaRules());
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    @Operation(summary = "Get SLA rules for a specific category")
    public ResponseEntity<List<SlaRuleResponse>> getSlaRulesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(slaRuleService.getSlaRulesByCategory(categoryId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update SLA rule threshold hours (Admin only)")
    public ResponseEntity<SlaRuleResponse> createOrUpdateSlaRule(@RequestBody Map<String, Object> payload) {
        Long categoryId = Long.valueOf(payload.get("categoryId").toString());
        Priority priority = Priority.valueOf(payload.get("priority").toString());
        Integer maxHours = Integer.valueOf(payload.get("maxResolutionHours").toString());

        SlaRuleResponse response = slaRuleService.createOrUpdateSlaRule(categoryId, priority, maxHours);
        return ResponseEntity.ok(response);
    }
}
