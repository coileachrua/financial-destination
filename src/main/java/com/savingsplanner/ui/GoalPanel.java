package com.savingsplanner.ui;

import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GoalPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 6725257L;

    private final SavingsPlanner planner;
    private final PersistenceService persistence;
    private final JComboBox<SavingsGoal> selector = new JComboBox<>();
    private final JTextField nameField   = new JTextField();
    private final JTextField totalField  = new JTextField();
    private final JTextField monthsField = new JTextField();
    private final JButton saveBtn        = new JButton("Save Goal");
    private final JButton removeBtn      = new JButton("Remove Selected");

    public GoalPanel(SavingsPlanner planner,
                     PersistenceService persistence) {
        this.planner    = planner;
        this.persistence = persistence;

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Savings Goals", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel left = buildLeftPanel();
        JPanel right = buildRightPanel();

        removeBtn.setEnabled(!planner.getGoals().isEmpty());
        if (!planner.getGoals().isEmpty()) {
            selector.setSelectedIndex(0);
            populateFields();
        }

        add(left,  BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(5,5));
        selector.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx, sel, focus);
                if (value instanceof SavingsGoal g) {
                    setText(String.format("%s – £%,.0f over %d mo.", g.name(), g.total(), g.months()));
                }
                return this;
            }
        });
        refreshSelector();
        selector.addActionListener(e -> populateFields());
        left.add(selector, BorderLayout.NORTH);

        JPanel inputs = new JPanel(new GridLayout(3,2,5,5));
        inputs.add(new JLabel("Goal Name:"));    inputs.add(nameField);
        inputs.add(new JLabel("Total (£):"));     inputs.add(totalField);
        inputs.add(new JLabel("Months:"));        inputs.add(monthsField);
        left.add(inputs, BorderLayout.CENTER);
        return left;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(saveBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(removeBtn);

        saveBtn.addActionListener(this::onSaveGoal);
        removeBtn.addActionListener(this::onRemoveGoal);
        return right;
    }

    private void refreshSelector() {
        selector.removeAllItems();
        for (SavingsGoal g : planner.getGoals()) selector.addItem(g);
    }

    private void populateFields() {
        SavingsGoal g = (SavingsGoal) selector.getSelectedItem();
        boolean has = (g != null);
        removeBtn.setEnabled(has);
        nameField  .setText(has ? g.name()   : "");
        totalField .setText(has ? String.valueOf(g.total())  : "");
        monthsField.setText(has ? String.valueOf(g.months()) : "");
    }

    private void onSaveGoal(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            double total  = Double.parseDouble(totalField.getText().trim());
            int    months = Integer.parseInt(monthsField.getText().trim());
            if (name.isEmpty() || months <= 0) throw new IllegalArgumentException();

            SavingsGoal g = new SavingsGoal(name, total, months);
            planner.setGoal(g);
            persistence.save(planner);
            log.info("Saved goal {} = {} over {} months", name, total, months);

            refreshSelector();
            selector.setSelectedItem(g);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a non-empty name, numeric total, and months>0.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveGoal(ActionEvent e) {
        int idx = selector.getSelectedIndex();
        if (idx < 0) return;
        SavingsGoal g = (SavingsGoal) selector.getSelectedItem();
        int choice = JOptionPane.showConfirmDialog(
                this, "Remove “" + g.name() + "”?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        planner.removeGoal(idx);
        persistence.save(planner);
        log.info("Removed goal {}", g.name());
        refreshSelector();
        if (selector.getItemCount()>0) {
            selector.setSelectedIndex(0);
            populateFields();
        } else {
            nameField.setText("");
            totalField.setText("");
            monthsField.setText("");
        }
    }

    public SavingsGoal getSelectedGoal() {
        return (SavingsGoal) selector.getSelectedItem();
    }
}
