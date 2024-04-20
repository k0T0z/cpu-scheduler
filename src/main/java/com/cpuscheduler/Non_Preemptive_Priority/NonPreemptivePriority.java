package com.cpuscheduler.Non_Preemptive_Priority;

import java.util.Vector;

import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.CPU;
import com.cpuscheduler.CPU.CPUState;
import com.cpuscheduler.Utils.Process;
import com.cpuscheduler.AlgorithmType.ExecutionResult;

/*
 * 
 * Range [0 - 7], with 0 as highest priority.
 * 
 */
public class NonPreemptivePriority implements AlgorithmType {
    private CPU cpu;
    private Vector<Process> readyQueue;

    private int processesCount = 0;
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;

    private int agingRoundTime = 1;

    private boolean isAgingEnabled = false; // this variable disables aging feature for testing purposes.

    public NonPreemptivePriority() {
        cpu = new CPU();
        readyQueue = new Vector<Process>();
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        int currentTime = App.getCurrentTime();
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
                hookProcessOnReadyQueue(process);
                return;
            default:
                return;
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
    public ExecutionResult executeProcess() {
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
            if (!hookProcessOnCPUFromReadyQueue())
                return ExecutionResult.CPU_IDLE;
        }

        cpu.getHookedProcess().runProcess(1);

        increaseWaitingPeriodForProcessesInReadyQueue();

        if (cpu.getHookedProcess().isFinished()) {
            processesCount++;
            totalWaitingTime += cpu.getHookedProcess().getWaitingTime();
            totalTurnaroundTime += App.getCurrentTime() - cpu.getHookedProcess().getArrivalTime() + 1;
            return ExecutionResult.PROCESS_FINISHED;
        }

        return ExecutionResult.PROCESS_EXECUTED;
    }

    @Override
    public void clear_context() {
        cpu.switchState(CPUState.IDLE);
        cpu.unHookProcess();
    }

    private boolean hookProcessOnCPUFromReadyQueue() {
        if (readyQueue.size() == 0)
            return false;

        // Highest priority process has the lowest value.
        int highestPriorityProcessValue = Integer.MAX_VALUE;
        int highestPriorityProcessIndex = -1;
        int maxArrivalTimeValue = App.getCurrentTime();

        for (int i = 0; i < readyQueue.size(); i++) {
            int currentTime = App.getCurrentTime();
            int currentArrivalTime = readyQueue.elementAt(i).getArrivalTime();

            // If the process has not arrived yet, skip it.
            if (currentArrivalTime > currentTime) {
                continue;
            }

            if (readyQueue.elementAt(i).getPriority() == highestPriorityProcessValue) {
                if (readyQueue.elementAt(i).getArrivalTime() <= maxArrivalTimeValue) {
                    highestPriorityProcessIndex = i;
                    highestPriorityProcessValue = readyQueue.elementAt(i).getPriority();
                    maxArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
                }

                continue;
            }

            // If the current process has higher priority than the saved process, give the priority to the current process.
            if (readyQueue.elementAt(i).getPriority() < highestPriorityProcessValue) {
                highestPriorityProcessIndex = i;
                highestPriorityProcessValue = readyQueue.elementAt(i).getPriority();
                maxArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
            }
        }

        if (highestPriorityProcessIndex == -1) {
            return false;
        }

        cpu.hookProcess(readyQueue.elementAt(highestPriorityProcessIndex));
        cpu.switchState(CPUState.BUZY);
        readyQueue.removeElementAt(highestPriorityProcessIndex);
        return true;
    }

    private void increaseWaitingPeriodForProcessesInReadyQueue() {
        int currentTime = App.getCurrentTime();
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
