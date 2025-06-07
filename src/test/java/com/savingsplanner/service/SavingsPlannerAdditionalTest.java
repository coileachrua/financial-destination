package com.savingsplanner.service;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsPlannerAdditionalTest {

    @Test
    void calculatesRemainingBalance() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 0.0));
        planner.addUser(new User("Bob", 2500.0, 0.0));
        planner.addExpense(new BudgetCategory("Rent", 1000.0));
        planner.addExpense(new BudgetCategory("Food", 400.0));

        assertEquals(4100.0, planner.calculateRemainingBalance(), 0.001);
    }

    @Test
    void calculatesTotalSavingsForGoal() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 200.0));
        planner.addUser(new User("Bob", 2500.0, 300.0));

        assertEquals(500.0, planner.calculateTotalSavingsForGoal(), 0.001);
    }

    @Test
    void suggestedSavingsLimitedByAvailableFunds() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 0.0));
        planner.addExpense(new BudgetCategory("Rent", 2900.0));

        SavingsGoal goal = new SavingsGoal("Car", 5000.0, 10);
        double[] suggestion = planner.calculateSuggestedSavings(goal);

        assertEquals(100.0, suggestion[0], 0.001);
    }

    @Test
    void suggestedSavingsReturnsRequiredWhenAffordable() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 500.0));
        planner.addUser(new User("Bob", 2000.0, 500.0));
        planner.addExpense(new BudgetCategory("Rent", 1500.0));

        SavingsGoal goal = new SavingsGoal("Vacation", 6000.0, 6);
        double[] suggestion = planner.calculateSuggestedSavings(goal);

        assertEquals(833.333, suggestion[0], 0.001);
    }

    @Test
    void setGoalUpdatesExistingGoal() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.setGoal(new SavingsGoal("Vacation", 1000.0, 10));
        planner.setGoal(new SavingsGoal("vacation", 2000.0, 12));

        assertEquals(1, planner.getGoals().size());
        SavingsGoal updated = planner.getGoals().get(0);
        assertEquals(2000.0, updated.total(), 0.001);
        assertEquals(12, updated.months());
    }
}
