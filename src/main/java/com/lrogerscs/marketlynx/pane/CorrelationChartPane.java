package com.lrogerscs.marketlynx.pane;

import com.lrogerscs.marketlynx.Main;
import com.lrogerscs.marketlynx.stock.Stock;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
    private Stock stock;
    private NumberAxis x;
    private NumberAxis y;
    private int size;
    private ScatterChart scatterChart;
    private HBox bottomPane;
    private Label correlation;
    private Label points;

    /**
     * Default constructor. Initializes variables.
     */
    public CorrelationChartPane() {
        // Initialize class variables.
        stock = new Stock();
        x = new NumberAxis();
        y = new NumberAxis();
        size = 0;
        scatterChart = new ScatterChart(x, y);
        bottomPane = new HBox();
        correlation = new Label();
        points = new Label();

        // Format axis.
        x.setLabel("y(t)");
        x.setMinorTickVisible(false);
        y.setLabel("y(t + 1)");
        y.setSide(Side.RIGHT);
        y.setMinorTickVisible(false);

        // Format chart.
        scatterChart.setAnimated(false);
        scatterChart.setLegendVisible(false);
        scatterChart.setPrefSize(1200, 800);

        // Format panes.
        bottomPane.setAlignment(Pos.CENTER);
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

        // Add elements to panes.
        bottomPane.getChildren().addAll(correlation, points);
        getChildren().addAll(scatterChart, bottomPane);

        // Add stylesheet.
        getStylesheets().add(Main.class.getResource("css/chart_style.css").toExternalForm());
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

        // Initialize local variables, clear class attributes, set size.
        XYChart.Series series = new XYChart.Series();
        double xMax = Double.MIN_VALUE;
        double xMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        scatterChart.getData().clear();
        size = units;

        // Build series of points.
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

        // Modify axis bounds and tick intervals.
        double estAxisDiff = 1.1 * yMax - 0.9 * yMin;
        int exp = estAxisDiff > 0 ? (int) Math.log10(estAxisDiff) : 0;
        double tickInterval = Math.ceil(estAxisDiff / Math.pow(10, exp - 1) / 25) * 25 * Math.pow(10, exp - 2);

        x.setAutoRanging(false);
        x.setUpperBound(Math.ceil((1.1 * yMax) / tickInterval) * tickInterval);
        x.setLowerBound(Math.floor((0.9 * yMin) / tickInterval) * tickInterval);
        x.setTickUnit(tickInterval);

        y.setAutoRanging(false);
        y.setUpperBound(x.getUpperBound());
        y.setLowerBound(x.getLowerBound());
        y.setTickUnit(tickInterval);

        // Set labels.
        correlation.setText("Correlation: " + String.format("%.2f", stock.getCorr(size)));
        points.setText("Points Plotted: " + size);
    }
}
