package com.lrogerscs.marketlynx.stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Stock holds all requisite stock data.
 *
 * @author Lee Rogers
 */
public class Stock {
    private String name;
    private String interval;
    private ArrayList<String> dates;
    private ArrayList<Double> opens;
    private ArrayList<Double> highs;
    private ArrayList<Double> lows;
    private ArrayList<Double> closes;
    private ArrayList<Long> volumes;

    /**
     * Default constructor. Initializes variables.
     */
    public Stock() {
        name = "";
        interval = "";
        dates = new ArrayList();
        opens = new ArrayList();
        highs = new ArrayList();
        lows = new ArrayList();
        closes = new ArrayList();
        volumes = new ArrayList();
    }

    /**
     * Retrieves the size (number of data points).
     * @return Size of stock data.
     */
    public int size() {
        return dates.size();
    }

    /**
     * Retrieves name.
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves interval.
     * @return Interval.
     */
    public String getInterval() {
        return interval;
    }

    /**
     * Retrieves a date according to an index.
     * @param index Index of date.
     * @return Requested date.
     */
    public String getDate(int index) {
        return dates.get(index);
    }

    /**
     * Retrieves an open price according to an index.
     * @param index Index of open.
     * @return Requested open.
     */
    public double getOpen(int index) {
        return opens.get(index);
    }

    /**
     * Retrieves a high price according to an index.
     * @param index Index of high.
     * @return Requested high.
     */
    public double getHigh(int index) {
        return highs.get(index);
    }

    /**
     * Retrieves a low price according to an index.
     * @param index Index of low.
     * @return Requested low.
     */
    public double getLow(int index) {
        return lows.get(index);
    }

    /**
     * Retrieves a close price according to an index.
     * @param index Index of close.
     * @return Requested close.
     */
    public double getClose(int index) {
        return closes.get(index);
    }

    /**
     * Retrieves a volume according to an index.
     * @param index Index of volume.
     * @return Requested volume.
     */
    public long getVolume(int index) {
        return volumes.get(index);
    }

    /**
     * Retrieves the index of a date.
     * @param date Date.
     * @return Index of date.
     */
    public int getIndexOfDate(String date) {
        return dates.indexOf(date);
    }

    /**
     * Returns the average close price across (a number of) data points.
     * @param size Size of data to use.
     * @return Average close price.
     */
    public double getAverage(int size) {
        double average = 0.0;
        for (int i = 0; i < size; i++)
            average += closes.get(size() - size + i);
        return average / size;
    }

    /**
     * Returns the volatility across (a number of) data points.
     * @param size Size of data to use.
     * @return Volatility.
     */
    public double getVolatility(int size) {
        double sum = 0.0;
        double average = getAverage(size);
        for (int i = 0; i < size; i++)
            sum += Math.pow((closes.get(size() - size + i) - average), 2);
        return Math.sqrt(sum / size);
    }

    /**
     * Returns the percentage change relative to the first close price in a set of points.
     * @param val Value to compare.
     * @param size Size of data.
     * @return Percentage change.
     */
    public double getChange(double val, int size) {
        return ((val - closes.get(size() - size)) / closes.get(size() - size)) * 100;
    }

    /**
     * Returns the correlation of (a number of) data points.
     * @param size Size of data to use.
     * @return Correlation between points.
     */
    public double getCorr(int size) {
        double numerator = 0.0;
        double denominator = 0.0;
        double average = getAverage(size);

        denominator += Math.pow((closes.get(size() - 1) - average), 2);

        for (int i = 1; i < size; i++) {
            numerator += (closes.get(size() - i) - average) * (closes.get(size() - 1 - i) - average);
            denominator += Math.pow((closes.get(size() - 1 - i) - average), 2);
        }

        return numerator / denominator;
    }

    /**
     * Returns the max volume across (a number of) data points.
     * @param size Size of data to use.
     * @return Maximum volume.
     */
    public long getMaxVolume(int size) {
        long maxVolume = Long.MIN_VALUE;
        for (int i = size() - size; i < size(); i++) {
            if (volumes.get(i) > maxVolume)
                maxVolume = volumes.get(i);
        }
        return maxVolume;
    }

    /**
     * Sets the current stock.
     * @param name Name.
     * @param interval Interval.
     * @param stockData Array of ArrayLists containing stock data.
     */
    public void setStock(String name, String interval, ArrayList[] stockData) {
        this.name = name;
        this.interval = interval;

        dates.clear();
        opens.clear();
        highs.clear();
        lows.clear();
        closes.clear();
        volumes.clear();

        for (int i = 0; i < stockData[0].size(); i++) {
            dates.add((String) stockData[0].get(i));
            opens.add((Double) stockData[1].get(i));
            highs.add((Double) stockData[2].get(i));
            lows.add((Double) stockData[3].get(i));
            closes.add((Double) stockData[4].get(i));
            volumes.add((Long) stockData[5].get(i));
        }
    }

    /**
     * Returns a subsection of the ArrayList containing dates.
     * @param start Starting point.
     * @param end Ending point.
     * @return List containing subsection of dates ArrayList.
     */
    public List<String> dates(int start, int end) {
        return dates.subList(start, end);
    }
}
