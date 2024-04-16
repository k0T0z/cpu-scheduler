package com.cpuscheduler.FCFS;

import java.util.Vector;

import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.CPU;
import com.cpuscheduler.CPU.CPUState;
import com.cpuscheduler.Utils.Process;

public class FirstComeFirstServed implements AlgorithmType {
    private CPU cpu;
    private int numProcesses=0;
    private Vector<Process> readyQueue;
    private int currentTime = 0;
    private double totalTurnAroundTime, totalWaitingTime;

    public CPU getCpu() {
        return cpu;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public Vector<Process> getReadyQueue() {
        return readyQueue;
    }

    public Vector<Process> getQueue1() {
        return queue1;
    }

    public Vector<Process> getQueue2() {
        return queue2;
    }

    private Vector<Process> queue1;
    private Vector<Process> queue2;

    public FirstComeFirstServed() {
        cpu = new CPU();
        queue1 = new Vector<Process>();
        queue2 = new Vector<Process>();
        readyQueue = new Vector<Process>();
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        numProcesses++;
        process.setInitialBurstTime(process.getBurstTime());
        // currentTime = App.getCurrentTime();
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
    public void executeProcess() {
        if (cpu.getState() == CPUState.IDLE) {
            if (!hookProcessOnCPUFromReadyQueue())
                return;
        }

        cpu.getHookedProcess().runProcess(1);
        cpu.getHookedProcess().setWaitingTime(cpu.getHookedProcess().getWaitingTime() - 1);

        if (cpu.getHookedProcess().isFinished()) {
            int completionTime = App.getLastTime() + getCPUHookedProcess().getInitialBurstTime();
            cpu.getHookedProcess().setTurnAroundTime(completionTime - cpu.getHookedProcess().getArrivalTime());
            totalTurnAroundTime += cpu.getHookedProcess().getTurnAroundTime();
            cpu.getHookedProcess().setWaitingTime(cpu.getHookedProcess().getTurnAroundTime() - cpu.getHookedProcess().getInitialBurstTime());
            totalWaitingTime += cpu.getHookedProcess().getWaitingTime();
            // cpu.getHookedProcess().getWaitingTime();
            cpu.switchState(CPUState.IDLE);
            cpu.unHookProcess();
            hookProcessOnCPUFromReadyQueue();
        }
    }

    private boolean hookProcessOnCPUFromReadyQueue() {
        currentTime=App.getCurrentTime();
        if (readyQueue.size() == 0)
            return false;

        int arrivalValue = Integer.MAX_VALUE;
        int processIndex = -1;

        for (int i = 0; i < readyQueue.size(); i++) {
            if (readyQueue.elementAt(i).getArrivalTime() <= currentTime
                    && readyQueue.elementAt(i).getArrivalTime() < arrivalValue) {
                arrivalValue = readyQueue.elementAt(i).getArrivalTime();   
                processIndex = i;
            }
        }

        if (arrivalValue != Integer.MAX_VALUE) {
            cpu.hookProcess(readyQueue.elementAt(processIndex));
            cpu.switchState(CPUState.BUZY);
            readyQueue.removeElementAt(processIndex);
            return true;
        }

        return false;
    }

    public double getAverageWaitingTime() {
        return totalWaitingTime / (double) numProcesses;
    }

    @Override
    public double getAverageTurnaroundTime() {
        return totalTurnAroundTime / (double) numProcesses;
    }

    @Override
    public void checkFutureArrivalProcessesInReadyQueue() {
        currentTime = App.getCurrentTime(); // Comment this line before running tests
        // System.out.println("before size(): "+readyQueue.size());

        if (cpu.isBuzy())
            return;

        int processIndex = -1;
        for (int i = 0; i < readyQueue.size(); i++) {
            if (readyQueue.elementAt(i).getArrivalTime() == currentTime) {
                processIndex = i;
                break;
            }
        }

        if (processIndex != -1) {
            Process futureProcess = readyQueue.elementAt(processIndex);
            readyQueue.removeElementAt(processIndex);

            switch (cpu.getState()) {
                case IDLE:
                    cpu.hookProcess(futureProcess);
                    cpu.switchState(CPUState.BUZY);
                    break;
                case BUZY:
                    hookProcessOnReadyQueue(futureProcess);
                    break;
                default:
                    break;
            }
        }
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
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'getAverageWaitingTime'");
    // }

    // @Override
    // public double getAverageTurnaroundTime() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'getAverageTurnaroundTime'");
    // }
}
