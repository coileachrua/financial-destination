package com.savingsplanner.service;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SavingsPlannerTest {

    @Test
    void calculatesTotalIncome() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 0.0));
        planner.addUser(new User("Bob", 2500.0, 0.0));

        assertEquals(5500.0, planner.calculateTotalIncome(), 0.001);
    }

    @Test
    void calculatesTotalExpenses() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addExpense(new BudgetCategory("Rent", 1200.0));
        planner.addExpense(new BudgetCategory("Food", 400.0));

        assertEquals(1600.0, planner.calculateTotalExpenses(), 0.001);
    }

    @Test
    void calculatesMonthlySavingsRequirement() {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 500.0));
        planner.addExpense(new BudgetCategory("Rent", 1000.0));
        planner.addExpense(new BudgetCategory("Utilities", 200.0));

        SavingsGoal goal = new SavingsGoal("Car", 5000.0, 10);

        double required = planner.calculateMonthlySavingsRequired(goal);
        // current savings are 500, so remaining = 4500, over 10 months => 450 per month
        assertEquals(450.0, required, 0.001);
    }
}
