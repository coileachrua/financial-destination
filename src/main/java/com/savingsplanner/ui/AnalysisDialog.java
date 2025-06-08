package com.savingsplanner.ui;

import com.savingsplanner.model.GoalTemplate;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.util.DialogUtil;
import com.savingsplanner.util.PlanType;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog showing savings analysis with selectable plans.
 */
public class AnalysisDialog extends JDialog {

    private final JTextArea textArea = new JTextArea();
    private final SavingsPlanner planner;
    private final SavingsGoal goal;

    private AnalysisDialog(JFrame parent, SavingsPlanner planner, SavingsGoal goal) {
        super(parent, "Savings Goal Analysis", true);
        this.planner = planner;
        this.goal = goal;
        buildUI();
        updateText(PlanType.REQUIRED);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));

        JRadioButton req = new JRadioButton("Required for Goal");
        JRadioButton max = new JRadioButton("Max Savings");
        JRadioButton sug = new JRadioButton("50/30/20");
        ButtonGroup group = new ButtonGroup();
        group.add(req);
        group.add(max);
        group.add(sug);
        req.setSelected(true);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT));
        options.add(req);
        options.add(max);
        options.add(sug);
        add(options, BorderLayout.NORTH);

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(550, 500));
        add(scroll, BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> dispose());
        JPanel south = new JPanel();
        south.add(okBtn);
        add(south, BorderLayout.SOUTH);

        req.addActionListener(e -> updateText(PlanType.REQUIRED));
        max.addActionListener(e -> updateText(PlanType.MAX));
        sug.addActionListener(e -> updateText(PlanType.SUGGESTED));
    }

    private void updateText(PlanType type) {
        double income = planner.calculateTotalIncome();
        double expenses = planner.calculateTotalExpenses();
        double balance = planner.calculateRemainingBalance();
        double saved = planner.calculateTotalSavingsForGoal();
        final String[] text = {DialogUtil.buildPlanAnalysisText(goal, goal.months(), income,
                expenses, balance, saved, type)};

        GoalTemplate.fromName(goal.name()).ifPresent(t -> {
            String breakdown = t.buildBreakdownText();
            if (t == GoalTemplate.HOUSE) {
                int idx = text[0].indexOf("Total expenses");
                if (idx >= 0) {
                    int end = text[0].indexOf('\n', idx);
                    if (end >= 0) {
                        text[0] = text[0].substring(0, end + 1) + breakdown +
                                text[0].substring(end + 1);
                    } else {
                        text[0] = text[0] + "\n" + breakdown;
                    }
                } else {
                    text[0] = text[0] + "\n" + breakdown;
                }
            } else {
                text[0] = text[0] + "\n" + breakdown;
            }
        });

        textArea.setText(text[0]);
        textArea.setCaretPosition(0);
    }

    public static void showDialog(JFrame parent, SavingsPlanner planner, SavingsGoal goal) {
        new AnalysisDialog(parent, planner, goal);
    }
}
