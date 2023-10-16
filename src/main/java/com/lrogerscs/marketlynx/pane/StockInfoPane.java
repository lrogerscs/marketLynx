package com.lrogerscs.marketlynx.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * StockInfoPane displays (general) stock data over a period of time.
 *
 * @author Lee Rogers
 */
public class StockInfoPane extends HBox {
    private Label start = new Label("Start: ");
    private Label end = new Label("End: ");
    private Label average = new Label("Average Price: ");
    private Label max = new Label("Maximum Price: ");
    private Label min = new Label(" Minimum Price: ");
    private Label volatility = new Label("Volatility: ");
    private Label change = new Label("Change: ");

    /**
     * Default constructor. Initializes variables.
     */
    public StockInfoPane() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

        getChildren().addAll(start, end, average, max, min, volatility, change);
    }

    /**
     * Sets all labels with provided information.
     * @param start New period starting date.
     * @param end New period ending date.
     * @param average New average price over period.
     * @param max New maximum over period.
     * @param min New minimum over period.
     * @param volatility New volatility over period.
     * @param change New percentage change over period.
     */
    public void setLabels(String start, String end, double average, double max, double min, double volatility, double change) {
        this.start.setText("Start: " + start);
        this.end.setText("End: " + end);
        this.average.setText("Average: " + String.format("%.2f", average));
        this.max.setText("Max: " + String.format("%.2f", max));
        this.min.setText(" Min: " + String.format("%.2f", min));
        this.volatility.setText("Volatility: " + String.format("%.2f", volatility));
        this.change.setText("Change: " + String.format("%.2f", change) + "%");

        if (change > 0)
            this.change.setStyle("-fx-text-fill: #55f67e; -fx-font-weight: bold");
        else if (change < 0)
            this.change.setStyle("-fx-text-fill: #f20d46; -fx-font-weight: bold");
        else
            this.change.setStyle("-fx-text-fill: rgba(92, 87, 89, .95); -fx-font-weight: bold");
    }
}
