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

public class GraphPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 46365252L;

    private final ChartPanel chartPanel;

    public GraphPanel(SavingsPlanner planner, SavingsGoal goal, int months) {
        setLayout(new BorderLayout());

        // Build the time series dataset
        TimeSeries series = new TimeSeries("Cumulative Savings");
        double startSaved = planner.calculateTotalSavingsForGoal();
        double monthly = planner.calculateSuggestedSavings(goal)[0];
        YearMonth ymStart = YearMonth.now();

        for (int i = 0; i <= months; i++) {
            YearMonth current = ymStart.plusMonths(i);
            double saved = startSaved + i * monthly;
            series.add(new Month(current.getMonthValue(), current.getYear()), saved);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Savings Over Time",
                "Date",
                "Total Saved (£)",
                dataset,
                false,
                true,
                false
        );

        // Style chart background and grid
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));

        // Vertical axis padding
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setLowerMargin(0.10);
        rangeAxis.setUpperMargin(0.10);

        // Horizontal (time) axis padding and tick units
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 3));
        domainAxis.setDateFormatOverride(new java.text.SimpleDateFormat("MMM-yy"));

        // Wrap in ChartPanel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(650, 300));

        // Save button
        JButton saveButton = new JButton("Save Graph");
        saveButton.addActionListener(this::onSave);

        add(chartPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void onSave(ActionEvent e) {
        try {
            BufferedImage image = chartPanel.getChart()
                    .createBufferedImage(chartPanel.getWidth(), chartPanel.getHeight());
            File dir = new File("src/main/resources");
            dir.mkdirs();
            File out = new File(dir, "savings_chart.png");
            ImageIO.write(image, "png", out);
            JOptionPane.showMessageDialog(this,
                    "Graph saved to " + out.getAbsolutePath(),
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving graph: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
