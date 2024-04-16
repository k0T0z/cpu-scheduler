package com.cpuscheduler.Add_Process_Dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;

import com.cpuscheduler.App.BooleanWrapper;
import com.cpuscheduler.App.SchedulerAlgorithm;
import com.cpuscheduler.App.StringWrapper;
import com.cpuscheduler.Utils.Process;
import com.cpuscheduler.Utils.ProcessColor;

public class AddProcessDialog extends Stage {
    private final Spinner<Integer> processPrioritySpinner = new Spinner<Integer>();
    private final Spinner<Integer> processBurstSpinner = new Spinner<Integer>();
    private final ColorPicker processColorPicker = new ColorPicker(Color.RED);
    private final Spinner<Integer> processArrivalSpinner = new Spinner<Integer>();

    BooleanWrapper isSaved;
    StringWrapper processPriority;
    StringWrapper processBurst;
    ProcessColor processColor;
    BooleanWrapper isFutureProcess;
    StringWrapper processArrival;

    private SchedulerAlgorithm algorithm;

    public AddProcessDialog(SchedulerAlgorithm algorithm, BooleanWrapper isSaved, StringWrapper processPriority,
            StringWrapper processBurst, ProcessColor processColor, BooleanWrapper isFutureProcess,
            StringWrapper processArrival) {
        this.isSaved = isSaved;
        this.processPriority = processPriority;
        this.processBurst = processBurst;
        this.processColor = processColor;
        this.isFutureProcess = isFutureProcess;
        this.processArrival = processArrival;

        this.algorithm = algorithm;
    }

    private void handleSaveButtonPress(MouseEvent event) {

        if (algorithm == SchedulerAlgorithm.PREEMPTIVE_PRIORITY
                || algorithm == SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY)
            processPriority.setValue(processPrioritySpinner.getValue().toString());
        processBurst.setValue(processBurstSpinner.getValue().toString());
        processColor.setColor(processColorPicker.getValue());
        processArrival.setValue(processArrivalSpinner.getValue().toString());

        isSaved.setValue(true);

        close();
    }

    public void showDialog() {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Add Process");

        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);

        if (algorithm == SchedulerAlgorithm.PREEMPTIVE_PRIORITY
                || algorithm == SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY) {
            HBox processPriorityHBox = new HBox();
            processPriorityHBox.setSpacing(20);
            VBox.setMargin(processPriorityHBox, new javafx.geometry.Insets(10, 10, 0, 10));
            Label processPriorityLabel = new Label("Process Priority:");
            processPriorityLabel.setTextAlignment(TextAlignment.CENTER);
            SpinnerValueFactory<Integer> processPrioritySpinnerFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, 7, 0, 1);
            processPrioritySpinner.setValueFactory(processPrioritySpinnerFactory);
            processPrioritySpinner.setEditable(true);
            processPriorityHBox.getChildren().addAll(processPriorityLabel, processPrioritySpinner);

            mainLayout.getChildren().add(processPriorityHBox);

            processPriority.setValue(processPrioritySpinner.getValue().toString());
        }

        HBox processBurstHBox = new HBox();
        processBurstHBox.setSpacing(20);
        if (algorithm == SchedulerAlgorithm.PREEMPTIVE_PRIORITY
                || algorithm == SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY)
            VBox.setMargin(processBurstHBox, new javafx.geometry.Insets(0, 10, 0, 10));
        else
            VBox.setMargin(processBurstHBox, new javafx.geometry.Insets(10, 10, 0, 10));
        Label processBurstLabel = new Label("Process Burst:");
        processBurstLabel.setTextAlignment(TextAlignment.CENTER);
        SpinnerValueFactory<Integer> processBurstSpinnerFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                50, 1, 1);
        processBurstSpinner.setValueFactory(processBurstSpinnerFactory);
        processBurstSpinner.setEditable(true);
        processBurstHBox.getChildren().addAll(processBurstLabel, processBurstSpinner);
        processBurst.setValue(processBurstSpinner.getValue().toString());

        HBox processColorHBox = new HBox();
        processColorHBox.setSpacing(20);
        VBox.setMargin(processColorHBox, new javafx.geometry.Insets(0, 10, 20, 10));
        Label processColorLabel = new Label("Process Color:");
        processColorLabel.setTextAlignment(TextAlignment.CENTER);
        processColorHBox.getChildren().addAll(processColorLabel, processColorPicker);

        HBox processArrivalHBox = new HBox();
        processArrivalHBox.setSpacing(20);
        VBox.setMargin(processArrivalHBox, new javafx.geometry.Insets(0, 10, 20, 10));
        Label processArrivalLabel = new Label("Process Arrival:");
        processArrivalLabel.setTextAlignment(TextAlignment.CENTER);

        processArrivalLabel.setDisable(true);
        processArrivalSpinner.setDisable(true);

        CheckBox checkBox = new CheckBox();

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                processArrivalLabel.setDisable(false);
                processArrivalSpinner.setDisable(false);

            } else {
                processArrivalLabel.setDisable(true);
                processArrivalSpinner.setDisable(true);
            }
            isFutureProcess.setValue(newVal);
        });

        SpinnerValueFactory<Integer> processArrivalSpinnerFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 100, 0, 1);
        processArrivalSpinner.setValueFactory(processArrivalSpinnerFactory);
        processArrivalSpinner.setEditable(true);
        processArrivalHBox.getChildren().addAll(processArrivalLabel, processArrivalSpinner, checkBox);
        processArrival.setValue(processArrivalSpinner.getValue().toString());

        HBox saveButtonHBox = new HBox();
        saveButtonHBox.setAlignment(Pos.CENTER);
        Button saveButton = new Button("Save");
        saveButton.setOnMousePressed(this::handleSaveButtonPress);
        saveButtonHBox.getChildren().add(saveButton);

        mainLayout.getChildren().addAll(processBurstHBox, processColorHBox, processArrivalHBox, saveButtonHBox);

        Scene addProcessDialogScene = new Scene(mainLayout, 320, 240);

        setScene(addProcessDialogScene);

        showAndWait();
    }

}
