package com.savingsplanner.ui;

import com.savingsplanner.model.SavingsGoal;
import com.savingsplanner.service.SavingsPlanner;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;
import java.time.YearMonth;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class GraphPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 46365252L;

    private final ChartPanel chartPanel;

    public GraphPanel(SavingsPlanner planner, SavingsGoal goal, int months) {
        setLayout(new BorderLayout());
        log.info("Generating chart for goal {}", goal.name());

        double balance = planner.calculateRemainingBalance();
        TimeSeriesCollection dataset = buildDataset(planner, goal, months);
        JFreeChart chart = buildChart(dataset);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(650, 300));

        if (balance <= 0) {
            JLabel warn = new JLabel("Goal not achievable with current balance");
            warn.setHorizontalAlignment(SwingConstants.CENTER);
            add(warn, BorderLayout.NORTH);
        }

        JButton saveButton = new JButton("Save Graph");
        saveButton.addActionListener(this::onSave);

        add(chartPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private TimeSeriesCollection buildDataset(SavingsPlanner planner, SavingsGoal goal, int months) {
        double startSaved = planner.calculateTotalSavingsForGoal();
        double suggestedMonthly = planner.calculateSuggestedSavings(goal)[0];
        double maxMonthly = planner.calculateRemainingBalance();
        double twentyMonthly = planner.calculateTotalIncome() * 0.20;
        double goalTotal = goal.total();

        double remainingNeed = Math.max(0, goalTotal - startSaved);
        int monthsMaxNeeded = maxMonthly > 0 ? (int) Math.ceil(remainingNeed / maxMonthly) : 0;
        int monthsTwentyNeeded = twentyMonthly > 0 ? (int) Math.ceil(remainingNeed / twentyMonthly) : 0;
        int monthsToShow = Math.max(Math.max(months, monthsMaxNeeded), monthsTwentyNeeded);

        TimeSeries suggested = new TimeSeries("Suggested Plan");
        TimeSeries accelerated = new TimeSeries("Max Savings Plan");
        TimeSeries twentySeries = new TimeSeries("20% Income Plan");

        YearMonth start = YearMonth.now();
        for (int i = 0; i <= monthsToShow; i++) {
            YearMonth current = start.plusMonths(i);
            double savedSuggested = Math.min(goalTotal, startSaved + i * suggestedMonthly);
            double savedMax = Math.min(goalTotal, startSaved + i * maxMonthly);
            double savedTwenty = Math.min(goalTotal, startSaved + i * twentyMonthly);
            suggested.add(new Month(current.getMonthValue(), current.getYear()), savedSuggested);
            accelerated.add(new Month(current.getMonthValue(), current.getYear()), savedMax);
            twentySeries.add(new Month(current.getMonthValue(), current.getYear()), savedTwenty);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(suggested);
        if (maxMonthly > suggestedMonthly && monthsMaxNeeded > 0) {
            dataset.addSeries(accelerated);
        }
        if (twentyMonthly > 0) {
            dataset.addSeries(twentySeries);
        }
        return dataset;
    }

    private JFreeChart buildChart(TimeSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Savings Over Time",
                "Date",
                "Total Saved (£)",
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setLowerMargin(0.10);
        rangeAxis.setUpperMargin(0.10);

        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 3));
        domainAxis.setDateFormatOverride(new java.text.SimpleDateFormat("MMM-yy"));
        return chart;
    }

    private void onSave(ActionEvent e) {
        try {
            File out = saveChartImage();
            JOptionPane.showMessageDialog(this,
                    "Graph saved to " + out.getAbsolutePath(),
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            log.error("Error saving graph", ex);
            JOptionPane.showMessageDialog(this,
                    "Error saving graph: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private File saveChartImage() throws Exception {
        BufferedImage image = chartPanel.getChart()
                .createBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
        File dir = new File("src/main/resources");
        dir.mkdirs();
        File out = new File(dir, "savings_chart.png");
        ImageIO.write(image, "png", out);
        log.info("Graph saved to {}", out.getAbsolutePath());
        return out;
    }
}
