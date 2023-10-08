package com.lrogerscs.marketlynx.reader;

import com.lrogerscs.marketlynx.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * StockFileReader reads stock data from a local file.
 *
 * @author Lee Rogers
 */
public class StockFileReader {
    /**
     * readFile returns an array of ArrayLists containing requested stock data.
     * @param localFile Local file to read from.
     * @return array of ArrayLists.
     */
    public ArrayList[] readFile(String localFile) {
        ArrayList[] stockData = new ArrayList[6];

        // Initialize array of ArrayLists.
        // Array order: dates, opens, highs, lows, closes, and volumes.
        stockData[0] = new ArrayList<String>();
        stockData[1] = new ArrayList<Double>();
        stockData[2] = new ArrayList<Double>();
        stockData[3] = new ArrayList<Double>();
        stockData[4] = new ArrayList<Double>();
        stockData[5] = new ArrayList<Long>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(Main.class.getResource(localFile).toString().substring(6)));
            String[] data;
            int start = lines.size() > 500 ? lines.size() - 500 : 1;

            for (int i = start; i < lines.size(); i++) {
                // Grab data values.
                data = lines.get(i).split(",");

                // Update ArrayLists.
                stockData[0].add(data[0]);
                stockData[1].add(Double.parseDouble(data[1]));
                stockData[2].add(Double.parseDouble(data[2]));
                stockData[3].add(Double.parseDouble(data[3]));
                stockData[4].add(Double.parseDouble(data[4]));
                stockData[5].add(Long.parseLong(data[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList[0];
        }

        return stockData;
    }
}
