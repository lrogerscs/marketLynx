package com.lrogerscs.marketlynx.pane;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * ChartOverlayPane highlights (significant) chart data by implementing a cross-hair, line, and labels.
 *
 * @author Lee Rogers
 */
public class ChartOverlayPane extends Pane {
    private Label xLabel = new Label(), yLabel = new Label(), endLabel = new Label();
    private Line horizontalLine = new Line(), verticalLine = new Line(), endLine = new Line();
    private Circle endCircle = new Circle(5);

    /**
     * Default constructor. Sets styles, adds children.
     */
    public ChartOverlayPane() {
        xLabel.getStyleClass().add("cursor-tick-label");
        yLabel.getStyleClass().add("cursor-tick-label");
        endLabel.getStyleClass().add("final-price-label");
        verticalLine.getStyleClass().add("line");
        horizontalLine.getStyleClass().add("line");
        endLine.getStyleClass().add("line");

        getChildren().addAll(endLabel, xLabel, yLabel, horizontalLine, verticalLine, endLine, endCircle);
    }

    /**
     * Returns the current yLabel text value.
     * @return Current text of yLabel.
     */
    public String getY() {
        return yLabel.getText();
    }

    /**
     * Sets the text and position of yLabel.
     * @param text Text to be set.
     * @param x New x position.
     * @param y New y position.
     */
    public void setYLabel(String text, double x, double y) {
        yLabel.setText(text);
        yLabel.setLayoutX(x);
        yLabel.setLayoutY(y - yLabel.getHeight() / 2);
    }

    /**
     * Sets the text and position of xLabel.
     * @param text Text to be set.
     * @param x New x position.
     * @param y New y position.
     */
    public void setXLabel(String text, double x, double y) {
        xLabel.setText(text);
        xLabel.setLayoutX(x - xLabel.getWidth() / 2);
        xLabel.setLayoutY(y);
    }

    /**
     * Sets the position of horizontalLine.
     * @param startX New starting x position.
     * @param startY New starting y position.
     * @param endX New end X position.
     * @param endY New end Y position.
     */
    public void setHorizontalLine(double startX, double startY, double endX, double endY) {
        horizontalLine.setStartX(startX);
        horizontalLine.setStartY(startY);
        horizontalLine.setEndX(endX);
        horizontalLine.setEndY(endY);
    }

    /**
     * Sets the position of verticalLine.
     * @param startX New starting x position.
     * @param startY New starting y position.
     * @param endX New end X position.
     * @param endY New end Y position.
     */
    public void setVerticalLine(double startX, double startY, double endX, double endY) {
        verticalLine.setStartX(startX);
        verticalLine.setStartY(startY);
        verticalLine.setEndX(endX);
        verticalLine.setEndY(endY);
    }

    /**
     * Sets the text, position, and color of endLabel.
     * @param text Text to be set.
     * @param x New x position.
     * @param y New y position.
     * @param color New color to be set.
     */
    public void setEndLabel(String text, double x, double y, String color) {
        endLabel.setText(text);
        endLabel.setLayoutX(x);
        endLabel.setLayoutY(y - endLabel.getHeight() / 2);
        endLabel.setStyle(color);
    }

    /**
     * Sets the position and color of endLine.
     * @param startX New starting x position.
     * @param startY New starting y position.
     * @param endX New end X position.
     * @param endY New end Y position.
     * @param color Color to be set.
     */
    public void setEndLine(double startX, double startY, double endX, double endY, String color) {
        endLine.setStartX(startX);
        endLine.setStartY(startY);
        endLine.setEndX(endX);
        endLine.setEndY(endY);
        endLine.setStyle(color);
    }

    /**
     * Sets the position and color of endCircle.
     * @param x New x position.
     * @param y New y position.
     * @param color Color to be set.
     */
    public void setEndCircle(double x, double y, String color) {
        endCircle.setLayoutX(x);
        endCircle.setLayoutY(y);
        endCircle.setStyle(color);
    }

    /**
     * Shows the cross-hair and related labels.
     */
    public void showCrossHair() {
        yLabel.setVisible(true);
        xLabel.setVisible(true);
        verticalLine.setVisible(true);
        horizontalLine.setVisible(true);
    }

    /**
     * Hides the cross-hair and related labels.
     */
    public void hideCrossHair() {
        yLabel.setVisible(false);
        xLabel.setVisible(false);
        verticalLine.setVisible(false);
        horizontalLine.setVisible(false);
    }
}
