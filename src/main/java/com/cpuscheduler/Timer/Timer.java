package com.cpuscheduler.Timer;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Timer extends Label {

    public Timer (String initialValue) {
        super(initialValue);
    }

    public void place(Pane layout) {
        VBox.setMargin(this, new javafx.geometry.Insets(10, 0, 0, 0));

        setFont(Font.font("Arial", FontWeight.BOLD, 24));

        layout.getChildren().add(this);
    }

    public void reset() {
        setText("00:00:00");
    }
}
