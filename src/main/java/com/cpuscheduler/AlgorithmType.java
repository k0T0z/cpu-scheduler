package com.cpuscheduler;
import com.cpuscheduler.Utils.Process;

public interface AlgorithmType {
    public enum ExecutionResult {
        CPU_IDLE,
        PROCESS_EXECUTED,
        PROCESS_FINISHED,
    }

    public void addProcessToReadyQueue(Process process);
    public Process getCPUHookedProcess();
    public boolean isCPUBuzy();
    public boolean isReadyQueueEmpty();
    public ExecutionResult executeProcess();
    public void clear_context();
    public double getAverageWaitingTime();
    public double getAverageTurnaroundTime();
}
