package com.cpuscheduler;

import com.cpuscheduler.Add_Process_Dialog.AddProcessDialog;
import com.cpuscheduler.Dropdown_Button.AlgorithmsDropdownButton;
import com.cpuscheduler.Dropdown_Button.InstantRealTimeDropDownButton;
import com.cpuscheduler.GanttChart.GanttChart;
import com.cpuscheduler.Icons.*;
import com.cpuscheduler.FCFS.FirstComeFirstServed;
import com.cpuscheduler.Round_Robin.RoundRobinScheduler;
import com.cpuscheduler.StatusBar.StatusBar;
import com.cpuscheduler.Non_Preemptive_SJF.NonPreemptiveSJF;
import com.cpuscheduler.Preemptive_SJF.PreemptiveSJF;
import com.cpuscheduler.Non_Preemptive_Priority.NonPreemptivePriority;
import com.cpuscheduler.Preemptive_Priority.PreemptivePriority;
import com.cpuscheduler.ProcessDetailsTable.ProcessDetailsTable;
import com.cpuscheduler.RRQuantumSpinBox.RRQuantumSpinBox;
import com.cpuscheduler.Timer.Timer;
import com.cpuscheduler.Utils.Process;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.cpuscheduler.AlgorithmType.ExecutionResult;
import javafx.geometry.Insets;
import java.util.Random;

