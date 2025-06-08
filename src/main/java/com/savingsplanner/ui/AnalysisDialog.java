package com.savingsplanner.ui;

import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.util.DialogUtil;
import com.savingsplanner.util.PlanType;

import javax.swing.*;
import java.awt.*;

/** Dialog showing savings analysis with selectable plans. */
public class AnalysisDialog extends JDialog {

    private final JTextArea textArea = new JTextArea();
    private final SavingsPlanner planner;
    private final SavingsGoal goal;
    private final JSlider splitSlider = new JSlider(0, 50, 20);
    private final JLabel splitLabel = new JLabel();
    private GraphPanel graphPanel;

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
        setLayout(new BorderLayout(5,5));

        JRadioButton req = new JRadioButton("Required for Goal");
        JRadioButton max = new JRadioButton("Max Savings");
        JRadioButton sug = new JRadioButton("50/30/20");
        ButtonGroup group = new ButtonGroup();
        group.add(req); group.add(max); group.add(sug);
        req.setSelected(true);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT));
        options.add(req); options.add(max); options.add(sug);
        add(options, BorderLayout.NORTH);

        splitSlider.setMajorTickSpacing(10);
        splitSlider.setMinorTickSpacing(1);
        splitSlider.setPaintTicks(true);
        splitSlider.addChangeListener(e -> {
            splitLabel.setText("Savings " + splitSlider.getValue() + "% / Wants "
                    + (50 - splitSlider.getValue()) + "%");
            if (sug.isSelected()) updateText(PlanType.SUGGESTED);
        });
        splitLabel.setText("Savings 20% / Wants 30%");
        splitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        splitSlider.setEnabled(false);
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(splitLabel);
        sliderPanel.add(splitSlider);
        add(sliderPanel, BorderLayout.WEST);

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(550, 500));
        add(scroll, BorderLayout.CENTER);

        graphPanel = new GraphPanel(planner, goal, goal.months(), splitSlider.getValue() / 100.0);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> dispose());
        JPanel south = new JPanel(new BorderLayout());
        south.add(graphPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okBtn);
        south.add(buttonPanel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        req.addActionListener(e -> {splitSlider.setEnabled(false); updateText(PlanType.REQUIRED);});
        max.addActionListener(e -> {splitSlider.setEnabled(false); updateText(PlanType.MAX);});
        sug.addActionListener(e -> {splitSlider.setEnabled(true); updateText(PlanType.SUGGESTED);});
    }

    private void updateText(PlanType type) {
        double income = planner.calculateTotalIncome();
        double expenses = planner.calculateTotalExpenses();
        double balance = planner.calculateRemainingBalance();
        double saved = planner.calculateTotalSavingsForGoal();
        int savingsPct = splitSlider.getValue();
        String text = DialogUtil.buildPlanAnalysisText(goal, goal.months(), income,
                expenses, balance, saved, type, savingsPct / 100.0);
        textArea.setText(text);
        textArea.setCaretPosition(0);
        if (graphPanel != null) {
            graphPanel.updateData(savingsPct / 100.0);
        }
    }

    public static void showDialog(JFrame parent, SavingsPlanner planner, SavingsGoal goal) {
        new AnalysisDialog(parent, planner, goal);
    }
}
