package com.lrogerscs.marketlynx.pane;

import com.lrogerscs.marketlynx.stock.Stock;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * StockChartPane displays stock data using JavaFX charts and other elements.
 *
 * @author Lee Rogers
 */
public class StockChartPane extends VBox {
    private CategoryAxis areaXAxis = new CategoryAxis();
    private NumberAxis areaYAxis = new NumberAxis();
    private CategoryAxis barXAxis = new CategoryAxis();
    private NumberAxis barYAxis = new NumberAxis();
    private AreaChart areaChart = new AreaChart(areaXAxis, areaYAxis);
    private StackedBarChart stackedBarChart = new StackedBarChart(barXAxis, barYAxis);
    private XYChart.Series areaSeries = new XYChart.Series();
    private XYChart.Series barSeries = new XYChart.Series();
    private XYChart.Series trendLineSeries = new XYChart.Series();
    private CursorInfoPane cursorInfoPane = new CursorInfoPane();
    private StockInfoPane stockInfoPane = new StockInfoPane();
    private ChartOverlayPane chartOverlayPane = new ChartOverlayPane();
    private Stock stock = new Stock();
    private IntegerProperty size = new SimpleIntegerProperty(0);
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private boolean trendLineShown = false;
    private boolean volumeShown = false;

    /**
     * Default constructor. Initializes variables and behavior.
     */
    public StockChartPane() {
        StackPane chartPane = new StackPane();

        // Set behavior.
        size.addListener((observableValue, number, t1) -> formatChart());
        areaChart.setOnMouseEntered(MouseEvent -> {
            chartOverlayPane.showCrossHair();
            cursorInfoPane.setVisible(true);
        });
        areaChart.setOnMouseMoved((MouseEvent event) -> {
            String date = areaXAxis.getValueForDisplay(event.getX() - areaChart.lookup(".chart-plot-background").getLayoutX());
            if (date == null)
                return;
            if (!date.equals(chartOverlayPane.getY()))
                formatCursor(date);
        });
        areaChart.setOnMouseExited(MouseEvent -> {
            chartOverlayPane.hideCrossHair();
            cursorInfoPane.setVisible(false);
        });

        // Add default styling.
        areaChart.getStyleClass().add("area-chart");
        stackedBarChart.getStyleClass().add("stacked-bar-chart");
        areaChart.setAnimated(false);
        stackedBarChart.setAnimated(false);
        stackedBarChart.setVisible(false);

        areaXAxis.setLabel("Date");
        areaYAxis.setLabel("Close");
        barXAxis.setLabel("Date");
        barYAxis.setLabel("Close");
        areaYAxis.getStyleClass().add("area-y-axis");
        barXAxis.getStyleClass().add("bar-x-axis");
        barYAxis.getStyleClass().add("bar-y-axis");
        areaXAxis.setAutoRanging(false);
        areaYAxis.setAutoRanging(false);
        barXAxis.setAutoRanging(false);
        barYAxis.setAutoRanging(false);

        getStyleClass().add("chart-pane");
        chartPane.setAlignment(Pos.TOP_LEFT);
        cursorInfoPane.setMouseTransparent(true);
        stackedBarChart.setMouseTransparent(true);
        chartOverlayPane.setMouseTransparent(true);

        // Add objects.
        areaChart.getData().addAll(areaSeries, trendLineSeries);
        stackedBarChart.getData().add(barSeries);
        chartPane.getChildren().addAll(areaChart, stackedBarChart, cursorInfoPane, chartOverlayPane);
        getChildren().addAll(chartPane, stockInfoPane);

        // Add stylesheet.
        getStylesheets().add(getClass().getResource("/com/lrogerscs/marketlynx/css/chart_style.css").toExternalForm());
    }

    /**
     * Retrieves the size (number of points displayed).
     * @return Size of currently displayed data.
     */
    public int size() {
        return size.get();
    }

    /**
     * Retrieves the name of currently displayed stock.
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
     * If not already shown, shows a trend line.
     */
    public void showTrendLine() {
        if (trendLineShown == false) {
            drawTrendLine();
            trendLineSeries.getNode().setVisible(true);
            trendLineShown = true;
        }
    }

    /**
     * If not already hidden, hides trend line.
     */
    public void hideTrendLine() {
        if (trendLineShown == true) {
            trendLineSeries.getNode().setVisible(false);
            trendLineShown = false;
        }
    }

    /**
     * If not already shown, shows volume bars.
     */
    public void showVolume() {
        if (volumeShown == false) {
            drawVolume();
            stackedBarChart.setVisible(true);
            volumeShown = true;
        }
    }

    /**
     * If not already hidden, hides volume bars.
     */
    public void hideVolume() {
        if (volumeShown == true) {
            stackedBarChart.setVisible(false);
            volumeShown = false;
        }
    }

