package com.lrogerscs.marketlynx.controller;

import com.lrogerscs.marketlynx.*;
import com.lrogerscs.marketlynx.downloader.StockFileDownloader;
import com.lrogerscs.marketlynx.reader.StockFileReader;
import com.lrogerscs.marketlynx.pane.CorrelationChartPane;
import com.lrogerscs.marketlynx.pane.StockChartPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * MainController controls the behavior of main.fxml.
 *
 * @author Lee Rogers
 */
public class MainController implements Initializable {
    @FXML
    private VBox pane;

    @FXML
    private TextField tickerTextField;

    @FXML
    private ComboBox intervalComboBox;

    @FXML
    private ToggleButton stockViewToggleButton;

    @FXML
    private ToggleButton correlationViewToggleButton;

    private StockChartPane stockChartPane;
    private CorrelationChartPane correlationChartPane;
    private StockFileDownloader stockFileDownloader;
    private StockFileReader stockFileReader;

    @FXML
    protected void onDownloadButtonClick() {
        String ticker = tickerTextField.getText();
        long period1 = LocalDateTime.of(1970, 1, 1, 23, 59).toEpochSecond(ZoneOffset.UTC);
        long period2 = LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC);
        String interval = convertInterval();

        stockFileDownloader.downloadFile("https://query1.finance.yahoo.com/v7/finance/download/" + ticker
                + "?period1=" + period1 + "&period2=" + period2 + "&interval=" + interval
                + "&events=history&includeAdjustedClose=true", "data/graph_data.csv");
        set(ticker, interval, stockFileReader.readFile("data/graph_data.csv"));
        write(ticker, (String) intervalComboBox.getValue(), "data/graph_info.txt");
    }

    @FXML
    protected void onShowTrendLineClick() {
        stockChartPane.showTrendLine();
    }

    @FXML
    protected void onHideTrendLineClick() {
        stockChartPane.hideTrendLine();
    }

    @FXML
    protected void onShowVolumeClick() {
        stockChartPane.showVolume();
    }

    @FXML
    protected void onHideVolumeClick() {
        stockChartPane.hideVolume();
    }

    @FXML
    protected void onStockChartClick() {
        if (pane.getChildren().contains(correlationChartPane)) {
            stockChartPane.draw(correlationChartPane.size());
            pane.getChildren().clear();
            pane.getChildren().add(stockChartPane);
        }
        // Persist toggle selection.
        stockViewToggleButton.setSelected(true);
    }

    @FXML
    protected void onCorrelationChartClick() {
        if (pane.getChildren().contains(stockChartPane)) {
            correlationChartPane.draw(stockChartPane.size());
            pane.getChildren().clear();
            pane.getChildren().add(correlationChartPane);
        }
        // Persist toggle selection.
        correlationViewToggleButton.setSelected(true);
    }

    private void set(String name, String interval, ArrayList[] stockData) {
        stockChartPane.setStock(name, interval, stockData);
        correlationChartPane.setStock(name, interval, stockData);
    }

    public void setDefault() {
        String ticker = "";
        String interval = "";

        // Retrieve graph info.
        try {
            List<String> lines = Files.readAllLines(Paths.get(Main.class.getResource("data/graph_info.txt").toString().substring(6)));
            ticker = lines.get(0);
            interval = lines.get(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set default ticker, combo box, stock values.
        tickerTextField.setText(ticker);
        intervalComboBox.setItems(FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        intervalComboBox.getSelectionModel().select(interval);
        set(ticker, interval, stockFileReader.readFile("data/graph_data.csv"));
    }

    private void draw(int size, int units) {
        if (pane.getChildren().contains(stockChartPane))
            stockChartPane.draw(size + units);
        else
            correlationChartPane.draw(size + units);
    }

    private String convertInterval() {
        if (intervalComboBox.getValue().equals("Daily"))
            return "1d";
        else if (intervalComboBox.getValue().equals("Weekly"))
            return "1wk";
        return "1mo";
    }

    private void write(String ticker, String interval, String localFile) {
        try {
            FileOutputStream stream = new FileOutputStream(Main.class.getResource(localFile).getFile());
            stream.write((ticker + "\n" + interval).getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stockFileReader = new StockFileReader();
        stockFileDownloader = new StockFileDownloader();
        stockChartPane = new StockChartPane();
        correlationChartPane = new CorrelationChartPane();

        pane.getChildren().add(stockChartPane);

        // Set scrolling behavior.
        pane.setOnScroll((ScrollEvent event) -> {
            int size = pane.getChildren().contains(stockChartPane) ? stockChartPane.size() : correlationChartPane.size();

            if (event.getDeltaY() < 0) {
                if (size <= 100)
                    draw(size, 10);
                else
                    draw(size, 50);
            } else {
                if (size <= 100)
                    draw(size, -10);
                else
                    draw(size, -50);
            }
        });
    }
}