package com.savingsplanner.util;

/** Plan options for savings analysis. */
public enum PlanType {
    /** Required monthly contribution to meet the goal's deadline. */
    REQUIRED,
    /** Maximum possible monthly savings based on current balance. */
    MAX,
    /** 50/30/20 suggested monthly savings (20% of income). */
    SUGGESTED
}
