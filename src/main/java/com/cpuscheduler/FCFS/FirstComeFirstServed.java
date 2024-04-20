package com.cpuscheduler.FCFS;

import java.util.Vector;

import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.CPU;
import com.cpuscheduler.CPU.CPUState;
import com.cpuscheduler.Utils.Process;

public class FirstComeFirstServed implements AlgorithmType {
    private CPU cpu;
    private Vector<Process> readyQueue;

    private int processesCount = 0;
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;

    public CPU getCpu() {
        return cpu;
    }

    public Vector<Process> getReadyQueue() {
        return readyQueue;
    }

    public FirstComeFirstServed() {
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

    @Override
    public ExecutionResult executeProcess() {
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

        int maxArrivalTimeValue = App.getCurrentTime();
        int maxArrivalTimeIndex = -1;

        for (int i = 0; i < readyQueue.size(); i++) {
            int currentTime = App.getCurrentTime();
            int currentArrivalTime = readyQueue.elementAt(i).getArrivalTime();

            // If the process has not arrived yet, skip it.
            if (currentArrivalTime > currentTime) {
                continue;
            }

            if (readyQueue.elementAt(i).getArrivalTime() <= maxArrivalTimeValue) {
                maxArrivalTimeIndex = i;
                maxArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
            }
        }

        if (maxArrivalTimeIndex == -1) {
            return false;
        }

        cpu.hookProcess(readyQueue.elementAt(maxArrivalTimeIndex));
        cpu.switchState(CPUState.BUZY);
        readyQueue.removeElementAt(maxArrivalTimeIndex);
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

    @Override
    public double getAverageWaitingTime() {
        return totalWaitingTime / (double) processesCount;
    }

    @Override
    public double getAverageTurnaroundTime() {
        return totalTurnaroundTime / (double) processesCount;
    }

    @Override
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
}
