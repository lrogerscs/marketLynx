package com.lrogerscs.marketlynx.pane;

import com.lrogerscs.marketlynx.*;
import com.lrogerscs.marketlynx.stock.Stock;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
    private CategoryAxis areaXAxis;
    private NumberAxis areaYAxis;
    private CategoryAxis barXAxis;
    private NumberAxis barYAxis;
    private AreaChart areaChart;
    private StackedBarChart stackedBarChart;
    private XYChart.Series areaSeries;
    private XYChart.Series barSeries;
    private XYChart.Series trendLineSeries;
    private CursorInfoPane cursorInfoPane;
    private StockInfoPane stockInfoPane;
    private Stock stock;
    private IntegerProperty size;
    private double max;
    private double min;
    private boolean trendLineShown;
    private boolean volumeShown;

    /**
     * Default constructor. Initializes variables and behavior.
     */
    public StockChartPane() {
        areaXAxis = new CategoryAxis();
        areaYAxis = new NumberAxis();
        barXAxis = new CategoryAxis();
        barYAxis = new NumberAxis();
        areaChart = new AreaChart(areaXAxis, areaYAxis);
        stackedBarChart = new StackedBarChart(barXAxis, barYAxis);
        areaSeries = new XYChart.Series();
        barSeries = new XYChart.Series();
        trendLineSeries = new XYChart.Series();
        cursorInfoPane = new CursorInfoPane();
        stockInfoPane = new StockInfoPane();
        stock = new Stock();
        size = new SimpleIntegerProperty(0);
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        trendLineShown = false;
        volumeShown = false;
        Node plotArea = areaChart.lookup(".chart-plot-background");
        ChartOverlayPane chartOverlayPane = new ChartOverlayPane();
        StackPane chartPane = new StackPane();

        size.addListener((observableValue, number, t1) -> {
            if (size.get() < 1)
                return;

            double xBuffer = plotArea.getLayoutX() * 1.5;
            double yBuffer = plotArea.getLayoutY() * 1.5;
            double price = stock.getClose(stock.size() - 1);
            String labelColor;
            String lineColor;
            String circleColor;

            if (stock.getClose(stock.size() - size.get()) < stock.getClose(stock.size() - 1)) {
                labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #55f67e, #9efab5)";
                lineColor = "-fx-stroke: #9efab5";
                circleColor = "-fx-fill: #55f67e";
            } else if (stock.getClose(stock.size() - size.get()) > stock.getClose(stock.size() - 1)) {
                labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #e70d2e, #f43e59)";
                lineColor = "-fx-stroke: #f43e59";
                circleColor = "-fx-fill: #e70d2e";
            } else {
                labelColor = "-fx-background-color: radial-gradient(center 0% 50% , radius 100% , #808080, #a6a6a6)";
                lineColor = "-fx-stroke: #a6a6a6";
                circleColor = "-fx-fill: #808080";
            }

            chartOverlayPane.setEndLabel(String.format("%.2f", price), areaXAxis.getWidth() + xBuffer
                    , areaYAxis.getDisplayPosition(price) + yBuffer, labelColor);
            chartOverlayPane.setEndLine(areaXAxis.getWidth() + xBuffer, areaYAxis.getDisplayPosition(price) + yBuffer
                    , xBuffer, areaYAxis.getDisplayPosition(price) + yBuffer, lineColor);
            chartOverlayPane.setEndCircle(areaXAxis.getDisplayPosition(stock.getDate(stock.size() - 1)) + xBuffer
                    , areaYAxis.getDisplayPosition(price) + yBuffer, circleColor);
        });

        areaChart.setOnMouseEntered(MouseEvent -> {
            chartOverlayPane.showCrossHair();
            cursorInfoPane.setVisible(true);
        });

        areaChart.setOnMouseMoved((MouseEvent event) -> {
            String date = areaXAxis.getValueForDisplay(event.getX() - plotArea.getLayoutX());

            if (date == null)
                return;

            if (!date.equals(chartOverlayPane.getY())) {
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
        });

        areaChart.setOnMouseExited(MouseEvent -> {
            chartOverlayPane.hideCrossHair();
            cursorInfoPane.setVisible(false);
        });

        // Format charts.
        areaChart.getData().addAll(areaSeries, trendLineSeries);
        areaChart.setAnimated(false);
        areaChart.setLegendVisible(false);
        areaChart.setCreateSymbols(false);
        areaChart.setPrefSize(1200, 800);
        stackedBarChart.getData().add(barSeries);
        stackedBarChart.setVisible(false);
        stackedBarChart.setAnimated(false);
        stackedBarChart.setLegendVisible(false);
        stackedBarChart.setCategoryGap(0);
        stackedBarChart.setPrefSize(1200, 800);

        // Format axis.
        areaXAxis.setLabel("Date");
        areaYAxis.setLabel("Close");
        areaYAxis.setSide(Side.RIGHT);
        areaYAxis.setMinorTickVisible(false);
        barXAxis.setLabel("Date");
        barYAxis.setLabel("Close");
        barXAxis.setStyle("-fx-tick-label-fill: transparent");
        barYAxis.setStyle("-fx-tick-label-fill: transparent");
        barYAxis.setSide(Side.RIGHT);
        barYAxis.setMinorTickVisible(false);

        // Format panes.
        chartPane.setAlignment(Pos.TOP_LEFT);
        cursorInfoPane.setMouseTransparent(true);
        stackedBarChart.setMouseTransparent(true);
        chartOverlayPane.setMouseTransparent(true);

        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

        // Add objects to panes.
        chartPane.getChildren().addAll(areaChart, stackedBarChart, cursorInfoPane, chartOverlayPane);
        getChildren().addAll(chartPane, stockInfoPane);

        // Add stylesheet.
        getStylesheets().add(Main.class.getResource("css/chart_style.css").toExternalForm());
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
        for(int i = 0; i < stock.size(); i++)
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

        // Modify x axis.
        areaXAxis.setAutoRanging(false);
        areaXAxis.setCategories(FXCollections.observableArrayList(validDates));
        areaXAxis.invalidateRange(validDates);
        barXAxis.setAutoRanging(false);
        barXAxis.setCategories(FXCollections.observableArrayList(validDates));
        barXAxis.invalidateRange(validDates);

        // Modify y axis bounds and tick intervals.
        double estAxisDiff = 1.1 * max - 0.9 * min;
        int exp = estAxisDiff > 0 ? (int) Math.log10(estAxisDiff) : 0;
        double tickInterval = Math.ceil(estAxisDiff / Math.pow(10, exp - 1) / 25) * 25 * Math.pow(10, exp - 2);

        areaYAxis.setAutoRanging(false);
        areaYAxis.setUpperBound(Math.ceil((1.1 * max) / tickInterval) * tickInterval);
        areaYAxis.setLowerBound(Math.floor((0.9 * min) / tickInterval) * tickInterval);
        areaYAxis.setTickUnit(tickInterval);
        barYAxis.setAutoRanging(false);
        barYAxis.setUpperBound(areaYAxis.getUpperBound());
        barYAxis.setLowerBound(areaYAxis.getLowerBound());
        barYAxis.setTickUnit(tickInterval);

        // Format chart after all variables and data points have been calculated.
        if (stock.getClose(stock.size() - units) < stock.getClose(stock.size() - 1)) {
            areaSeries.getNode().lookup(".chart-series-area-line")
                    .setStyle("-fx-stroke: #55f67e; -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill")
                    .setStyle("-fx-fill: linear-gradient(to top, transparent " + (areaYAxis.getLowerBound() / max * 100)
                            + "%, rgba(85, 246, 126, .3));");
        } else if (stock.getClose(stock.size() - units) > stock.getClose(stock.size() - 1)) {
            areaSeries.getNode().lookup(".chart-series-area-line")
                    .setStyle("-fx-stroke: #f20d46; -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill")
                    .setStyle("-fx-fill: linear-gradient(to top, transparent " + (areaYAxis.getLowerBound() / max * 100)
                            + "%, rgba(231, 13, 68, .3));");
        } else {
            areaSeries.getNode().lookup(".chart-series-area-line")
                    .setStyle("-fx-stroke: rgba(92, 87, 89, .95); -fx-stroke-width: 3");
            areaSeries.getNode().lookup(".chart-series-area-fill")
                    .setStyle("-fx-fill: linear-gradient(to top, transparent " + (areaYAxis.getLowerBound() / max * 100)
                            + "%, rgba(92, 87, 89, .3));");
        }

        // Set label texts.
        stockInfoPane.setLabels(stock.getDate(stock.size() - units), stock.getDate(stock.size() - 1)
                , stock.getAverage(units), max, min, stock.getVolatility(units)
                , stock.getChange(stock.getClose(stock.size() - 1), units));

        // Force refresh areaChart layout and set size.
        areaChart.layout();
        size.set(units);

        // Load volume or trend line if necessary.
        if (volumeShown == true)
            drawVolume();
        if (trendLineShown == true)
            drawTrendLine();
    }

    /**
     * Calculates and displays a trend line according to currently displayed data.
     */
    private void drawTrendLine() {
        double a;
        double b;
        double middle;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumXSquared = 0.0;

        if (size.get() < 2)
            return;

        trendLineSeries.getData().clear();

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

        trendLineSeries.getNode().lookup(".chart-series-area-line")
                .setStyle("-fx-stroke: #3d99f5; -fx-stroke-width: 1.5");
        trendLineSeries.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: transparent");
    }

    /**
     * Retrieves and displays stock volumes according to currently displayed data.
     */
    private void drawVolume() {
        XYChart.Data dataPoint;

        if (size.get() < 1)
            return;

        // Clear class attributes.
        barSeries.getData().clear();

        long maxVolume = stock.getMaxVolume(size.get());

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