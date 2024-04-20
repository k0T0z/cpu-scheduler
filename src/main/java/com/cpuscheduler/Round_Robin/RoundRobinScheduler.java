package com.cpuscheduler.Round_Robin;
import java.util.LinkedList;
import java.util.Queue;
import com.cpuscheduler.CPU;
import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.Utils.Process;
import javafx.scene.paint.Color;
import java.util.Vector;
import com.cpuscheduler.CPU.CPUState;

public class RoundRobinScheduler implements AlgorithmType{

    private int timeQuantum;
    
    private CPU cpu;
    private Vector<Process> readyQueue;

    private int processesCount = 0;
    private double totalTurnaroundTime = 0;
    private double totalWaitingTime = 0;

    private int quantumCounter = 0;

    public RoundRobinScheduler(int timeQuantum) {
        cpu = new CPU();
        readyQueue = new Vector<Process>();
        this.timeQuantum = timeQuantum;
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
                    hookProcessOnReadyQueue(process);
                }
                return;
            default:
                return;
        }
    }

    private void hookProcessOnReadyQueue(Process process) {
        readyQueue.add(process); // In Round Robin, we add the process to the end of the queue
    }
    
    public void setQuantum(int quantum){
        this.timeQuantum = quantum;
        System.out.println(this.timeQuantum);
    }

    @Override
    public ExecutionResult executeProcess() {
        if (cpu.getState() == CPUState.IDLE) {
            if (!hookProcessOnCPUFromReadyQueue())
                return ExecutionResult.CPU_IDLE;
        }

        if (quantumCounter == timeQuantum) {
            Process currentProcess = cpu.getHookedProcess();
            cpu.unHookProcess();
            hookProcessOnReadyQueue(currentProcess);
            quantumCounter = 0;

            if (!hookProcessOnCPUFromReadyQueue())
                return ExecutionResult.CPU_IDLE;
        }

        cpu.getHookedProcess().runProcess(1);
        quantumCounter++; // Increment quantum counter

        increaseWaitingPeriodForProcessesInReadyQueue();

        if (cpu.getHookedProcess().isFinished()) {
            // If a process has a burst less than the qunatum, then we should reset the quantum counter
            // when the process finishes
            quantumCounter = 0;
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

        int processIndex = 0; // Take first process

        cpu.hookProcess(readyQueue.elementAt(processIndex));
        cpu.switchState(CPUState.BUZY);
        readyQueue.removeElementAt(processIndex);

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
    public Process getCPUHookedProcess() {
        return cpu.getHookedProcess();
    }

    @Override
    public boolean isCPUBuzy() {
        return cpu.isBuzy();
    }

    @Override
    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }
}
