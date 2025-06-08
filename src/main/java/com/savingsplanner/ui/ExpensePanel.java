package com.savingsplanner.ui;

import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.ui.common.EditableTablePanel;
import com.savingsplanner.ui.common.NumberEditor;
import com.savingsplanner.ui.common.NumberRenderer;
import com.savingsplanner.ui.common.TwoColumnPanel;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;

@Log4j2
public class ExpensePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 157843535L;

    public ExpensePanel(SavingsPlanner planner,
                        PersistenceService persistence) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Expenses"));

        EditableTablePanel tablePanel = createTablePanel(planner);
        JTable table = tablePanel.getTable();

        JPanel inputs = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField totalField = new JTextField();
        inputs.add(new JLabel("Category:"));
        inputs.add(nameField);
        inputs.add(new JLabel("Total (" + "£" + "):"));
        inputs.add(totalField);

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.add(inputs, BorderLayout.NORTH);
        left.add(tablePanel, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel();
        updateTotalLabel(planner, totalLabel);

        tablePanel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int r = e.getFirstRow();
                Object catObj = table.getValueAt(r, 0);
                Object totObj = table.getValueAt(r, 1);
                try {
                    String nm = catObj.toString().trim();
                    double tot = parseCell(totObj);
                    // ensure the table stores a numeric value so it renders correctly
                    if (!(totObj instanceof Number)) {
                        table.setValueAt(tot, r, 1);
                    }
                    planner.updateExpense(r, new BudgetCategory(nm, tot));
                    persistence.save(planner);
                    updateTotalLabel(planner, totalLabel);
                } catch (Exception ex) {
                    log.error("Failed to update expense", ex);
                    JOptionPane.showMessageDialog(
                            this, "Invalid data", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton addBtn = new JButton("Add Expense");
        JButton removeBtn = new JButton("Remove Selected");

        addBtn.addActionListener((ActionEvent e) -> {
            try {
                String nm = nameField.getText().trim();
                double tot = parseCell(totalField);
                if (nm.isEmpty()) throw new IllegalArgumentException();
                planner.addExpense(new BudgetCategory(nm, tot));
                persistence.save(planner);
                tablePanel.addRow(nm, tot);
                nameField.setText("");
                totalField.setText("");
                updateTotalLabel(planner, totalLabel);
                log.info("Added expense {} = {}", nm, tot);
            } catch (Exception ex) {
                log.error("Failed to add expense", ex);
                JOptionPane.showMessageDialog(
                        this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        removeBtn.addActionListener(e -> {
            int idx = table.getSelectedRow();
            if (idx >= 0) {
                planner.removeExpense(idx);
                persistence.save(planner);
                tablePanel.removeRow(idx);
                updateTotalLabel(planner, totalLabel);
                log.info("Removed expense at row {}", idx);
            }
        });

        TwoColumnPanel container = new TwoColumnPanel(left, addBtn, removeBtn);
        container.addFooter(totalLabel);
        add(container, BorderLayout.CENTER);
    }

    private double parseCell(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.UK);
        String txt = value.toString().trim();
        try {
            return fmt.parse(txt).doubleValue();
        } catch (Exception ex) {
            return Double.parseDouble(txt.replace(".0", ""));
        }
    }

    private EditableTablePanel createTablePanel(SavingsPlanner planner) {
        EditableTablePanel tablePanel = new EditableTablePanel(
                new String[]{"Category", "Total"});
        JTable table = tablePanel.getTable();
        table.getColumnModel()
                .getColumn(1)
                .setCellRenderer(new NumberRenderer(NumberFormat.getCurrencyInstance(Locale.UK)));
        table.getColumnModel()
                .getColumn(1)
                .setCellEditor(new NumberEditor(NumberFormat.getCurrencyInstance(Locale.UK)));
        for (BudgetCategory bc : planner.getExpenses()) {
            tablePanel.addRow(bc.name(), bc.total());
        }
        return tablePanel;
    }

    private void updateTotalLabel(SavingsPlanner planner, JLabel lbl) {
        double total = planner.calculateTotalExpenses();
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.UK);
        lbl.setText("Total Expenses: " + fmt.format(total));
    }
}
