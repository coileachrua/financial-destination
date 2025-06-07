package com.savingsplanner.util;

import com.savingsplanner.model.SavingsGoal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DialogUtil {

    public static void showResultsDialog(Component parent,
                                         SavingsGoal goal,
                                         int monthsRemaining,
                                         double totalIncome,
                                         double totalExpenses,
                                         double remainingBalance,
                                         double totalAlreadySaved) {

        String text = buildAnalysisText(goal, monthsRemaining, totalIncome,
                totalExpenses, remainingBalance, totalAlreadySaved);

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setCaretPosition(0);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));

        JOptionPane.showMessageDialog(
                parent,
                scrollPane,
                "Savings Goal Analysis",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String buildAnalysisText(SavingsGoal goal,
                                            int monthsRemaining,
                                            double totalIncome,
                                            double totalExpenses,
                                            double remainingBalance,
                                            double totalAlreadySaved) {

        double goalTotal = goal.total();
        double remainingNeed = goalTotal - totalAlreadySaved;

        StringBuilder sb = new StringBuilder();
        LocalDate today = LocalDate.now();
        DateTimeFormatter ukFmt = DateTimeFormatter.ofPattern("d MMMM yyyy");

        sb.append(String.format("Savings Goal Analysis: “%s” (£%,.2f in %d months)%n",
                goal.name(), goalTotal, monthsRemaining));
        sb.append("Date run: ").append(today.format(ukFmt)).append("\n\n");

        sb.append(String.format("1) Total income (all users): £%,.2f%n", totalIncome));
        sb.append(String.format("2) Total expenses (all categories): £%,.2f%n", totalExpenses));
        sb.append(String.format("3) Remaining balance (1 − 2): £%,.2f%n%n", remainingBalance));

        double needs = totalIncome * 0.50;
        double wants = totalIncome * 0.30;
        double savings = totalIncome * 0.20;
        sb.append("4) 50/30/20 rule suggestion:\n");
        sb.append(String.format("   • Needs (50%%): £%,.2f%n", needs));
        sb.append(String.format("   • Wants (30%%): £%,.2f%n", wants));
        sb.append(String.format("   • Savings (20%%): £%,.2f%n%n", savings));

        sb.append(String.format("5) Total already saved (all users): £%,.2f%n", totalAlreadySaved));
        sb.append(String.format("6) Remaining to save (%,.2f − %,.2f): £%,.2f%n",
                goalTotal, totalAlreadySaved, remainingNeed));

        if (remainingBalance <= 0) {
            sb.append("\n→ Remaining balance is zero or negative. You have no funds available to save toward this goal.\n");
        } else {
            double requiredMonthly = (goalTotal - totalAlreadySaved) / monthsRemaining;
        sb.append(String.format("7) Monthly savings required (6 ÷ %d): £%,.2f%n%n",
                    monthsRemaining, requiredMonthly));

            boolean isAchievable = (requiredMonthly <= remainingBalance);
            double monthsNeededIfMax = remainingNeed / remainingBalance;
            int monthsToAchieve = isAchievable
                    ? monthsRemaining
                    : (int) Math.ceil(monthsNeededIfMax);

            LocalDate achievedDate = today.plusMonths(monthsToAchieve);

            if (isAchievable) {
                sb.append(String.format("→ Since £%,.2f ≤ £%,.2f, goal is achievable.%n%n",
                        requiredMonthly, remainingBalance));
                sb.append(String.format("   • You need £%,.2f per month.%n", requiredMonthly));
                sb.append(String.format("   • If you save £%,.2f each month, you’ll hit £%,.2f on: %s%n",
                        requiredMonthly, goalTotal, achievedDate.format(ukFmt)));
                LocalDate fastestDate = today.plusMonths((int) Math.ceil(monthsNeededIfMax));
                sb.append(String.format("   • Saving your entire remaining balance (£%,.2f) hits £%,.2f on: %s%n",
                        remainingBalance, goalTotal, fastestDate.format(ukFmt)));
                double monthsAtTwenty = remainingNeed / savings;
                LocalDate dateAtTwenty = today.plusMonths((int) Math.ceil(monthsAtTwenty));
                sb.append(String.format("   • Saving the 20%% amount (£%,.2f) hits £%,.2f on: %s%n",
                        savings, goalTotal, dateAtTwenty.format(ukFmt)));
            } else {
                sb.append(String.format("→ £%,.2f > £%,.2f, so goal is not achievable.%n%n",
                        requiredMonthly, remainingBalance));
                sb.append(String.format("   • At most, you can save £%,.2f per month.%n", remainingBalance));
                sb.append(String.format("   • To save £%,.2f at £%,.2f/month takes %.2f → %d months.%n",
                        goalTotal, remainingBalance, monthsNeededIfMax, monthsToAchieve));
                sb.append(String.format("   • At £%,.2f/month, you’ll hit £%,.2f on: %s%n",
                        remainingBalance, goalTotal, achievedDate.format(ukFmt)));
                double monthsAtTwenty = remainingNeed / savings;
                LocalDate dateAtTwenty = today.plusMonths((int) Math.ceil(monthsAtTwenty));
                sb.append(String.format("   • Saving the 20%% amount (£%,.2f) hits £%,.2f on: %s%n",
                        savings, goalTotal, dateAtTwenty.format(ukFmt)));
            }
        }

        return sb.toString();
    }
}
