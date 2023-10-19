package com.lrogerscs.marketlynx.controller;

import com.lrogerscs.marketlynx.downloader.StockFileDownloader;
import com.lrogerscs.marketlynx.reader.DownloadReader;
import com.lrogerscs.marketlynx.reader.StockFileReader;
import com.lrogerscs.marketlynx.pane.CorrelationChartPane;
import com.lrogerscs.marketlynx.pane.StockChartPane;
import com.lrogerscs.marketlynx.writer.DownloadWriter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
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
    private ToggleButton stockViewToggleButton, correlationViewToggleButton;

    private StockChartPane stockChartPane = new StockChartPane();
    private CorrelationChartPane correlationChartPane = new CorrelationChartPane();
    private StockFileDownloader stockFileDownloader = new StockFileDownloader();
    private StockFileReader stockFileReader = new StockFileReader();
    private DownloadReader downloadReader = new DownloadReader();
    private DownloadWriter downloadWriter = new DownloadWriter();

    @FXML
    protected void onDownloadButtonClick() {
        String ticker = tickerTextField.getText();
        long period1 = LocalDateTime.of(1970, 1, 1, 23, 59).toEpochSecond(ZoneOffset.UTC);
        long period2 = LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC);
        String interval = "1mo";

        if (intervalComboBox.getValue().equals("Daily"))
            interval = "1d";
        else if (intervalComboBox.getValue().equals("Weekly"))
            interval = "1wk";

        stockFileDownloader.downloadFile("https://query1.finance.yahoo.com/v7/finance/download/" + ticker
                + "?period1=" + period1 + "&period2=" + period2 + "&interval=" + interval
                + "&events=history&includeAdjustedClose=true", "./graph/graph_data.csv");
        set(ticker, interval, stockFileReader.readFile("./graph/graph_data.csv"));
        downloadWriter.write(ticker, (String) intervalComboBox.getValue(), "./graph/graph_info.txt");
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
        List<String> graphInfo = downloadReader.read("./graph/graph_info.txt");

        // Set default ticker, combo box, stock values.
        tickerTextField.setText(graphInfo.get(0));
        intervalComboBox.setItems(FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        intervalComboBox.getSelectionModel().select(graphInfo.get(1));
        set(graphInfo.get(0), graphInfo.get(1), stockFileReader.readFile("./graph/graph_data.csv"));
    }

    private void draw(int size, int units) {
        if (pane.getChildren().contains(stockChartPane))
            stockChartPane.draw(size + units);
        else
            correlationChartPane.draw(size + units);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
