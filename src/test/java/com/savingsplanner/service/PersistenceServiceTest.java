package com.savingsplanner.service;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceServiceTest {

    @Test
    void saveAndLoadRestoresData(@TempDir Path tempDir) {
        String originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            SavingsPlanner planner = new SavingsPlanner();
            planner.addUser(new User("Alice", 3000.0, 500.0));
            planner.addExpense(new BudgetCategory("Rent", 1000.0));
            planner.setGoal(new SavingsGoal("Vacation", 2000.0, 12));

            PersistenceService ps = new PersistenceService();
            ps.save(planner);

            SavingsPlanner loaded = new SavingsPlanner();
            ps.load(loaded);

            assertEquals(planner.getUsers(), loaded.getUsers());
            assertEquals(planner.getExpenses(), loaded.getExpenses());
            assertEquals(planner.getGoals(), loaded.getGoals());
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }
}
