package com.cpuscheduler.Preemptive_Priority;

import java.util.Vector;

import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.CPU;
import com.cpuscheduler.CPU.CPUState;
import com.cpuscheduler.Utils.Process;

/*
 * 
 * Range [0 - 7], with 0 as highest priority.
 * 
 */
public class PreemptivePriority implements AlgorithmType {
    private CPU cpu;
    private boolean isPreemptive;
    private Vector<Process> readyQueue;

    private double totalWaitingTime = 0.0;
    private double totalTurnaroundTime = 0.0;
    private int processesCount = 0;

    private int agingRoundTime = 1;

    private int currentTime = 0;

    private boolean isAgingEnabled = false; // this variable disables aging feature for testing purposes.

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public PreemptivePriority(boolean isPreemptive) {
        this.isPreemptive = isPreemptive;
        cpu = new CPU();
        readyQueue = new Vector<Process>();
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        // System.out.println("addProcessToReadyQueue:getArrivalTime: " +
        // process.getArrivalTime());
        processesCount++;
        currentTime = App.getCurrentTime(); // Comment this line before running tests
        switch (cpu.getState()) {
            case IDLE:
                if (process.getArrivalTime() <= currentTime) {
                    cpu.hookProcess(process);
                    cpu.switchState(CPUState.BUZY);
                } else {
                    hookProcessOnReadyQueue(process);
                }
                return;
            case BUZY:
                if (process.getArrivalTime() <= currentTime) {
                    if (isPreemptive) {
                        hookProcessOnCPUIfHigherPriority(process);
                    } else {
                        hookProcessOnReadyQueue(process);
                    }
                } else {
                    hookProcessOnReadyQueue(process);
                }
                return;
            default:
                return;
        }
    }

    private void hookProcessOnCPUIfHigherPriority(Process process) {
        /*
         * 
         * Here we will apply FCFS for processes with same priorities.
         * For processes with lower priorities, just add them to the ready queue.
         * 
         */
        if (process.getPriority() < cpu.getHookedProcessPriority()) {
            cpu.getHookedProcess().setPreempted(true);
            hookProcessOnReadyQueue(cpu.getHookedProcess());
            cpu.switchState(CPUState.IDLE);
            cpu.unHookProcess();
            cpu.hookProcess(process);
            cpu.switchState(CPUState.BUZY);
        } else {
            hookProcessOnReadyQueue(process);
        }
    }

    private void hookProcessOnReadyQueue(Process process) {
        readyQueue.add(process);
    }

    @Override
    public Process getCPUHookedProcess() {
        return cpu.getHookedProcess();
    }