    /**
     * Sets the currently displayed stock.
     * @param name Name of the stock.
     * @param interval Interval of the stock.
     * @param stockData Array of ArrayLists containing stock data.
     */
    public void setStock(String name, String interval, ArrayList[] stockData) {
        stock.setStock(name, interval, stockData);

        // Reset max/min.
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;

        // Build new series of points.
        areaSeries.getData().clear();
        for (int i = 0; i < stock.size(); i++)
            areaSeries.getData().add(new XYChart.Data(stock.getDate(i), stock.getClose(i)));

        // Reset size and draw new graph.
        size.set(0);
        draw(100);
    }

    /**
     * Displays stock data over a given number of time units.
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
        if (size.get() == units || stock.size() < 10)
            return;

        // Set local and class variables.
        max = size.get() < units ? max : Double.MIN_VALUE;
        min = size.get() < units ? min : Double.MAX_VALUE;
        List<String> validDates = stock.dates(stock.size() - units, stock.size());
        int end = size.get() < units ? units - size.get() : units;

        // Update max/min.
        for (int i = 0; i < end; i++) {
            if (stock.getClose(stock.size() - units + i) > max)
                max = stock.getClose(stock.size() - units + i);
            if (stock.getClose(stock.size() - units + i) < min)
                min = stock.getClose(stock.size() - units + i);
        }

        areaXAxis.setCategories(FXCollections.observableArrayList(validDates));
        areaXAxis.invalidateRange(validDates);
        barXAxis.setCategories(FXCollections.observableArrayList(validDates));
        barXAxis.invalidateRange(validDates);

        // Format new axis, force refresh areaChart layout, and set size.
        formatAxis();
        areaChart.layout();
        size.set(units);

        // Set label texts.
        stockInfoPane.setLabels(stock.getDate(stock.size() - units), stock.getDate(stock.size() - 1), stock.getAverage(units)
                , max, min, stock.getVolatility(units), stock.getChange(stock.getClose(stock.size() - 1), units));

        // Load volume or trend line if necessary.
        if (volumeShown == true)
            drawVolume();
        if (trendLineShown == true)
            drawTrendLine();
    }

    /**
     * Formats axis bounds and tick intervals according to new max/min.
     */
    private void formatAxis() {
        double diff = 1.1 * max - 0.9 * min;
        int exp = diff > 0 ? (int) Math.log10(diff) : 0;
        double interval = Math.ceil(diff / Math.pow(10, exp - 1) / 25) * 25 * Math.pow(10, exp - 2);

        areaYAxis.setUpperBound(Math.ceil((1.1 * max) / interval) * interval);
        areaYAxis.setLowerBound(Math.floor((0.9 * min) / interval) * interval);
        barYAxis.setUpperBound(areaYAxis.getUpperBound());
        barYAxis.setLowerBound(areaYAxis.getLowerBound());
        areaYAxis.setTickUnit(interval);
        barYAxis.setTickUnit(interval);
    }

