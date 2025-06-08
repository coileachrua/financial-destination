package com.savingsplanner.service;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.model.GoalTemplate;
import com.savingsplanner.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SavingsPlanner implements Serializable {
    private final List<User> users = new ArrayList<>();
    private final List<BudgetCategory> expenses = new ArrayList<>();
    private final List<SavingsGoal> goals = new ArrayList<>();

    public void addUser(User u) {
        users.add(u);
    }

    public void removeUser(int i) {
        if (i >= 0 && i < users.size()) users.remove(i);
    }

    public void updateUser(int index, User newUser) {
        if (index >= 0 && index < users.size()) {
            users.set(index, newUser);
        }
    }


    public List<User> getUsers() {
        return List.copyOf(users);
    }

    public void addExpense(BudgetCategory c) {
        expenses.add(c);
    }

    public void updateExpense(int i, BudgetCategory c) {
        if (i >= 0 && i < expenses.size()) expenses.set(i, c);
    }

    public void removeExpense(int i) {
        if (i >= 0 && i < expenses.size()) expenses.remove(i);
    }

    public List<BudgetCategory> getExpenses() {
        return List.copyOf(expenses);
    }

    public void setGoal(SavingsGoal g) {
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).name().equalsIgnoreCase(g.name())) {
                goals.set(i, g);
                return;
            }
        }
        goals.add(g);
    }

    public void removeGoal(int i) {
        if (i >= 0 && i < goals.size()) goals.remove(i);
    }

    public List<SavingsGoal> getGoals() {
        return List.copyOf(goals);
    }

    public void clearAll() {
        users.clear();
        expenses.clear();
        goals.clear();
    }

    public double calculateTotalIncome() {
        return users.stream().mapToDouble(User::income).sum();
    }

    public double calculateTotalExpenses() {
        return expenses.stream().mapToDouble(BudgetCategory::total).sum();
    }

    public double calculateRemainingBalance() {
        return calculateTotalIncome() - calculateTotalExpenses();
    }

    public double calculateTotalSavingsForGoal() {
        return users.stream().mapToDouble(User::currentSavings).sum();
    }

    public double calculateMonthlySavingsRequired(SavingsGoal goal) {
        double adjusted = GoalTemplate.adjustedTotalFor(goal);
        double remaining = adjusted - calculateTotalSavingsForGoal();
        return remaining / goal.months();
    }

    public double[] calculateSuggestedSavings(SavingsGoal goal) {
        double req = calculateMonthlySavingsRequired(goal);
        double avail = calculateRemainingBalance();
        return req > avail ? new double[]{avail} : new double[]{req};
    }
}
