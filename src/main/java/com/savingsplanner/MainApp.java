package com.savingsplanner;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.savingsplanner.model.User;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.ui.UserPanel;
import com.savingsplanner.ui.ExpensePanel;
import com.savingsplanner.ui.GoalPanel;
import com.savingsplanner.ui.GraphPanel;
import com.savingsplanner.util.DialogUtil;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

@Log4j2
public class MainApp {
    public static void main(String[] args) {
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            SavingsPlanner planner    = new SavingsPlanner();
            PersistenceService ps     = new PersistenceService("savings_data.json");
            ps.load(planner);

            if (planner.getUsers().isEmpty()) {
                planner.addUser(new User("Josh", 6800.0, 0.0));
            }

            JFrame frame = new JFrame("Savings Planner");
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();
            JMenu view = new JMenu("View");
            JCheckBoxMenuItem darkModeItem = new JCheckBoxMenuItem("Dark Mode", true);
            view.add(darkModeItem);
            menuBar.add(view);
            frame.setJMenuBar(menuBar);

            darkModeItem.addActionListener(e -> {
                try {
                    if (darkModeItem.isSelected()) {
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                    } else {
                        UIManager.setLookAndFeel(new FlatLightLaf());
                    }
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception ex) {
                    log.error("Dark mode toggle failed", ex);
                }
            });

            JPanel main = new JPanel();
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
            main.setBorder(new EmptyBorder(10, 10, 10, 10));

            main.add(new UserPanel(planner, ps));
            main.add(Box.createVerticalStrut(10));
            main.add(new ExpensePanel(planner, ps));
            main.add(Box.createVerticalStrut(10));
            main.add(new GoalPanel(planner, ps));
            main.add(Box.createVerticalStrut(20));

            JButton calculateAll = new JButton("Calculate & Show Analysis");
            calculateAll.setAlignmentX(Component.CENTER_ALIGNMENT);
            calculateAll.addActionListener(e -> {
                SavingsGoal goal = ((GoalPanel) main.getComponent(4)).getSelectedGoal();
                if (goal == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Please select or save a goal first.",
                            "No Goal", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int months       = goal.months();
                double income    = planner.calculateTotalIncome();
                double expenses  = planner.calculateTotalExpenses();
                double balance   = planner.calculateRemainingBalance();
                double saved     = planner.calculateTotalSavingsForGoal();

                DialogUtil.showResultsDialog(
                        frame, goal, months, income, expenses, balance, saved
                );

                GraphPanel chart = new GraphPanel(planner, goal, months);
                JDialog dlg = new JDialog(frame, "Savings Trajectory", true);
                dlg.getContentPane().add(chart);
                dlg.pack();
                dlg.setLocationRelativeTo(frame);
                dlg.setVisible(true);
            });
            main.add(calculateAll);
            main.add(Box.createVerticalGlue());

            JScrollPane scroller = new JScrollPane(main);
            scroller.setPreferredSize(new Dimension(700, 800));
            scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            frame.getContentPane().add(scroller);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> ps.save(planner))
            );
        });
    }
}
