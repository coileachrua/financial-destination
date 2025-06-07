package com.savingsplanner.model.persistence;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.User;

import java.util.List;

public record SaveFile(
        List<User> users,
        List<BudgetCategory> expenses,
        List<SavingsGoal> goals) {
}
