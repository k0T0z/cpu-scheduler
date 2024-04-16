package com.cpuscheduler.GanttChart;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class GanttChart extends HBox {

    private final ScrollPane scrollPane = new ScrollPane();

    public void place(Pane layout) {
        setSpacing(5);

        scrollPane.setPrefHeight(200);
        VBox.setMargin(scrollPane, new javafx.geometry.Insets(0, 50, 0, 50));
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(this);

        layout.getChildren().add(scrollPane);
    }
    public void Clear(){
        getChildren().clear();
    }

    public void adjustView() {
        scrollPane.setHvalue(1.0);
    }
    
}
