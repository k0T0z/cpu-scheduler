package com.cpuscheduler;

import com.cpuscheduler.Add_Process_Dialog.AddProcessDialog;
import com.cpuscheduler.Dropdown_Button.DropdownButton;
import com.cpuscheduler.GanttChart.GanttChart;
import com.cpuscheduler.Icons.*;
import com.cpuscheduler.FCFS.FirstComeFirstServed;
import com.cpuscheduler.Round_Robin.RoundRobinScheduler;
import com.cpuscheduler.StatusBar.StatusBar;
import com.cpuscheduler.Non_Preemptive_SJF.SJFS;
import com.cpuscheduler.Preemptive_Priority.PreemptivePriority;
import com.cpuscheduler.ProcessDetailsTable.ProcessDetailsTable;
import com.cpuscheduler.RRQuantumSpinBox.RRQuantumSpinBox;
import com.cpuscheduler.Timer.Timer;
import com.cpuscheduler.Utils.Process;
import com.cpuscheduler.Utils.ProcessColor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    final String TITLE = "CPU Processes Scheduler";

    public enum SchedulerAlgorithm {
        NONE,
        FCFS,
        NON_PREEMPTIVE_PRIORITY,
        PREEMPTIVE_PRIORITY,
        NON_PREEMPTIVE_SJF,
        PREEMPTIVE_SJF,
        RR
    }

    public enum SchedulerState {
        INITIALIZATION,
        RUNNING,
        PAUSED,
        INVALID
    }

    private SchedulerAlgorithm currentSchedulerAlgorithm = SchedulerAlgorithm.NONE;
    private SchedulerState currentSchedulerState = SchedulerState.INVALID;

    /*
     * 
     * This variable keeps adding 1 each second.
     * 
     */
    private static int accumulativeSeconds = 0;

    /*
     * 
     * This variable gives id to every added process.
     */
    private int processesIdTracker = 0;

    AlgorithmType algorithmType;

    Timer timer = new Timer("00:00:00");

    private final Button startButton = new Button();
    private final ButtonIcon startButtonIcon = new StartButtonIcon();

    private final Button stopButton = new Button();
    private final ButtonIcon stopButtonIcon = new StopButtonIcon();

    private final Button pauseButton = new Button();
    private final ButtonIcon pauseButtonIcon = new PauseButtonIcon();

    private final Button continueButton = new Button();
    private final ButtonIcon continueButtonIcon = new ContinueButtonIcon();

    private final Button addProcessButton = new Button();
    private final ButtonIcon addProcessButtonIcon = new AddProcessButtonIcon();

    private final RRQuantumSpinBox rrQuantumSpinBox = new RRQuantumSpinBox();

    private final DropdownButton dropdownButton = new DropdownButton();

    private final ProcessDetailsTable processDetailsTable = new ProcessDetailsTable();

    StatusBar statusBar = new StatusBar();

    GanttChart ganttChart = new GanttChart();

    private final Button generateAverageWaitingTimeButton = new Button("Generate Average Waiting Time");

    private final Button generateAverageTurnaroundTimeButton = new Button("Generate Average Turnaround Time");

    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), this::handleTimelineEvent));

    public static int getCurrentTime() {
        return accumulativeSeconds;
    }
    public static int getLastTime() { return lastTime; }
    int processWidth = 0;
    int lastProcessId = -1;
    static int lastTime = -1;
    int tempRR = 0;
    Color lastColor = Color.BLACK;

    private void handleTimelineEvent(ActionEvent event) {

        if (algorithmType instanceof PreemptivePriority || algorithmType instanceof FirstComeFirstServed) {
            algorithmType.checkFutureArrivalProcessesInReadyQueue();
        }

        if(algorithmType instanceof RoundRobinScheduler) algorithmType.rearrangeProcesses();

        if (!(algorithmType.isCPUBuzy()) && lastProcessId != -1) {
            Label label = new Label("P" + lastProcessId);
            createRectangle(label);
            lastProcessId = -1;
            processWidth = 0;
        } else if (algorithmType.isCPUBuzy()
                && (lastProcessId != algorithmType.getCPUHookedProcess().getId()) && lastProcessId != -1) {
            Label label = new Label("P" + lastProcessId);
            createRectangle(label);
            processWidth = 0;
            lastProcessId = algorithmType.getCPUHookedProcess().getId();
            lastTime = App.getCurrentTime() + (algorithmType instanceof SJFS ? -1 : 0);
            lastColor = algorithmType.getCPUHookedProcess().getColor();
        }

        if (algorithmType.isCPUBuzy() && algorithmType.getCPUHookedProcess().getArrivalTime() <= App.getCurrentTime()) {
            if (tempRR > 0) {
                processWidth += 50;
                tempRR--;
            } else if (lastProcessId == -1) {
                if (algorithmType instanceof RoundRobinScheduler)
                    tempRR = Math.min(rrQuantumSpinBox.getValue(), algorithmType.getCPUHookedProcess().getBurstTime())
                            - 1;
                processWidth += 50;
                lastProcessId = algorithmType.getCPUHookedProcess().getId();
                lastTime = App.getCurrentTime() + (algorithmType instanceof SJFS ? -1 : 0);
                lastColor = algorithmType.getCPUHookedProcess().getColor();
            } else if (lastProcessId == algorithmType.getCPUHookedProcess().getId()) {
                if (algorithmType instanceof RoundRobinScheduler)
                    tempRR = Math.min(rrQuantumSpinBox.getValue(), algorithmType.getCPUHookedProcess().getBurstTime())
                            - 1;
                processWidth += 50;
            }
        } else {                                                                            // ready queue is empty and we're still counting
            if (!(algorithmType instanceof SJFS) || App.getCurrentTime() >= 1) {
                Rectangle rectangle = new Rectangle(50, 50);
                rectangle.setFill(Color.TRANSPARENT);
                Circle circle = new Circle(3);
                circle.setFill(Color.BLACK);
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(rectangle, circle);
                ganttChart.getChildren().add(stackPane);
            }
        }

        if (tempRR == 0 && algorithmType.isCPUBuzy())
            algorithmType.executeProcess();

        System.out.println("currentTime from app: " + App.getCurrentTime());

        accumulativeSeconds++;
        String hoursStr = String.format("%02d", (App.getCurrentTime() / 3600));
        String minutesStr = String.format("%02d", ((App.getCurrentTime() / 60) % 60));
        String secondsStr = String.format("%02d", (App.getCurrentTime() % 60));

        timer.setText(hoursStr + ':' + minutesStr + ':' + secondsStr);
    }

    private void createRectangle(Label label) {
        Rectangle rectangle = new Rectangle(processWidth, 50);
        rectangle.setFill(lastColor);
        HBox hbox = new HBox();
        hbox.setSpacing(0); // spacing between each box carrying the lines and label2
        Line line = new Line(0, 0, 0, 30);
        line.setStrokeWidth(1); // thickness
        hbox.getChildren().add(line);
        Label label2 = new Label(Integer.toString(lastTime));
        label2.setTranslateY(35);
        hbox.getChildren().add(label2);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(rectangle, label, hbox);
        ganttChart.getChildren().add(stackPane);

        System.out.println("tempX: " + processWidth);
    }

    private void updateLook() {
        switch (currentSchedulerState) {
            case INITIALIZATION:
                startButton.setDisable(false);
                stopButton.setDisable(true);
                pauseButton.setDisable(true);
                continueButton.setDisable(true);
                addProcessButton.setDisable(false);
                rrQuantumSpinBox.setDisable(currentSchedulerAlgorithm != SchedulerAlgorithm.RR);
                generateAverageWaitingTimeButton.setDisable(true);
                generateAverageTurnaroundTimeButton.setDisable(true);
                break;
            case PAUSED:
                startButton.setDisable(true);
                stopButton.setDisable(false);
                pauseButton.setDisable(true);
                continueButton.setDisable(false);
                addProcessButton.setDisable(false);
                rrQuantumSpinBox.setDisable(currentSchedulerAlgorithm != SchedulerAlgorithm.RR);
                generateAverageWaitingTimeButton.setDisable(false);
                generateAverageTurnaroundTimeButton.setDisable(false);
                break;
            case RUNNING:
                startButton.setDisable(true);
                stopButton.setDisable(false);
                pauseButton.setDisable(false);
                continueButton.setDisable(true);
                addProcessButton.setDisable(false);
                rrQuantumSpinBox.setDisable(true);
                generateAverageWaitingTimeButton.setDisable(true);
                generateAverageTurnaroundTimeButton.setDisable(true);
                break;
            case INVALID:
                startButton.setDisable(true);
                stopButton.setDisable(true);
                pauseButton.setDisable(true);
                continueButton.setDisable(true);
                addProcessButton.setDisable(true);
                rrQuantumSpinBox.setDisable(true);
                generateAverageWaitingTimeButton.setDisable(true);
                generateAverageTurnaroundTimeButton.setDisable(true);
                break;
            default:
                startButton.setDisable(true);
                stopButton.setDisable(true);
                pauseButton.setDisable(true);
                continueButton.setDisable(true);
                addProcessButton.setDisable(true);
                rrQuantumSpinBox.setDisable(true);
                generateAverageWaitingTimeButton.setDisable(true);
                generateAverageTurnaroundTimeButton.setDisable(true);
                break;
        }
    }

    private void handleStartButtonPress(MouseEvent event) {
        currentSchedulerState = SchedulerState.RUNNING;
        updateLook();

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void handleStopButtonPress(MouseEvent event) {
        currentSchedulerState = SchedulerState.INITIALIZATION;
        updateLook();

        timeline.stop();
    }

    private void handlePauseButtonPress(MouseEvent event) {
        currentSchedulerState = SchedulerState.PAUSED;
        updateLook();

        timeline.pause();
    }

    private void handleContinueButtonPress(MouseEvent event) {
        currentSchedulerState = SchedulerState.RUNNING;
        updateLook();

        timeline.play();
    }

    private void handleAddProcessButtonPress(MouseEvent event) {
        if (currentSchedulerState == SchedulerState.RUNNING)
            timeline.pause();

         BooleanWrapper isSaved = new BooleanWrapper(false);

         StringWrapper processPriority = new StringWrapper();
         StringWrapper processBurst = new StringWrapper();
         ProcessColor processColor = new ProcessColor(Color.RED);
         BooleanWrapper isFutureProcess = new BooleanWrapper(false);
         StringWrapper processArrival = new StringWrapper();
         AddProcessDialog addProcessDialog = new
         AddProcessDialog(currentSchedulerAlgorithm, isSaved, processPriority,
         processBurst, processColor,
         isFutureProcess, processArrival);

         addProcessDialog.showDialog();

         if (!(isSaved.value)) {
         return;
         }

         int priority, burst;

         burst = Integer.parseInt(processBurst.value);

         Process process;

         if (currentSchedulerAlgorithm == SchedulerAlgorithm.PREEMPTIVE_PRIORITY
         || currentSchedulerAlgorithm == SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY) {

         priority = Integer.parseInt(processPriority.value);

         process = new Process(
         ++processesIdTracker,
         (isFutureProcess.value) ? Integer.parseInt(processArrival.value) :
         App.getCurrentTime(),
         burst,
         priority,
         processColor.getColor());
         } else {
         process = new Process(
         ++processesIdTracker,
         (isFutureProcess.value) ? Integer.parseInt(processArrival.value) :
         App.getCurrentTime(),
         burst,
         processColor.getColor());
         }

         processDetailsTable.addProcess(currentSchedulerAlgorithm, process);

         algorithmType.addProcessToReadyQueue(process);

        // processDetailsTable.addProcess(currentSchedulerAlgorithm, new Process(0, 0,
        // 3, Color.rgb(135, 206, 250)));
        // processDetailsTable.addProcess(currentSchedulerAlgorithm, new Process(1, 4,
        // 5, Color.rgb(135, 206, 250)));
        // algorithmType.addProcessToReadyQueue(new Process(0, 0, 3,Color.rgb(135, 206,
        // 250)));
        // algorithmType.addProcessToReadyQueue(new Process(1, 4, 5,Color.rgb(135, 206,
        // 250)));

        ///*
        // Non Preemptive Priority Testcases
//        Process process1 = new Process(0, 0, 5,1, Color.rgb(135, 206, 250));

//        Process process2 = new Process(1, 1000, 5,4, Color.rgb(135, 206, 250));
//        Process process3 = new Process(2, 6, 4,3, Color.rgb(135, 206, 250));
//        Process process4 = new Process(3, 0, 3,2, Color.rgb(135, 206, 250));
//        Process process5 = new Process(4, 6, 2,1, Color.rgb(135, 206, 250));
//        Process process6 = new Process(5, 5, 4,0, Color.rgb(135, 206, 250));
//
////         Process process7 = new Process(6,0, 3,1, Color.rgb(135, 206, 250));
//
//        // Process process8 = new Process(7, 0, 2,1, Color.rgb(135, 206, 250));
//        // Process process9 = new Process(8, 5, 3,1, Color.rgb(135, 206, 250));
//        // Process process10 = new Process(9, 6, 4,1, Color.rgb(135, 206, 250));
//
//        // Process process11 = new Process(10, 0, 3,3, Color.rgb(135, 206, 250));
//        // Process process12 = new Process(11, 1, 3,2, Color.rgb(135, 206, 250));
//        // Process process13 = new Process(12, 2, 3,1, Color.rgb(135, 206, 250));
//
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process1);
//
//        processDetailsTable.addProcess(currentSchedulerAlgorithm, process2);
//        processDetailsTable.addProcess(currentSchedulerAlgorithm, process3);
//        processDetailsTable.addProcess(currentSchedulerAlgorithm, process4);
////
//        processDetailsTable.addProcess(currentSchedulerAlgorithm, process5);
//        processDetailsTable.addProcess(currentSchedulerAlgorithm, process6);
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process7);
//
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process8);
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process9);
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process10);
//
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process11);
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process12);
//        // processDetailsTable.addProcess(currentSchedulerAlgorithm, process13);
//
//        // algorithmType.addProcessToReadyQueue(process1);
//
//        algorithmType.addProcessToReadyQueue(process2);
//        algorithmType.addProcessToReadyQueue(process3);
//        algorithmType.addProcessToReadyQueue(process4);
//
//        algorithmType.addProcessToReadyQueue(process5);
//        algorithmType.addProcessToReadyQueue(process6);
        // algorithmType.addProcessToReadyQueue(process7);

        // algorithmType.addProcessToReadyQueue(process8);
        // algorithmType.addProcessToReadyQueue(process9);
        // algorithmType.addProcessToReadyQueue(process10);

        // algorithmType.addProcessToReadyQueue(process11);
        // algorithmType.addProcessToReadyQueue(process12);
        // algorithmType.addProcessToReadyQueue(process13);
        //*/

        if (currentSchedulerState == SchedulerState.RUNNING)
            timeline.play();
    }

    private void dropdownOnAction(ActionEvent event) {
        ganttChart.Clear();
        timeline.stop();
        accumulativeSeconds = 0;
        timer.reset();
        ComboBox<SchedulerAlgorithm> source = (ComboBox<SchedulerAlgorithm>) event.getSource();
        SchedulerAlgorithm selectedValue = source.getSelectionModel().getSelectedItem();
        switch (selectedValue) {
            case NONE:
                processesIdTracker = 0;
                algorithmType = null;
                currentSchedulerAlgorithm = SchedulerAlgorithm.NONE;
                currentSchedulerState = SchedulerState.INVALID;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NONE);
                break;
            case FCFS:
                processesIdTracker = 0;
                algorithmType = new FirstComeFirstServed();
                currentSchedulerAlgorithm = SchedulerAlgorithm.FCFS;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.FCFS);
                break;
            case NON_PREEMPTIVE_PRIORITY:
                processesIdTracker = 0;
                algorithmType = new PreemptivePriority(false);
                currentSchedulerAlgorithm = SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY);
                break;
            case NON_PREEMPTIVE_SJF:
                processesIdTracker = 0;
                algorithmType = new SJFS(false);
                currentSchedulerAlgorithm = SchedulerAlgorithm.NON_PREEMPTIVE_SJF;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NON_PREEMPTIVE_SJF);
                break;
            case PREEMPTIVE_PRIORITY:
                processesIdTracker = 0;
                algorithmType = new PreemptivePriority(true);
                currentSchedulerAlgorithm = SchedulerAlgorithm.PREEMPTIVE_PRIORITY;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.PREEMPTIVE_PRIORITY);
                break;
            case PREEMPTIVE_SJF:
                processesIdTracker = 0;
                algorithmType = new SJFS(true);
                currentSchedulerAlgorithm = SchedulerAlgorithm.PREEMPTIVE_SJF;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NON_PREEMPTIVE_SJF);
                break;
            case RR:
                processesIdTracker = 0;
                algorithmType = new RoundRobinScheduler(2);
                currentSchedulerAlgorithm = SchedulerAlgorithm.RR;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.RR);
                break;
            default:
                break;
        }
    }

    private void handleGenerateAverageWaitingTimeButtonPress(MouseEvent event) {
        double averageWaitingTime = algorithmType.getAverageWaitingTime();
        statusBar.updateAverageWaitingTime(averageWaitingTime);
    }

    private void handleGenerateAverageTurnaroundTimeButtonPress(MouseEvent event) {
        double averageTurnaroundTime = algorithmType.getAverageTurnaroundTime();
        statusBar.updateAverageTurnaroundTime(averageTurnaroundTime);
    }

    public static class StringWrapper {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class BooleanWrapper {
        private boolean value;

        public BooleanWrapper(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        VBox mainLayout = new VBox();
        mainLayout.setSpacing(10);

        timer.place(mainLayout);

        updateLook();

        startButtonIcon.paint(startButton);
        startButton.setOnMousePressed(this::handleStartButtonPress);

        stopButtonIcon.paint(stopButton);
        stopButton.setOnMousePressed(this::handleStopButtonPress);

        pauseButtonIcon.paint(pauseButton);
        pauseButton.setOnMousePressed(this::handlePauseButtonPress);

        continueButtonIcon.paint(continueButton);
        continueButton.setOnMousePressed(this::handleContinueButtonPress);

        addProcessButtonIcon.paint(addProcessButton);
        addProcessButton.setOnMousePressed(this::handleAddProcessButtonPress);

        HBox schedulersControllers = new HBox();

        schedulersControllers.setAlignment(Pos.CENTER);

        schedulersControllers.getChildren().addAll(startButton, stopButton, pauseButton, continueButton,
                addProcessButton);

        rrQuantumSpinBox.place(schedulersControllers);

        rrQuantumSpinBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            ((RoundRobinScheduler) algorithmType).setQuantum(rrQuantumSpinBox.getValue());
        });

        schedulersControllers.setSpacing(10);

        mainLayout.getChildren().add(schedulersControllers);

        dropdownButton.setOnAction(this::dropdownOnAction);
        dropdownButton.place(mainLayout);

        processDetailsTable.place(mainLayout);

        statusBar.place(mainLayout);

        ganttChart.place(mainLayout);

        generateAverageWaitingTimeButton.setOnMousePressed(this::handleGenerateAverageWaitingTimeButtonPress);
        generateAverageTurnaroundTimeButton.setOnMousePressed(this::handleGenerateAverageTurnaroundTimeButtonPress);

        HBox schedulersControllers2 = new HBox();

        schedulersControllers2.setSpacing(20);

        VBox.setMargin(schedulersControllers2, new javafx.geometry.Insets(0, 50, 20, 50));

        schedulersControllers2.setAlignment(Pos.CENTER);

        schedulersControllers2.getChildren().addAll(generateAverageWaitingTimeButton,
                generateAverageTurnaroundTimeButton);

        mainLayout.getChildren().add(schedulersControllers2);

        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 640, 480);
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
