package com.helpdesk.sys.service;

import java.util.concurrent.CompletableFuture;

public interface AiTriageService {
    
    public static class AiTriageResult {
        private String suggestedCategory;
        private String suggestedPriority;
        private String summary;

        public AiTriageResult() {
        }

        public AiTriageResult(String suggestedCategory, String suggestedPriority, String summary) {
            this.suggestedCategory = suggestedCategory;
            this.suggestedPriority = suggestedPriority;
            this.summary = summary;
        }

        public String getSuggestedCategory() {
            return suggestedCategory;
        }

        public void setSuggestedCategory(String suggestedCategory) {
            this.suggestedCategory = suggestedCategory;
        }

        public String getSuggestedPriority() {
            return suggestedPriority;
        }

        public void setSuggestedPriority(String suggestedPriority) {
            this.suggestedPriority = suggestedPriority;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    CompletableFuture<AiTriageResult> analyzeTicketAsync(Long ticketId, String title, String description);
}
