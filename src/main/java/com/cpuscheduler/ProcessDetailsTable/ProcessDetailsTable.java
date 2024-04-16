package com.cpuscheduler.ProcessDetailsTable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.cpuscheduler.Utils.Process;

import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;

import com.cpuscheduler.App.SchedulerAlgorithm;

import javafx.beans.property.SimpleStringProperty;

public class ProcessDetailsTable extends TableView<String[]> {
    private final ObservableList<String[]> data = FXCollections.observableArrayList();

    public void switchAlgorithm(SchedulerAlgorithm algorithm) {
        switch (algorithm) {
            case NONE:
                resetTable();
                break;
            case FCFS:
                resetTable();
                setupFCFS();
                break;
            case NON_PREEMPTIVE_PRIORITY:
                resetTable();
                setupNonPreemptivePriority();
                break;
            case NON_PREEMPTIVE_SJF:
                resetTable();
                setupNonPreemptiveSJF();
                break;
            case PREEMPTIVE_PRIORITY:
                resetTable();
                setupPreemptivePriority();
                break;
            case PREEMPTIVE_SJF:
                resetTable();
                setupPreemptiveSJF();
                break;
            case RR:
                resetTable();
                setupRoundRobin();
                break;
            default:
                break;
        }
    }
    private void resetTable() {
        getItems().clear();
        getColumns().clear();
    }
    private void setupFCFS(){
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        setItems(data);

        getColumns().addAll(columns);
    }
    private void setupNonPreemptivePriority(){
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        TableColumn<String[], String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        priorityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(priorityColumn);

        setItems(data);

        getColumns().addAll(columns);
    }
    private void setupNonPreemptiveSJF() {
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        setItems(data);

        getColumns().addAll(columns);
    }
    private void setupPreemptivePriority() {
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        TableColumn<String[], String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        priorityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(priorityColumn);

        setItems(data);

        getColumns().addAll(columns);
    }
    private void setupPreemptiveSJF() {
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        setItems(data);

        getColumns().addAll(columns);
    }
    private void setupRoundRobin(){
        List<TableColumn<String[], ?>> columns = new ArrayList<>();

        TableColumn<String[], String> processColumn = new TableColumn<>("Process");
        processColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        processColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(processColumn);

        TableColumn<String[], String> arrivalTimeColumn = new TableColumn<>("Arrival Time");
        arrivalTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        arrivalTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(arrivalTimeColumn);

        TableColumn<String[], String> burstTimeColumn = new TableColumn<>("Burst Time");
        burstTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        burstTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columns.add(burstTimeColumn);

        setItems(data);

        getColumns().addAll(columns);
    }

    public void addProcess(SchedulerAlgorithm algorithm, Process process) {
        switch (algorithm) {
            case FCFS:
            case NON_PREEMPTIVE_SJF:
            case RR:
            case PREEMPTIVE_SJF:
                data.add(new String[] {
                    "P"+ process.getId(),
                        String.valueOf(process.getArrivalTime()),
                        String.valueOf(process.getBurstTime()) });
                break;
            case NON_PREEMPTIVE_PRIORITY:
            case PREEMPTIVE_PRIORITY:
                data.add(new String[] {
                        "P"+ process.getId(),
                        String.valueOf(process.getArrivalTime()),
                        String.valueOf(process.getBurstTime()),
                        String.valueOf(process.getPriority()) });
                break;
            default:
                break;
        }
    }

    public void place(Pane layout) {
        setPrefHeight(400);
        VBox.setMargin(this, new javafx.geometry.Insets(0, 50, 0, 50));
        layout.getChildren().add(this);
    }

    public ObservableList<String[]> getData() {
        return data;
    }
}
