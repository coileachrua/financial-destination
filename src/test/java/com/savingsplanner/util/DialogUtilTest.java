package com.savingsplanner.util;

import com.savingsplanner.model.SavingsGoal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogUtilTest {

    @Test
    void requiredPlanWarnsWhenUnachievable() {
        SavingsGoal goal = new SavingsGoal("Car", 5000.0, 1);
        String text = DialogUtil.buildPlanAnalysisText(goal, 1,
                2000.0, 1000.0, 1000.0, 0.0, PlanType.REQUIRED);
        assertTrue(text.contains("Required contribution exceeds"));
    }

    @Test
    void taxYearScheduleSpansMultipleYears() throws Exception {
        var m = DialogUtil.class.getDeclaredMethod(
                "buildTaxYearSchedule",
                java.time.LocalDate.class, double.class, double.class,
                com.savingsplanner.model.SavingsGoal.class, int.class);
        m.setAccessible(true);
        String schedule = (String) m.invoke(null,
                java.time.LocalDate.of(2025,1,1), 0.0, 1000.0,
                new com.savingsplanner.model.SavingsGoal("Goal", 5000.0, 10), 16);
        assertTrue(schedule.contains("2025/2026"));
        assertTrue(schedule.contains("2026/2027"));
    }
}
