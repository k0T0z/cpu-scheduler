package com.cpuscheduler.Icons;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class StopButtonIcon extends Pane implements ButtonIcon {
    public void paint(Button button) {
        Canvas startButtonIconCanvas = new Canvas(40, 40);

        double centerX = startButtonIconCanvas.getWidth() * 0.5;
        double centerY = startButtonIconCanvas.getHeight() * 0.5;

        double sideLength = Math.min(centerX, centerY) * 0.5;

        GraphicsContext gc = startButtonIconCanvas.getGraphicsContext2D();

        gc.setFill(Color.RED);
        gc.fillRect(centerX - sideLength, centerY - sideLength, sideLength * 2, sideLength * 2);

        getChildren().add(startButtonIconCanvas);

        button.setGraphic(startButtonIconCanvas);

        startButtonIconCanvas = null;
    }
}