import java.io.IOException;

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

    public enum InstantRealTime {
        REAL_TIME,
        INSTANT
    }

    public enum SchedulerState {
        INITIALIZATION,
        RUNNING,
        PAUSED,
        DONE, // For instant
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

    private final AlgorithmsDropdownButton algorithms_dropdownButton = new AlgorithmsDropdownButton();

    private final InstantRealTimeDropDownButton instantRealTimeSwitch = new InstantRealTimeDropDownButton();

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

    int currentProcessWidth = 25; // 25 px is 1 second (width)
    int currentProcessId = -1;
    Rectangle currentProcessRectangle = null;
    StackPane currentProcessStackPane = null;

    private void handleTimelineEvent(ActionEvent event) {
        // Kill the timer if it is instant and the algorithm is done
        if (instantRealTimeSwitch.getValue() == InstantRealTime.INSTANT 
            && currentSchedulerState == SchedulerState.RUNNING
            && !algorithmType.isCPUBuzy()
            && algorithmType.isReadyQueueEmpty()) {
            timeline.stop();

            // pause the timer
            currentSchedulerState = SchedulerState.DONE;
            updateLook();
        }

        ExecutionResult result = algorithmType.executeProcess();

        switch (result) {
            case CPU_IDLE:
                Rectangle rectangle = new Rectangle(25, 50); // 25 px is 1 second (width)
                rectangle.setFill(Color.TRANSPARENT);
                Circle circle = new Circle(3);
                circle.setFill(Color.BLACK);
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(rectangle, circle);
                ganttChart.getChildren().add(stackPane);
                break;
            case PROCESS_EXECUTED:
                if (currentProcessId == -1) {
                    int newProcessId = algorithmType.getCPUHookedProcess().getId();
                    Color newColor = algorithmType.getCPUHookedProcess().getColor();
                    Label label = new Label("P" + newProcessId);
                    label.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");
                    currentProcessRectangle = addRectangle(label, newColor);
                    currentProcessId = newProcessId;
                }
                else if (currentProcessId != algorithmType.getCPUHookedProcess().getId()) {
                    // A process is preempted here.
                    // A new process is replaced with the old one.

                    // Because we didn't cleared the context yet, we need to add the label for the current time.
                    // Note that the current time is the time when the process is finished. It is App.getCurrentTime() + 1 - 1 which
                    // is App.getCurrentTime().
                    Label currentTimeLabel = new Label(Integer.toString(App.getCurrentTime()));
                    currentTimeLabel.setStyle("-fx-font-weight: bold;");
                    currentProcessStackPane.getChildren().add(currentTimeLabel);
                    StackPane.setAlignment(currentTimeLabel, javafx.geometry.Pos.BOTTOM_RIGHT);
                    StackPane.setMargin(currentTimeLabel, new Insets(5));

                    currentProcessWidth = 25; // Reset the width
                    int newProcessId = algorithmType.getCPUHookedProcess().getId();
                    Color newColor = algorithmType.getCPUHookedProcess().getColor();
                    Label label = new Label("P" + newProcessId);
                    label.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");
                    currentProcessRectangle = addRectangle(label, newColor);
                    currentProcessId = newProcessId;
                }
                else {
                    currentProcessWidth += 25;
                    currentProcessRectangle.setWidth(currentProcessWidth);
                }
                break;
            case PROCESS_FINISHED:
                if (currentProcessId == -1) {
                    // A special case when a new process of burst 1 is executed and finished.
                    int newProcessId = algorithmType.getCPUHookedProcess().getId();
                    Color newColor = algorithmType.getCPUHookedProcess().getColor();
                    Label label = new Label("P" + newProcessId);
                    label.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");
                    currentProcessRectangle = addRectangle(label, newColor);
                    currentProcessId = newProcessId;
                }
                else if (currentProcessId != algorithmType.getCPUHookedProcess().getId()) {
                    // A special case when the current process is preempted and a new process of burst 1 is executed and finished.
                    // A new process is replaced with the old one.

                    // Because we didn't cleared the context yet, we need to add the label for the current time.
                    // Note that the current time is the time when the process is finished. It is App.getCurrentTime() + 1 - 1 which
                    // is App.getCurrentTime().
                    Label currentTimeLabel = new Label(Integer.toString(App.getCurrentTime()));
                    currentTimeLabel.setStyle("-fx-font-weight: bold;");
                    currentProcessStackPane.getChildren().add(currentTimeLabel);
                    StackPane.setAlignment(currentTimeLabel, javafx.geometry.Pos.BOTTOM_RIGHT);
                    StackPane.setMargin(currentTimeLabel, new Insets(5));

                    currentProcessWidth = 0; // Reset the width; Zero because it is a preempted process.
                    int newProcessId = algorithmType.getCPUHookedProcess().getId();
                    Color newColor = algorithmType.getCPUHookedProcess().getColor();
                    Label label = new Label("P" + newProcessId);
                    label.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");
                    currentProcessRectangle = addRectangle(label, newColor);
                    currentProcessId = newProcessId;
                }

                currentProcessWidth += 25;
                currentProcessRectangle.setWidth(currentProcessWidth);
                currentProcessId = -1;
                currentProcessWidth = 25; // Reset the width

                Label currentTimeLabel = new Label(Integer.toString(App.getCurrentTime() + 1));
                currentTimeLabel.setStyle("-fx-font-weight: bold;");
                currentProcessStackPane.getChildren().add(currentTimeLabel);
                StackPane.setAlignment(currentTimeLabel, javafx.geometry.Pos.BOTTOM_RIGHT);
                StackPane.setMargin(currentTimeLabel, new Insets(5));

                currentProcessRectangle = null;
                currentProcessStackPane = null;

                algorithmType.clear_context();

                break;
            default:
                break;
        }

        accumulativeSeconds++;
        String hoursStr = String.format("%02d", (App.getCurrentTime() / 3600));
        String minutesStr = String.format("%02d", ((App.getCurrentTime() / 60) % 60));
        String secondsStr = String.format("%02d", (App.getCurrentTime() % 60));

        timer.setText(hoursStr + ':' + minutesStr + ':' + secondsStr);

        // Kill the timer if it is instant and the algorithm is done
        if (instantRealTimeSwitch.getValue() == InstantRealTime.INSTANT 
            && result == ExecutionResult.PROCESS_FINISHED
            && !algorithmType.isCPUBuzy()
            && algorithmType.isReadyQueueEmpty()) {
            timeline.stop();

            // pause the timer
            currentSchedulerState = SchedulerState.DONE;
            updateLook();
        }
    }

    private Rectangle addRectangle(Label label, Color color) {
        Rectangle rectangle = new Rectangle(25, 75); // Width, Height
        rectangle.setFill(color);

        currentProcessStackPane = new StackPane();
        currentProcessStackPane.getChildren().addAll(rectangle, label);
        ganttChart.getChildren().add(currentProcessStackPane);

        return rectangle;
    }

    private void updateLook() {
        switch (currentSchedulerState) {
            case INITIALIZATION:
                instantRealTimeSwitch.setDisable(false);
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
                instantRealTimeSwitch.setDisable(true);
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
                instantRealTimeSwitch.setDisable(true);
                startButton.setDisable(true);
                stopButton.setDisable(false);
                pauseButton.setDisable(false);
                continueButton.setDisable(true);
                addProcessButton.setDisable(false);
                rrQuantumSpinBox.setDisable(true);
                generateAverageWaitingTimeButton.setDisable(true);
                generateAverageTurnaroundTimeButton.setDisable(true);
                break;
            case DONE:
                instantRealTimeSwitch.setDisable(false);
                startButton.setDisable(true);
                stopButton.setDisable(false);
                pauseButton.setDisable(true);
                continueButton.setDisable(true);
                addProcessButton.setDisable(false);
                rrQuantumSpinBox.setDisable(currentSchedulerAlgorithm != SchedulerAlgorithm.RR);
                generateAverageWaitingTimeButton.setDisable(false);
                generateAverageTurnaroundTimeButton.setDisable(false);
                break;
            case INVALID:
                instantRealTimeSwitch.setDisable(false);
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
                instantRealTimeSwitch.setDisable(true);
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
        BooleanWrapper isFutureProcess = new BooleanWrapper(false);
        StringWrapper processArrival = new StringWrapper();
        AddProcessDialog addProcessDialog = new
        AddProcessDialog(currentSchedulerAlgorithm, isSaved, processPriority,
            processBurst, isFutureProcess, processArrival);

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
            generateRandomColor());
        } else {
            process = new Process(
            ++processesIdTracker,
            (isFutureProcess.value) ? Integer.parseInt(processArrival.value) :
            App.getCurrentTime(),
            burst,
            generateRandomColor());
        }

        processDetailsTable.addProcess(currentSchedulerAlgorithm, process);

        algorithmType.addProcessToReadyQueue(process);

        if (currentSchedulerState == SchedulerState.RUNNING)
            timeline.play();
    }

    private Color generateRandomColor() {
        Random random = new Random();
        // Generate random RGB values within the range [220, 255]
        int red = random.nextInt(36) + 220;     // 220-255
        int green = random.nextInt(36) + 220;   // 220-255
        int blue = random.nextInt(36) + 220;    // 220-255
        return Color.rgb(red, green, blue);
    }

    private void algorithms_dropdownOnAction(ActionEvent event) {
        ganttChart.Clear();
        timeline.stop();
        accumulativeSeconds = 0;
        timer.reset();
        ComboBox<?> source = (ComboBox<?>) event.getSource();
        SchedulerAlgorithm selectedValue = (SchedulerAlgorithm) source.getValue();
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
                algorithmType = new NonPreemptivePriority();
                currentSchedulerAlgorithm = SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NON_PREEMPTIVE_PRIORITY);
                break;
            case NON_PREEMPTIVE_SJF:
                processesIdTracker = 0;
                algorithmType = new NonPreemptiveSJF();
                currentSchedulerAlgorithm = SchedulerAlgorithm.NON_PREEMPTIVE_SJF;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.NON_PREEMPTIVE_SJF);
                break;
            case PREEMPTIVE_PRIORITY:
                processesIdTracker = 0;
                algorithmType = new PreemptivePriority();
                currentSchedulerAlgorithm = SchedulerAlgorithm.PREEMPTIVE_PRIORITY;
                currentSchedulerState = SchedulerState.INITIALIZATION;
                updateLook();
                processDetailsTable.switchAlgorithm(SchedulerAlgorithm.PREEMPTIVE_PRIORITY);
                break;
            case PREEMPTIVE_SJF:
                processesIdTracker = 0;
                algorithmType = new PreemptiveSJF();
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

    private void instantRealTimeSwitchOnAction(ActionEvent event) {
        ganttChart.Clear();
        timeline.stop();
        accumulativeSeconds = 0;
        timer.reset();
        ComboBox<?> source = (ComboBox<?>) event.getSource();
        InstantRealTime selectedValue = (InstantRealTime) source.getValue();
        switch (selectedValue) {
            case REAL_TIME:
                timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::handleTimelineEvent));
                break;
            case INSTANT:
                timeline = new Timeline(new KeyFrame(Duration.millis(1), this::handleTimelineEvent));
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

        algorithms_dropdownButton.setOnAction(this::algorithms_dropdownOnAction);
        algorithms_dropdownButton.place(mainLayout);

        instantRealTimeSwitch.setOnAction(this::instantRealTimeSwitchOnAction);
        instantRealTimeSwitch.place(mainLayout);

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

        Scene scene = new Scene(mainLayout, 1440, 720);
        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
