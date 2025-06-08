package com.savingsplanner.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoalTemplateTest {

    @Test
    void adjustTotalAddsHouseExtras() {
        double adjusted = GoalTemplate.HOUSE.adjustTotal(20000.0);
        assertEquals(30300.0, adjusted, 0.001);
    }

    @Test
    void breakdownTextContainsLabels() {
        String text = GoalTemplate.WEDDING.buildBreakdownText();
        assertTrue(text.contains("Average Wedding Cost Breakdown"));
        assertTrue(text.contains("Total: £18,000.00"));

        text = GoalTemplate.HOUSE.buildBreakdownText();
        assertTrue(text.contains("House Additional Costs Breakdown"));
        assertTrue(text.contains("Total Extras: £10,300.00"));
    }

    @Test
    void fromNameIsCaseInsensitive() {
        assertTrue(GoalTemplate.fromName("wedding").isPresent());
        assertEquals(GoalTemplate.HOUSE, GoalTemplate.fromName("HOUSE").orElseThrow());
    }
}
