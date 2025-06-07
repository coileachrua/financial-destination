import com.savingsplanner.model.BudgetCategory;
import com.savingsplanner.model.User;
import com.savingsplanner.service.PersistenceService;
import com.savingsplanner.service.SavingsPlanner;
import com.savingsplanner.ui.ExpensePanel;
import com.savingsplanner.ui.UserPanel;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PanelFormatTest {
    @Test
    void userPanelFormatsTotalInUKCurrency() throws Exception {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addUser(new User("Alice", 3000.0, 0.0));
        planner.addUser(new User("Bob", 2500.0, 0.0));

        UserPanel panel = new UserPanel(planner, new PersistenceService());
        JLabel lbl = new JLabel();

        Method m = UserPanel.class.getDeclaredMethod("updateTotalLabel", SavingsPlanner.class, JLabel.class);
        m.setAccessible(true);
        m.invoke(panel, planner, lbl);

        assertEquals("Total Income: £5,500.00", lbl.getText());
    }

    @Test
    void expensePanelFormatsTotalInUKCurrency() throws Exception {
        SavingsPlanner planner = new SavingsPlanner();
        planner.addExpense(new BudgetCategory("Rent", 1200.0));
        planner.addExpense(new BudgetCategory("Food", 400.0));

        ExpensePanel panel = new ExpensePanel(planner, new PersistenceService());
        JLabel lbl = new JLabel();

        Method m = ExpensePanel.class.getDeclaredMethod("updateTotalLabel", SavingsPlanner.class, JLabel.class);
        m.setAccessible(true);
        m.invoke(panel, planner, lbl);

        assertEquals("Total Expenses: £1,600.00", lbl.getText());
    }
}
