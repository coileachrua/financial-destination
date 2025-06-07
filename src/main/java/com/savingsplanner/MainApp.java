package com.savingsplanner;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.savingsplanner.model.User;
import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.ui.SettingsDialog;
import com.savingsplanner.ui.UserPanel;
import com.savingsplanner.ui.ExpensePanel;
import com.savingsplanner.ui.GoalPanel;
import com.savingsplanner.ui.GraphPanel;
import com.savingsplanner.util.DialogUtil;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.prefs.Preferences;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

@Log4j2
public class MainApp {

    private static final Preferences PREFS = Preferences.userNodeForPackage(MainApp.class);

    public static void applyTheme(boolean dark, JFrame frame) {
        try {
            if (dark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(frame);
            PREFS.putBoolean("darkMode", dark);
        } catch (Exception ex) {
            log.error("Failed to apply theme", ex);
        }
    }

    public static void main(String[] args) {
        boolean dark = PREFS.getBoolean("darkMode", true);
        if (dark) FlatDarkLaf.setup(); else FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            SavingsPlanner planner    = new SavingsPlanner();
            PersistenceService ps     = new PersistenceService();
            ps.load(planner);

            if (planner.getUsers().isEmpty()) {
                planner.addUser(new User("Josh", 6800.0, 0.0));
            }

            JFrame frame = new JFrame("Savings Planner");
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenuItem settingsItem = new JMenuItem("Settings...");
            fileMenu.add(settingsItem);
            menuBar.add(fileMenu);
            frame.setJMenuBar(menuBar);

            settingsItem.addActionListener(e -> SettingsDialog.showDialog(frame, ps));

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
            calculateAll.addActionListener(e ->
                    showGoalAnalysis(frame, main, planner)
            );
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

    private static void showGoalAnalysis(JFrame frame, JPanel main, SavingsPlanner planner) {
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
    }
}
