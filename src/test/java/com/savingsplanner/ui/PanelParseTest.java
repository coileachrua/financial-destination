package com.savingsplanner.ui;

import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PanelParseTest {
    @Test
    void userPanelParsesPlainNumber() throws Exception {
        SavingsPlanner planner = new SavingsPlanner();
        UserPanel panel = new UserPanel(planner, new PersistenceService());
        Method m = UserPanel.class.getDeclaredMethod("parseCell", Object.class, NumberFormat.class);
        m.setAccessible(true);
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.UK);
        double val = (double) m.invoke(panel, "550.0", fmt);
        assertEquals(550.0, val, 0.001);
    }
}
