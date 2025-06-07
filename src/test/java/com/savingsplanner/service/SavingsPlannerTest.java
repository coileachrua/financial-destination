package com.savingsplanner.service;

import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsPlannerTest {

    @Test
    void calculateMonthlySavingsRequiresPositiveMonths() {
        SavingsPlanner planner = new SavingsPlanner();
        SavingsGoal goal = new SavingsGoal("Test", 1000, 0);
        assertThrows(IllegalArgumentException.class,
                () -> planner.calculateMonthlySavingsRequired(goal));
    }

    @Test
    void calculateMonthlySavingsReturnsExpectedValue() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 0, 100));
        SavingsGoal goal = new SavingsGoal("Goal", 600, 5);
        double monthly = planner.calculateMonthlySavingsRequired(goal);
        assertEquals(100.0, monthly, 0.0001);
    }

    @Test
    void setGoalRejectsNonPositiveMonths() {
        SavingsPlanner planner = new SavingsPlanner();
        SavingsGoal goal = new SavingsGoal("Invalid", 200, -1);
        assertThrows(IllegalArgumentException.class, () -> planner.setGoal(goal));
    }
}