    @Override
    public boolean isCPUBuzy() {
        return cpu.isBuzy();
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setAgingEnabled(boolean isAgingEnabled) {
        this.isAgingEnabled = isAgingEnabled;
    }

    public boolean isPreemptive() {
        return isPreemptive;
    }

    public Vector<Process> getReadyQueue() {
        return readyQueue;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getTotalTurnaroundTime() {
        return totalTurnaroundTime;
    }

    public int getProcessesCount() {
        return processesCount;
    }

    public int getAgingRoundTime() {
        return agingRoundTime;
    }

    @Override
    public void executeProcess() {
        // currentTime = App.getCurrentTime(); // Comment this line before running tests

        // System.out.println("executeProcess:currentTime: " + currentTime);

        if (isAgingEnabled) {
            agingRoundTime++;
            /*
             * Every 5 seconds we age processes in ready queue to prevent starvation.
             */
            if (agingRoundTime % 5 == 0) {
                ageProcesses();
                agingRoundTime = 1;
            }
        }

        if (cpu.getState() == CPUState.IDLE) {
            // System.out.println("executeProcess:enteredIDLEIf");
            if (!hookProcessOnCPUFromReadyQueue(currentTime))
                return;
        }

        cpu.getHookedProcess().runProcess(1);

        if (cpu.getHookedProcess().isFinished()) {
            totalTurnaroundTime += currentTime - cpu.getHookedProcess().getArrivalTime() + 1;
            totalWaitingTime += cpu.getHookedProcess().getWaitingTime();
            cpu.switchState(CPUState.IDLE);
            cpu.unHookProcess();
            if (hookProcessOnCPUFromReadyQueue(currentTime)){
                cpu.getHookedProcess().wait(1);
            }
        }

        increaseWaitingPeriodForProcessesInReadyQueue();
    }

    @Override
    public void checkFutureArrivalProcessesInReadyQueue() {
        currentTime = App.getCurrentTime(); // Comment this line before running tests
        // System.out.println("before size(): "+readyQueue.size());
        int highestPriorityProcessValue = Integer.MAX_VALUE;

        if (cpu.isBuzy())
            highestPriorityProcessValue = cpu.getHookedProcess().getPriority();

        int highestPriorityProcessIndex = Integer.MAX_VALUE;
        for (int i = 0; i < readyQueue.size(); i++) {
            if (readyQueue.elementAt(i).getArrivalTime() == currentTime
                    && readyQueue.elementAt(i).getPriority() < highestPriorityProcessValue) {
                highestPriorityProcessIndex = i;
                highestPriorityProcessValue = readyQueue.elementAt(i).getPriority();
                // System.out.println("currentTime from algo: "+currentTime);
                // System.out.println("i: "+i);
                // System.out.println("size(): "+readyQueue.size());
            }
        }

        if (highestPriorityProcessIndex != Integer.MAX_VALUE) {
            Process futureProcess = readyQueue.elementAt(highestPriorityProcessIndex);
            readyQueue.removeElementAt(highestPriorityProcessIndex);

            switch (cpu.getState()) {
                case IDLE:
                    cpu.hookProcess(futureProcess);
                    cpu.switchState(CPUState.BUZY);
                    break;
                case BUZY:
                    if (isPreemptive) {
                        hookProcessOnCPUIfHigherPriority(futureProcess);
                    } else {
                        hookProcessOnReadyQueue(futureProcess);
                    }
                    break;
                default:
                    break;
            }

            return;
        }
    }

    @Override
    public void rearrangeProcesses() {

    }

    private boolean hookProcessOnCPUFromReadyQueue(int currentTime) {
        if (readyQueue.size() == 0)
            return false;

        int highestPriorityProcessValue = Integer.MAX_VALUE;
        int highestPriorityProcessIndex = Integer.MAX_VALUE;
        int minArrivalTimeValue = currentTime;

        for (int i = 0; i < readyQueue.size(); i++) {
            if ((((readyQueue.elementAt(i).getPriority() < highestPriorityProcessValue)
                    || (readyQueue.elementAt(i).getPriority() == highestPriorityProcessValue
                            && readyQueue.elementAt(i).isPreempted()))
                            && readyQueue.elementAt(i).getArrivalTime() == minArrivalTimeValue)
                    || (readyQueue.elementAt(i).getArrivalTime() < minArrivalTimeValue)) {
                // if (readyQueue.elementAt(i).getPriority() == highestPriorityProcessValue
                // && readyQueue.elementAt(i).isPreempted())
                // System.out.println("yeeeeeeeeeeeeeeeah!!!!!");
                highestPriorityProcessIndex = i;
                highestPriorityProcessValue = readyQueue.elementAt(i).getPriority();
                minArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
            }
        }

        if (highestPriorityProcessIndex != Integer.MAX_VALUE) {
            cpu.hookProcess(readyQueue.elementAt(highestPriorityProcessIndex));
            cpu.switchState(CPUState.BUZY);
            readyQueue.removeElementAt(highestPriorityProcessIndex);
            return true;
        }

        return false;
    }

    private void increaseWaitingPeriodForProcessesInReadyQueue() {
        for (int i = 0 ; i < readyQueue.size() ; i++) {
            if (readyQueue.elementAt(i).getArrivalTime() <= currentTime) {
                readyQueue.elementAt(i).wait(1);
            }
        }
    }

    private void ageProcesses() {
        for (int i = 0; i < readyQueue.size(); i++) {
            readyQueue.elementAt(i).age();
        }
    }

    public double getAverageWaitingTime() {
        return totalWaitingTime / processesCount;
    }

    @Override
    public double getAverageTurnaroundTime() {
        return totalTurnaroundTime / processesCount;
    }

    @Override
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
}
