package com.helpdesk.sys.service.impl;

import com.helpdesk.sys.entity.Category;
import com.helpdesk.sys.entity.Priority;
import com.helpdesk.sys.entity.Ticket;
import com.helpdesk.sys.repository.CategoryRepository;
import com.helpdesk.sys.repository.TicketRepository;
import com.helpdesk.sys.service.AiTriageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class GeminiAiTriageServiceImpl implements AiTriageService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAiTriageServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiAiTriageServiceImpl(TicketRepository ticketRepository,
                                     CategoryRepository categoryRepository) {
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Async("aiTaskExecutor")
    @Transactional
    public CompletableFuture<AiTriageResult> analyzeTicketAsync(Long ticketId, String title, String description) {
        logger.info("Starting Async Gen AI Triage for Ticket ID: {} on thread {}", ticketId, Thread.currentThread().getName());

        AiTriageResult result;
        if (apiKey != null && !apiKey.trim().isEmpty() && !"demo_key".equalsIgnoreCase(apiKey)) {
            result = callGeminiApi(title, description);
        } else {
            logger.warn("Gemini API Key not set. Falling back to local heuristic Gen AI simulation for Ticket ID: {}", ticketId);
            result = heuristicTriageFallback(title, description);
        }

        // Apply AI Triage updates back to Ticket entity asynchronously
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setAiSuggestedCategory(result.getSuggestedCategory());
            ticket.setAiSuggestedPriority(result.getSuggestedPriority());
            ticket.setAiSummary(result.getSummary());

            // If user did not manually specify category, auto-apply AI suggested category
            if (ticket.getCategory() == null && result.getSuggestedCategory() != null) {
                Optional<Category> categoryOpt = categoryRepository.findByName(result.getSuggestedCategory().toUpperCase());
                categoryOpt.ifPresent(ticket::setCategory);
            }

            // If user priority was LOW default, set to AI suggested priority
            if ((ticket.getPriority() == null || ticket.getPriority() == Priority.LOW) && result.getSuggestedPriority() != null) {
                try {
                    ticket.setPriority(Priority.valueOf(result.getSuggestedPriority().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }

            ticketRepository.save(ticket);
            logger.info("Successfully updated Ticket ID: {} with Async AI Triage insights", ticketId);
        }

        return CompletableFuture.completedFuture(result);
    }

    private AiTriageResult callGeminiApi(String title, String description) {
        try {
            String prompt = String.format(
                    "You are an IT Helpdesk Gen AI Assistant. Analyze the following ticket title and description.\n" +
                    "Title: %s\nDescription: %s\n\n" +
                    "Respond EXACTLY in this JSON format without markdown ticks:\n" +
                    "{\"suggestedCategory\": \"HARDWARE|SOFTWARE|NETWORK|ACCESS\", \"suggestedPriority\": \"LOW|MEDIUM|HIGH|CRITICAL\", \"summary\": \"1-2 sentence concise summary\"}",
                    title, description
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> contentsPart = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(contentsPart));

            String fullUrl = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parse response body structure
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    String text = (String) firstPart.get("text");

                    return parseAiResponseJson(text, title, description);
                }
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API for ticket triage: {}", e.getMessage());
        }

        return heuristicTriageFallback(title, description);
    }

    private AiTriageResult parseAiResponseJson(String rawText, String title, String description) {
        try {
            String cleanJson = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
            // Basic parsing logic
            String cat = extractJsonField(cleanJson, "suggestedCategory", "SOFTWARE");
            String prio = extractJsonField(cleanJson, "suggestedPriority", "MEDIUM");
            String sum = extractJsonField(cleanJson, "summary", title + ": " + description);
            return new AiTriageResult(cat, prio, sum);
        } catch (Exception e) {
            return heuristicTriageFallback(title, description);
        }
    }

    private String extractJsonField(String json, String fieldName, String defaultValue) {
        String key = "\"" + fieldName + "\"";
        int idx = json.indexOf(key);
        if (idx == -1) return defaultValue;
        int colonIdx = json.indexOf(":", idx);
        int startQuote = json.indexOf("\"", colonIdx);
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (startQuote != -1 && endQuote != -1) {
            return json.substring(startQuote + 1, endQuote);
        }
        return defaultValue;
    }

    private AiTriageResult heuristicTriageFallback(String title, String description) {
        String combined = (title + " " + description).toLowerCase();

        String category = "SOFTWARE";
        if (combined.contains("laptop") || combined.contains("monitor") || combined.contains("keyboard") || combined.contains("mouse") || combined.contains("printer") || combined.contains("hardware")) {
            category = "HARDWARE";
        } else if (combined.contains("vpn") || combined.contains("wifi") || combined.contains("internet") || combined.contains("dns") || combined.contains("network")) {
            category = "NETWORK";
        } else if (combined.contains("password") || combined.contains("login") || combined.contains("access") || combined.contains("permission") || combined.contains("sso")) {
            category = "ACCESS";
        }

        String priority = "LOW";
        if (combined.contains("urgent") || combined.contains("critical") || combined.contains("down") || combined.contains("crash") || combined.contains("broken")) {
            priority = "CRITICAL";
        } else if (combined.contains("error") || combined.contains("failed") || combined.contains("high")) {
            priority = "HIGH";
        } else if (combined.contains("slow") || combined.contains("issue") || combined.contains("medium")) {
            priority = "MEDIUM";
        }

        String summary = String.format("Auto Triage: Issue regarding %s with estimated %s priority. Request: %s", category, priority, title);
        return new AiTriageResult(category, priority, summary);
    }
}
