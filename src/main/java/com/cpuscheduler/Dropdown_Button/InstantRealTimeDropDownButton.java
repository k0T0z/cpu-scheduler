package com.cpuscheduler.Dropdown_Button;

import com.cpuscheduler.App.InstantRealTime;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

public class InstantRealTimeDropDownButton extends ComboBox<InstantRealTime> {

    public void place(Pane layout) {

        getItems().addAll(InstantRealTime.values());

        setValue(InstantRealTime.REAL_TIME);

        layout.getChildren().add(this);
    }
}
