package com.lrogerscs.marketlynx.pane;

import com.lrogerscs.marketlynx.stock.Stock;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * CorrelationChartPane displays the correlation between points using JavaFX charts and other elements.
 *
 * @author Lee Rogers
 */
public class CorrelationChartPane extends VBox {
    private NumberAxis x = new NumberAxis(), y = new NumberAxis();
    private ScatterChart scatterChart = new ScatterChart(x, y);
    private HBox bottomPane = new HBox();
    private Label correlation = new Label(), points = new Label();
    private Stock stock = new Stock();
    private double xMax = Double.MIN_VALUE, yMax = Double.MIN_VALUE, xMin = Double.MAX_VALUE, yMin = Double.MAX_VALUE;
    private int size = 0;

    /**
     * Default constructor. Sets styling, adds children.
     */
    public CorrelationChartPane() {
        // Add default styling.
        x.setLabel("y(t)");
        y.setLabel("y(t + 1)");
        x.getStyleClass().add("scatter-x-axis");
        y.getStyleClass().add("scatter-y-axis");
        x.setAutoRanging(false);
        y.setAutoRanging(false);

        scatterChart.getStyleClass().add("scatter-chart");
        scatterChart.setAnimated(false);

        bottomPane.setAlignment(Pos.CENTER);
        getStyleClass().add("chart-pane");

        // Add elements to panes.
        bottomPane.getChildren().addAll(correlation, points);
        getChildren().addAll(scatterChart, bottomPane);

        // Add stylesheet.
        getStylesheets().add(getClass().getResource("/com/lrogerscs/marketlynx/css/chart_style.css").toExternalForm());
    }

    /**
     * Retrieves the size (number of points displayed).
     * @return Size of currently displayed data.
     */
    public int size() {
        return size;
    }

    /**
     * Retrieves the name of the currently displayed stock.
     * @return Name of the stock.
     */
    public String getName() {
        return stock.getName();
    }

    /**
     * Retrieves the interval of the currently displayed stock.
     * @return Interval of the stock.
     */
    public String getInterval() {
        return stock.getInterval();
    }

    /**
     * Sets the currently displayed stock.
     * @param name Name of the stock.
     * @param interval Interval of the stock.
     * @param stockData Array of ArrayLists containing stock data.
     */
    public void setStock(String name, String interval, ArrayList[] stockData) {
        stock.setStock(name, interval, stockData);
        draw(100);
    }

    /**
     * Displays correlation data over a given number of time units.
     * @param units Number of time units to display (10-500).
     */
    public void draw(int units) {
        // Adjust input value. Smallest value is 10, largest value is 500 or data size if data size < 500.
        if (units < 10)
            units = 10;
        else {
            if (units > stock.size())
                units = stock.size();
            if (units > 500)
                units = 500;
        }

        // If size is already input value or data size < 10, return.
        if (size == units || stock.size() < 10)
            return;

        // Initialize variables, clear class attributes, set size.
        XYChart.Series series = new XYChart.Series();
        xMax = yMax = Double.MIN_VALUE;
        xMin = yMin = Double.MAX_VALUE;
        scatterChart.getData().clear();
        size = units;

        // Build series of points and update maximums/minimums.
        for (int i = 1; i < size; i++) {
            series.getData().add(new XYChart.Data(stock.getClose(stock.size() - i), stock.getClose(stock.size() - 1 - i)));

            if (stock.getClose(stock.size() - i) > xMax)
                xMax = stock.getClose(stock.size() - i);
            if (stock.getClose(stock.size() - i) < xMin)
                xMin = stock.getClose(stock.size() - i);

            if (stock.getClose(stock.size() - 1 - i) > yMax)
                yMax = stock.getClose(stock.size() - 1 - i);
            if (stock.getClose(stock.size() - 1 - i) < yMin)
                yMin = stock.getClose(stock.size() - 1 - i);
        }

        scatterChart.getData().add(series);
        formatAxis();

        // Set labels.
        correlation.setText("Correlation: " + String.format("%.2f", stock.getCorr(size)));
        points.setText("Points Plotted: " + size);
    }

    /**
     * Formats axis bounds and tick intervals according to new max/min.
     */
    private void formatAxis() {
        double diff = 1.1 * yMax - 0.9 * yMin;
        int exp = diff > 0 ? (int) Math.log10(diff) : 0;
        double interval = Math.ceil(diff / Math.pow(10, exp - 1) / 25) * 25 * Math.pow(10, exp - 2);

        x.setUpperBound(Math.ceil((1.1 * yMax) / interval) * interval);
        x.setLowerBound(Math.floor((0.9 * yMin) / interval) * interval);
        y.setUpperBound(x.getUpperBound());
        y.setLowerBound(x.getLowerBound());
        x.setTickUnit(interval);
        y.setTickUnit(interval);
    }
}
