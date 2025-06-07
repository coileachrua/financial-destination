package com.savingsplanner.ui;

import com.savingsplanner.model.User;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.ui.common.EditableTablePanel;
import com.savingsplanner.ui.common.NumberEditor;
import com.savingsplanner.ui.common.NumberRenderer;
import com.savingsplanner.ui.common.TwoColumnPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UserPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 2412462265L;


    public UserPanel(SavingsPlanner planner,
                     PersistenceService persistence) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Users",
                TitledBorder.LEFT, TitledBorder.TOP));

        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.UK);

        EditableTablePanel tablePanel = new EditableTablePanel(
                new String[]{"Name", "Income", "Saved"});
        JTable table = tablePanel.getTable();
        for (int col : new int[]{1, 2}) {
            table.getColumnModel()
                    .getColumn(col)
                    .setCellRenderer(new NumberRenderer(fmt));
            table.getColumnModel()
                    .getColumn(col)
                    .setCellEditor(new NumberEditor(fmt));
        }

        for (User u : planner.getUsers()) {
            tablePanel.addRow(u.name(), u.income(), u.currentSavings());
        }

        JPanel inputs = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField incomeField = new JTextField();
        JTextField savedField = new JTextField();
        inputs.add(new JLabel("Name:"));
        inputs.add(nameField);
        inputs.add(new JLabel("Income (" + "£" + "):"));
        inputs.add(incomeField);
        inputs.add(new JLabel("Saved (" + "£" + "):"));
        inputs.add(savedField);

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.add(inputs, BorderLayout.NORTH);
        left.add(tablePanel, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel();
        updateTotalLabel(planner, totalLabel);

        tablePanel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                Object nameObj = table.getValueAt(row, 0);
                Object incomeObj = table.getValueAt(row, 1);
                Object savedObj = table.getValueAt(row, 2);
                try {
                    String nm = nameObj.toString().trim();
                    double inc = parseCell(incomeObj, fmt);
                    double sav = parseCell(savedObj, fmt);
                    planner.updateUser(row, new User(nm, inc, sav));
                    persistence.save(planner);
                    updateTotalLabel(planner, totalLabel);
                    log.info("Updated user {}", nm);
                } catch (Exception ex) {
                    log.error("Failed to update user", ex);
                    JOptionPane.showMessageDialog(
                            this, "Invalid data", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton addBtn = new JButton("Add User");
        JButton removeBtn = new JButton("Remove Selected");

        addBtn.addActionListener((ActionEvent e) -> {
            try {
                String nm = nameField.getText().trim();
                double inc = Double.parseDouble(incomeField.getText().trim());
                double sav = Double.parseDouble(savedField.getText().trim());
                if (nm.isEmpty()) throw new IllegalArgumentException();
                planner.addUser(new User(nm, inc, sav));
                persistence.save(planner);
                tablePanel.addRow(nm, inc, sav);
                nameField.setText("");
                incomeField.setText("");
                savedField.setText("");
                updateTotalLabel(planner, totalLabel);
                log.info("Added user {}", nm);
            } catch (Exception ex) {
                log.error("Failed to add user", ex);
                JOptionPane.showMessageDialog(
                        this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        removeBtn.addActionListener(e -> {
            int idx = table.getSelectedRow();
            if (idx >= 0) {
                planner.removeUser(idx);
                persistence.save(planner);
                tablePanel.removeRow(idx);
                updateTotalLabel(planner, totalLabel);
                log.info("Removed user at row {}", idx);
            }
        });

        TwoColumnPanel container = new TwoColumnPanel(left, addBtn, removeBtn);
        container.addFooter(totalLabel);
        add(container, BorderLayout.CENTER);
    }

    private double parseCell(Object value, NumberFormat fmt) throws Exception {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return fmt.parse(value.toString().trim()).doubleValue();
    }

    private void updateTotalLabel(SavingsPlanner planner, JLabel lbl) {
        double total = planner.calculateTotalIncome();
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.UK);
        lbl.setText("Total Income: " + fmt.format(total));
    }
}
