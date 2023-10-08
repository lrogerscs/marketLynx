package com.lrogerscs.marketlynx.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * CursorInfoPane displays stock data (related to cursor position) using a set of labels.
 *
 * @author Lee Rogers
 */
public class CursorInfoPane extends VBox {
    private Label open;
    private Label high;
    private Label low;
    private Label close;
    private Label change;
    private Label volume;

    /**
     * Default constructor. Initializes variables.
     */
    public CursorInfoPane() {
        open = new Label();
        high = new Label();
        low = new Label();
        close = new Label();
        change = new Label();
        volume = new Label();

        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

        getChildren().addAll(open, high, low, close, change, volume);
    }

    /**
     * Sets all labels with provided stock information.
     * @param open New open price.
     * @param high New high price.
     * @param low New low price.
     * @param close New close price.
     * @param change New percentage change.
     * @param volume New volume.
     */
    public void setLabels(double open, double high, double low, double close, double change, long volume) {
        this.open.setText("Open: " + String.format("%.2f", open));
        this.high.setText("High: " + String.format("%.2f", high));
        this.low.setText("Low: " + String.format("%.2f", low));
        this.close.setText("Close: " + String.format("%.2f", close));
        this.change.setText("Change: " + String.format("%.2f", change) + "%");
        this.volume.setText("Volume: " + NumberFormat.getInstance(Locale.US).format(volume));

        if (change > 0)
            this.change.setStyle("-fx-text-fill: #55f67e; -fx-font-weight: bold");
        else if (change < 0)
            this.change.setStyle("-fx-text-fill: #f20d46; -fx-font-weight: bold");
        else
            this.change.setStyle("-fx-text-fill: rgba(92, 87, 89, .95); -fx-font-weight: bold");
    }
}
