package com.cpuscheduler.Icons;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class AddProcessButtonIcon extends Pane implements ButtonIcon {
    public void paint(Button button) {
        Canvas startButtonIconCanvas = new Canvas(40, 40);

        double centerX = startButtonIconCanvas.getWidth() * 0.5;
        double centerY = startButtonIconCanvas.getHeight() * 0.5;

        GraphicsContext gc = startButtonIconCanvas.getGraphicsContext2D();

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(centerX, centerY - 10.0, centerX, centerY + 10.0);
        gc.strokeLine(centerX - 10.0, centerY, centerX + 10.0, centerY);

        getChildren().add(startButtonIconCanvas);

        button.setGraphic(startButtonIconCanvas);

        startButtonIconCanvas = null;
    }
}



