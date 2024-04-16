package com.cpuscheduler.Icons;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class StartButtonIcon extends Pane implements ButtonIcon {
    public void paint(Button button) {
        Canvas startButtonIconCanvas = new Canvas(40, 40);

        double centerX = startButtonIconCanvas.getWidth() * 0.5;
        double centerY = startButtonIconCanvas.getHeight() * 0.5;

        GraphicsContext gc = startButtonIconCanvas.getGraphicsContext2D();
        
        gc.setFill(Color.GREEN);
        gc.fillPolygon(new double[] {centerX - 10.0, centerX - 10.0, centerX + 10.0},
         new double[] {centerY - 10.0, centerY + 10.0, centerY}, 3);

        getChildren().add(startButtonIconCanvas);

        button.setGraphic(startButtonIconCanvas);

        startButtonIconCanvas = null;
    }
}
