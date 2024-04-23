package com.cpuscheduler.Preemptive_SJF;

import com.cpuscheduler.AlgorithmType;

import java.util.Vector;
import com.cpuscheduler.Utils.Process;

import com.cpuscheduler.App;
import com.cpuscheduler.CPU;
import com.cpuscheduler.CPU.CPUState;

public class PreemptiveSJF implements AlgorithmType {
    private CPU cpu;
    private Vector<Process> readyQueue;

    private int processesCount = 0;
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;

    public PreemptiveSJF() {
        cpu = new CPU();
        readyQueue = new Vector<Process>();
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        int currentTime = App.getCurrentTime(); // Comment this line before running tests
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
                    hookProcessOnCPUIfShorterBurst(process);
                } else {
                    hookProcessOnReadyQueue(process);
                }
                return;
            default:
                return;
        }
    }

    private void hookProcessOnCPUIfShorterBurst(Process process) {
        if (process.getBurstTime() < cpu.getHookedProcessBurstTime()) {
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

    @Override
    public ExecutionResult executeProcess() {
        if (cpu.getState() == CPUState.IDLE) {
            if (!hookProcessOnCPUFromReadyQueue())
                return ExecutionResult.CPU_IDLE;
        }

        PreemptionContextSwitchCheck();

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

        int minBurstProcessValue = Integer.MAX_VALUE;
        int minBurstProcessIndex = -1;
        int maxArrivalTimeValue = App.getCurrentTime();

        for (int i = 0; i < readyQueue.size(); i++) {
            int currentTime = App.getCurrentTime();
            int currentArrivalTime = readyQueue.elementAt(i).getArrivalTime() - 1; // -1 because the time starts from 0

            // If the process has not arrived yet, skip it.
            if (currentArrivalTime > currentTime) {
                continue;
            }

            if (readyQueue.elementAt(i).getBurstTime() == minBurstProcessValue) {
                if (readyQueue.elementAt(i).getArrivalTime() <= maxArrivalTimeValue) {
                    minBurstProcessIndex = i;
                    minBurstProcessValue = readyQueue.elementAt(i).getBurstTime();
                    maxArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
                }

                continue;
            }

            // If the current process has lower burst than the saved process, give the priority to the current process.
            if (readyQueue.elementAt(i).getBurstTime() < minBurstProcessValue) {
                minBurstProcessIndex = i;
                minBurstProcessValue = readyQueue.elementAt(i).getBurstTime();
                maxArrivalTimeValue = readyQueue.elementAt(i).getArrivalTime();
            }
        }

        if (minBurstProcessIndex == -1) {
            return false;
        }

        cpu.hookProcess(readyQueue.elementAt(minBurstProcessIndex));
        cpu.switchState(CPUState.BUZY);
        readyQueue.removeElementAt(minBurstProcessIndex);
        return true;
    }

    public void PreemptionContextSwitchCheck() {
        if (readyQueue.size() == 0)
            return;

        if (cpu.getState() == CPUState.IDLE)
            return;

        int minBurstProcessValue = cpu.getHookedProcess().getBurstTime();
        int minBurstProcessIndex = -1;

        for (int i = 0; i < readyQueue.size(); i++) {
            int currentTime = App.getCurrentTime();
            int currentArrivalTime = readyQueue.elementAt(i).getArrivalTime();

            // If the process has not arrived yet, skip it.
            if (currentArrivalTime != currentTime) {
                continue;
            }

            // If the current process has lower burst than the saved process, give the priority to the current process.
            if (readyQueue.elementAt(i).getBurstTime() < minBurstProcessValue) {
                minBurstProcessIndex = i;
                minBurstProcessValue = readyQueue.elementAt(i).getBurstTime();
            }
        }

        if (minBurstProcessIndex == -1) {
            return;
        }

        Process futureProcess = readyQueue.elementAt(minBurstProcessIndex);
        readyQueue.removeElementAt(minBurstProcessIndex);

        switch (cpu.getState()) {
            case BUZY:
                hookProcessOnCPUIfShorterBurst(futureProcess);
                break;
            case IDLE:
            default:
                break;
        }

        return;
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
