package com.savingsplanner.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Pre-defined savings goal templates with typical cost breakdowns.
 */
public enum GoalTemplate {
    WEDDING("Wedding", new LinkedHashMap<>() {{
        put("Venue", 8000d);
        put("Catering", 5000d);
        put("Photography", 2000d);
        put("Dress & Attire", 1500d);
        put("Miscellaneous", 1500d);
    }}),
    HOUSE("House", new LinkedHashMap<>() {{
        put("Stamp Duty", 4000d);
        put("Legal Fees", 1500d);
        put("Survey", 600d);
        put("Moving Costs", 1200d);
        put("Furnishings", 3000d);
    }});

    private final String label;
    private final Map<String, Double> breakdown;
    private final double total;

    GoalTemplate(String label, Map<String, Double> breakdown) {
        this.label = label;
        this.breakdown = new LinkedHashMap<>(breakdown);
        double sum = 0;
        for (double v : breakdown.values()) sum += v;
        this.total = sum;
    }

    public String label() {
        return label;
    }

    public double total() {
        return total;
    }

    public Map<String, Double> getBreakdown() {
        return Map.copyOf(breakdown);
    }

    /**
     * Lookup a template by name ignoring case.
     */
    public static Optional<GoalTemplate> fromName(String name) {
        for (GoalTemplate t : values()) {
            if (t.label.equalsIgnoreCase(name)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * Build a formatted text block describing the template breakdown.
     */
    public String buildBreakdownText() {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(" Cost Breakdown:\n");
        for (Map.Entry<String, Double> e : breakdown.entrySet()) {
            sb.append(String.format(" - %s: £%,.2f%n", e.getKey(), e.getValue()));
        }
        sb.append(String.format("Total: £%,.2f%n", total));
        return sb.toString();
    }
}
