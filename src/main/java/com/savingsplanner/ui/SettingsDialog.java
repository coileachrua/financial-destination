package com.savingsplanner.ui;

import com.savingsplanner.MainApp;
import com.savingsplanner.service.PersistenceService;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class SettingsDialog extends JDialog {
    private final JTextField pathField = new JTextField(20);
    private final JCheckBox darkMode = new JCheckBox("Dark mode");
    private final Preferences prefs = Preferences.userNodeForPackage(SettingsDialog.class);
    private final PersistenceService persistence;
    private final JFrame owner;

    public SettingsDialog(JFrame owner, PersistenceService ps) {
        super(owner, "Settings", true);
        this.owner = owner;
        this.persistence = ps;

        setLayout(new BorderLayout(10, 10));
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createPathPanel());
        center.add(Box.createVerticalStrut(10));
        darkMode.setSelected(prefs.getBoolean("darkMode", true));
        center.add(darkMode);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        buttons.add(ok);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        ok.addActionListener(e -> applyAndClose());
        cancel.addActionListener(e -> setVisible(false));

        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel createPathPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Data file:"), BorderLayout.WEST);
        pathField.setText(prefs.get("dataFilePath", "savings_data.json"));
        panel.add(pathField, BorderLayout.CENTER);
        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(pathField.getText());
            int res = fc.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                pathField.setText(fc.getSelectedFile().getPath());
            }
        });
        panel.add(browse, BorderLayout.EAST);
        return panel;
    }

    private void applyAndClose() {
        String path = pathField.getText().trim();
        prefs.put("dataFilePath", path);
        prefs.putBoolean("darkMode", darkMode.isSelected());
        persistence.setFilePath(path);
        MainApp.applyTheme(darkMode.isSelected(), owner);
        setVisible(false);
    }

    public static void showDialog(JFrame owner, PersistenceService ps) {
        SettingsDialog dlg = new SettingsDialog(owner, ps);
        dlg.setVisible(true);
    }
}
