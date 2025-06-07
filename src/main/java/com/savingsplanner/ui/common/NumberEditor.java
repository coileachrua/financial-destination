package com.savingsplanner.ui.common;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class NumberEditor extends DefaultCellEditor {
    public NumberEditor(NumberFormat format) {
        super(new JFormattedTextField(new NumberFormatter(format)));
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        ftf.setHorizontalAlignment(SwingConstants.RIGHT);
        ftf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    }
}
