package com.cpuscheduler.Icons;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class PauseButtonIcon extends Pane implements ButtonIcon {
    public void paint(Button button) {
        Canvas startButtonIconCanvas = new Canvas(40, 40);

        double centerX = startButtonIconCanvas.getWidth() * 0.5;
        double centerY = startButtonIconCanvas.getHeight() * 0.5;

        GraphicsContext gc = startButtonIconCanvas.getGraphicsContext2D();

        gc.setLineWidth(5);
        gc.strokeLine(centerX - 5.0, centerY - 10.0, centerX - 5.0, centerY + 10.0);
        gc.strokeLine(centerX + 5.0, centerY - 10.0, centerX + 5.0, centerY + 10.0);

        getChildren().add(startButtonIconCanvas);

        button.setGraphic(startButtonIconCanvas);

        startButtonIconCanvas = null;
    }
}
