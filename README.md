# Savings Planner Swing App

A simple Java Swing application for planning and tracking personal savings goals. This tool helps you define targets, monitor income and expenses, and visualize your savings trajectory over time.

## Features

* **Savings Goals**: Create and manage goals with target amount and deadline.
* **Budget Tracking**: Record monthly income and expense categories to calculate available savings.
* **Savings Calculation**: Determine required monthly contributions to meet each goal.
* **Visualization**: View a savings trajectory chart implemented with JFreeChart.
* **Editable Tables**: Modify budget categories and goal parameters through a user-friendly table interface.
* **Settings**: Configure application preferences, including UI theme (light/dark) and file locations via the **File → Settings** menu.
* **Persistence**: Save and load user data (goals, budgets, settings) as JSON files using Jackson.
* **Logging**: Application events and errors are logged via Log4j2 (logs/app.log).

## Prerequisites

* Java Development Kit (JDK) 17 or higher
* Apache Maven 3.6 or higher

## Installation & Build

1. Clone or unzip the project directory:

   ```bash
   git clone <repository-url>
   # or unzip savings-planner.zip
   ```
2. Change to the project directory:

   ```bash
   cd savings-planner
   ```
3. Build the project with Maven:

   ```bash
   mvn clean package
   ```

## Run the Application

Use the Maven Exec plugin to launch the Swing UI:

```bash
mvn exec:java -Dexec.mainClass=com.savingsplanner.MainApp
```

Alternatively, run the generated JAR (after packaging) directly:

```bash
java -jar target/savings-planner-1.0-SNAPSHOT.jar
```

## Project Structure

```
├── pom.xml                 # Maven configuration
├── src/
│   └── main/java/
│       └── com/savingsplanner/
│           ├── MainApp.java          # Application entry point
│           ├── model/                # Domain models (User, SavingsGoal, BudgetCategory)
│           ├── service/              # Business logic and persistence
│           └── ui/                   # Swing panels and dialogs
├── savings_data.json       # Sample data file
└── logs/                   # Application log files
```

## Configuration

* **Data File**: Default `savings_data.json` in the project root (or as specified in Settings).
* **Logs**: Written to `logs/app.log`.
* **UI Theme**: Toggle between light and dark themes via the **File → Settings** dialog.

## Dependencies

* **FlatLaf**: Modern Swing look-and-feel.
* **JFreeChart & JCommon**: Charting library for plotting savings trajectory.
* **Jackson**: JSON serialization/deserialization (core, datatype-jsr310).
* **Log4j2**: Logging framework.

## Contributing

Contributions are welcome! Please follow these guidelines:

* Use Java 17 and Maven best practices.
* Keep methods small and descriptive; adhere to DRY principles.
* Avoid inline comments; rely on clear method names.
* Log errors and key application events for troubleshooting.

## License

This project is released under the [MIT License](LICENSE).
You are free to use, modify, and distribute the code as permitted by the license.

