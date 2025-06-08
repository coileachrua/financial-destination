package com.savingsplanner.service;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceServiceTest {

    @Test
    void saveAndLoadRestoresData(@TempDir Path tempDir) {
        Preferences prefs = Preferences.userNodeForPackage(PersistenceService.class);
        String path = tempDir.resolve("data.json").toString();
        prefs.put("dataFilePath", path);
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
            prefs.remove("dataFilePath");
        }
    }
}
