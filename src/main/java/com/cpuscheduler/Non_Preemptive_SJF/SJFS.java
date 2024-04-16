package com.cpuscheduler.Non_Preemptive_SJF;




import com.cpuscheduler.AlgorithmType;

import java.util.TreeSet;
import java.util.Vector;
import com.cpuscheduler.Utils.Process;

import static com.cpuscheduler.App.getCurrentTime;

public class SJFS implements AlgorithmType {
    private TreeSet<Process> processesB = new TreeSet<>(new SJFSProcessComparatorB());
    private TreeSet<Process> processesA = new TreeSet<>(new SJFSProcessComparatorA());
    Vector<Process> readyQueue;
    private boolean isPreemptive = false;
    private double AverageWaitingTime = 0;
    private double AverageTurnAroundTime = 0;
    private int nProcess = 0;
    private boolean done = false;
    private Process currentProcess;

    public SJFS(boolean isPreemptive) {
        this.isPreemptive = isPreemptive;
        this.currentProcess = null;
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        processesA.add(process);
        nProcess++;
    }
    public double getAverageWaitingTime(){
        return this.AverageWaitingTime;
    }
    public double getAverageTurnaroundTime(){
        return this.AverageTurnAroundTime;
    }


    @Override
    public Process getCPUHookedProcess() {
        return this.currentProcess;
    }

    @Override
    public boolean isCPUBuzy() {
        return currentProcess != null;
    }

    @Override
    public void executeProcess() {
        if (currentProcess != null) {
            if (!isPreemptive){
                if(currentProcess.getBurstTime() == 1){
                    int currentTime = getCurrentTime();
                    currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnAroundTime()-1 + currentProcess.getWaitingTime());
                    this.AverageWaitingTime += currentProcess.getWaitingTime();
                    this.AverageTurnAroundTime += (currentTime - currentProcess.getArrivalTime());
                }
                currentProcess.runProcess(1);
                currentProcess.setWaitingTime(currentProcess.getWaitingTime()-1);
            }
            else {
                currentProcess.runProcess(1);
                currentProcess.setWaitingTime(currentProcess.getWaitingTime() - 1); // Same here
                if (currentProcess.getBurstTime() != 0) processesA.add(currentProcess);
                else {
                    currentProcess.setTurnAroundTime(getCurrentTime() - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() + currentProcess.getWaitingTime());
                    AverageWaitingTime += currentProcess.getWaitingTime();
                    AverageTurnAroundTime += currentProcess.getTurnAroundTime();
                }
                currentProcess = null;
            }
        }
        if (currentProcess == null || currentProcess.isFinished()) {
            if (!getProcessFromReadyQueue()) {
                currentProcess = null;
            }
        }

    }

    private boolean getProcessFromReadyQueue() {
        int currentTime = getCurrentTime();
        while (!processesA.isEmpty() && currentTime >= processesA.first().getArrivalTime()) {
            processesB.add(processesA.first());
            processesA.remove(processesA.first());
        }
        if (processesB.isEmpty() && processesA.isEmpty() && !done) {
            done = true;
            AverageWaitingTime/=nProcess;
            AverageTurnAroundTime/=nProcess;
        }
        if (processesB.isEmpty()) {
            return false;
        } else currentProcess = processesB.first();

        //Logic according to the type of SJF scheduler
        if (!isPreemptive) {

            processesB.remove(currentProcess);
            return true;
        } else {
            processesB.remove(currentProcess);
            return true;
        }
    }

    @Override
    public void checkFutureArrivalProcessesInReadyQueue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkFutureArrivalProcessesInReadyQueue'");
    }

    @Override
    public void rearrangeProcesses() {

    }

    @Override
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }

    // @Override
    // public double getAverageWaitingTime() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getAverageWaitingTime'");
    // }

//    @Override
//    public double getAverageTurnaroundTime() {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'getAverageTurnaroundTime'");
//    }
}
