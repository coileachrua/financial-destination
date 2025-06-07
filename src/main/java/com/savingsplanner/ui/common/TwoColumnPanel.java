package com.savingsplanner.ui.common;

import javax.swing.*;
import java.awt.*;

public class TwoColumnPanel extends JPanel {
    private final JPanel right;

    public TwoColumnPanel(JComponent leftComponent, JButton... rightButtons) {
        super(new BorderLayout(10,10));

        // LEFT
        add(leftComponent, BorderLayout.CENTER);

        // RIGHT
        right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        for (int i = 0; i < rightButtons.length; i++) {
            JButton b = rightButtons[i];
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            right.add(b);
            if (i < rightButtons.length - 1) {
                right.add(Box.createVerticalStrut(10));
            }
        }
        add(right, BorderLayout.EAST);
    }

    /** Add a footer component under the buttons (with spacing). */
    public void addFooter(JComponent footer) {
        right.add(Box.createVerticalStrut(15));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(footer);
    }
}