    /**
     * Dynamically sets styling for current chart.
     */
    private void formatChart() {
        if (size.get() < 1)
            return;

        Node plotArea = areaChart.lookup(".chart-plot-background");
        double xBuffer = plotArea.getLayoutX() * 1.5;
        double yBuffer = plotArea.getLayoutY() * 1.5;
        double price = stock.getClose(stock.size() - 1);
        String labelColor, lineColor, circleColor;

        if (stock.getClose(stock.size() - size.get()) < stock.getClose(stock.size() - 1)) {
            labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #55f67e, #9efab5)";
            lineColor = "-fx-stroke: #9efab5";
            circleColor = "-fx-fill: #55f67e";
            areaSeries.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: #55f67e; -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: linear-gradient(to top, transparent "
                    + (areaYAxis.getLowerBound() / max * 100) + "%, rgba(85, 246, 126, .3));");
        } else if (stock.getClose(stock.size() - size.get()) > stock.getClose(stock.size() - 1)) {
            labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #e70d2e, #f43e59)";
            lineColor = "-fx-stroke: #f43e59";
            circleColor = "-fx-fill: #e70d2e";
            areaSeries.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: #f20d46; -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: linear-gradient(to top, transparent "
                    + (areaYAxis.getLowerBound() / max * 100) + "%, rgba(231, 13, 68, .3));");
        } else {
            labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #808080, #a6a6a6)";
            lineColor = "-fx-stroke: #a6a6a6";
            circleColor = "-fx-fill: #808080";
            areaSeries.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: rgba(92, 87, 89, .95); -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: linear-gradient(to top, transparent "
                    + (areaYAxis.getLowerBound() / max * 100) + "%, rgba(92, 87, 89, .3));");
        }

        chartOverlayPane.setEndLabel(String.format("%.2f", price), areaXAxis.getWidth() + xBuffer
                , areaYAxis.getDisplayPosition(price) + yBuffer, labelColor);
        chartOverlayPane.setEndLine(areaXAxis.getWidth() + xBuffer, areaYAxis.getDisplayPosition(price) + yBuffer
                , xBuffer, areaYAxis.getDisplayPosition(price) + yBuffer, lineColor);
        chartOverlayPane.setEndCircle(areaXAxis.getDisplayPosition(stock.getDate(stock.size() - 1)) + xBuffer
                , areaYAxis.getDisplayPosition(price) + yBuffer, circleColor);
    }

    /**
     * Sets cursor elements (cross hair, info pane, etc.) given date cursor is hovering over.
     * @param date Cursor date.
     */
    private void formatCursor(String date) {
        Node plotArea = areaChart.lookup(".chart-plot-background");
        double xBuffer = plotArea.getLayoutX() * 1.5;
        double yBuffer = plotArea.getLayoutY() * 1.5;
        int index = stock.getIndexOfDate(date);
        double price = stock.getClose(index);

        cursorInfoPane.setLabels(stock.getOpen(index), stock.getHigh(index), stock.getLow(index), price
                , stock.getChange(stock.getClose(index), size.get()), stock.getVolume(index));
        chartOverlayPane.setXLabel(date, areaXAxis.getDisplayPosition(date) + xBuffer
                , areaYAxis.getDisplayPosition(areaYAxis.getLowerBound()) + yBuffer);
        chartOverlayPane.setYLabel(String.format("%.2f", price), areaXAxis.getWidth() + xBuffer
                , areaYAxis.getDisplayPosition(price) + yBuffer);
        chartOverlayPane.setVerticalLine(areaXAxis.getDisplayPosition(date) + xBuffer
                , areaYAxis.getDisplayPosition(areaYAxis.getLowerBound()) + yBuffer
                , areaXAxis.getDisplayPosition(date) + xBuffer
                , areaYAxis.getDisplayPosition(areaYAxis.getUpperBound()) + yBuffer);
        chartOverlayPane.setHorizontalLine(areaXAxis.getWidth() + xBuffer
                , areaYAxis.getDisplayPosition(price) + yBuffer, xBuffer
                , areaYAxis.getDisplayPosition(price) + yBuffer);
    }

    /**
     * Displays a trend line according to currently displayed data.
     */
    private void drawTrendLine() {
        double a, b, middle, sumY, sumXY, sumXSquared;

        trendLineSeries.getData().clear();

        sumY = sumXY = sumXSquared = 0.0;
        middle = size.get() % 2 == 0 ? (double) ((size.get() / 2 + 1) + size.get() / 2) / 2 : size.get() / 2 + 1;

        for (int i = 1; i <= size.get(); i++) {
            sumY += stock.getClose(stock.size() - size.get() + i - 1);
            sumXY += (i - middle) * stock.getClose(stock.size() - size.get() + i - 1);
            sumXSquared += Math.pow((i - middle), 2);
        }

        a = sumY / size.get();
        b = sumXY / sumXSquared;

        trendLineSeries.getData().add(new XYChart.Data(stock.getDate(stock.size() - size.get()), a + b * (1 - middle)));
        trendLineSeries.getData().add(new XYChart.Data(stock.getDate(stock.size() - 1), a + b * (size.get() - middle)));
    }

    /**
     * Displays stock volumes according to currently displayed data.
     */
    private void drawVolume() {
        XYChart.Data dataPoint;
        long maxVolume = stock.getMaxVolume(size.get());

        barSeries.getData().clear();

        // Build the first volume bar.
        dataPoint = new XYChart.Data(stock.getDate(stock.size() - size.get())
                , ((0.2 * ((double) stock.getVolume(stock.size() - size.get()) / maxVolume)
                * (areaYAxis.getUpperBound() - areaYAxis.getLowerBound())) + areaYAxis.getLowerBound()));
        barSeries.getData().add(dataPoint);
        dataPoint.getNode().setStyle("-fx-bar-fill: rgba(92, 87, 89, .55)");

        // Build the remaining volume bars.
        for (int i = 1; i < size.get(); i++) {
            dataPoint = new XYChart.Data(stock.getDate(stock.size() - size.get() + i)
                    , ((0.2 * ((double) stock.getVolume(stock.size() - size.get() + i) / maxVolume)
                    * (areaYAxis.getUpperBound() - areaYAxis.getLowerBound())) + areaYAxis.getLowerBound()));
            barSeries.getData().add(dataPoint);

            if (stock.getClose(stock.size() - size.get() + i) > stock.getClose(stock.size() - size.get() + i - 1))
                dataPoint.getNode().setStyle("-fx-bar-fill: rgba(85, 246, 126, .4)");
            else if (stock.getClose(stock.size() - size.get() + i) < stock.getClose(stock.size() - size.get() + i - 1))
                dataPoint.getNode().setStyle("-fx-bar-fill: rgba(231, 13, 68, .4)");
            else
                dataPoint.getNode().setStyle("-fx-bar-fill: rgba(92, 87, 89, .4)");
        }
    }
}
