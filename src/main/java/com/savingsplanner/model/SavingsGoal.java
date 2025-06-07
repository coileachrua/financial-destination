package com.savingsplanner.model;


public record SavingsGoal(String name, double total, int months) {

    @Override
    public String toString() {
        return String.format("%s – %s over %d months", name, total, months);
    }
}
