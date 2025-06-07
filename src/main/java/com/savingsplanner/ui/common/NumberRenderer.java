package com.savingsplanner.ui.common;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;

public class NumberRenderer extends DefaultTableCellRenderer {
    private final NumberFormat formatter;

    public NumberRenderer(NumberFormat formatter) {
        this.formatter = formatter;
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Number n) {
            setText(formatter.format(n));
        } else {
            setText("");
        }
    }
}
