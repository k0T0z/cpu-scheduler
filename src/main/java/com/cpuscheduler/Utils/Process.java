package com.cpuscheduler.Utils;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Process {

	private int id;
    private int arrivalTime;
    private int initialBurstTime;
    private int burstTime;
    private int priority;
    private int waitingTime=0;
    private int turnAroundTime=0;
    private int lastQueue;
    private boolean isPreempted = false;
	private final Color color;

    private boolean quantumExpired = false;

    public Process(int id, int arrivalTime, int burstTime, int priority, Color color) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.lastQueue = 1;
		this.color = color;
    }

    public Process(int id, int arrivalTime, int burstTime, Color color) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = -1;
        this.lastQueue = 1;
		this.color = color;
    }

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.lastQueue = 1;
		this.color = new Color(0, 0, 0, 1.0);
    }

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = -1;
        this.lastQueue = 1;
		this.color = new Color(0, 0, 0, 1.0);
    }

    public int getId() {
        return this.id;
    }

    public int getArrivalTime() {
        return this.arrivalTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public boolean isPreempted() {
        return isPreempted;
    }

    public void setPreempted(boolean isPreempted) {
        this.isPreempted = isPreempted;
    }

    public int getBurstTime() {
        return this.burstTime;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getWaitingTime() {
        return this.waitingTime;
    }

    public void setQuantumExpired(boolean quantumExpired) {
        this.quantumExpired = quantumExpired;
    }

    public boolean isQuantumExpired() {
        return this.quantumExpired;
    }

    public void setTurnAroundTime(int turnAroundTime) { this.turnAroundTime = turnAroundTime; }
    public void setInitialBurstTime(int initialTime) { this.initialBurstTime = initialTime; }
    public int getInitialBurstTime() { return this.initialBurstTime; }
    public int getTurnAroundTime() {
        return this.turnAroundTime;
    }
    public Color getColor() {
        return this.color;
    }
    public int getLastQueue() {
        return this.lastQueue;
    }
    public void setLastQueue(int lastQueue) {
        this.lastQueue = lastQueue;
    }
    public boolean isFinished() {
        return burstTime == 0;
    }
    public void runProcess(int time) {
        burstTime -= time;
    }
    public void wait(int time) {
        waitingTime += time;
    }
    public void age() {
        if (priority == 0) return;
        priority--;
    }
}
