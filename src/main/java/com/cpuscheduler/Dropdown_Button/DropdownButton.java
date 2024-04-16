package com.cpuscheduler.Dropdown_Button;

import com.cpuscheduler.App.SchedulerAlgorithm;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DropdownButton extends ComboBox<SchedulerAlgorithm> {

    public void place(Pane layout) {

        getItems().addAll(SchedulerAlgorithm.values());

        setValue(SchedulerAlgorithm.NONE);

        layout.getChildren().add(this);
    }
}
