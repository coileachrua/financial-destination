package com.savingsplanner.ui.common;

import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EditableTablePanel extends JPanel {
    private final DefaultTableModel model;
    @Getter
    private final JTable table;

    public EditableTablePanel(String[] columnNames) {
        super(new BorderLayout(5,5));

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);

        int rowHeight = table.getRowHeight();
        int visibleRows = 5;
        Dimension dim = table.getPreferredScrollableViewportSize();
        dim.height = rowHeight * visibleRows;
        table.setPreferredScrollableViewportSize(dim);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void addRow(Object... rowData) {
        model.addRow(rowData);
    }

    public void removeRow(int index) {
        model.removeRow(index);
    }

    public void addTableModelListener(javax.swing.event.TableModelListener l) {
        model.addTableModelListener(l);
    }

}
