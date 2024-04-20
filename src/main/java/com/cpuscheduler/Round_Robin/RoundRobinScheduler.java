package com.cpuscheduler.Round_Robin;
import java.util.LinkedList;
import java.util.Queue;
import com.cpuscheduler.CPU;
import com.cpuscheduler.AlgorithmType;
import com.cpuscheduler.App;
import com.cpuscheduler.Utils.Process;
import javafx.scene.paint.Color;

public class RoundRobinScheduler implements AlgorithmType{

    private final Queue<Process> queue1;
    private int timeQuantum;
    private CPU cpu;
    int time;
    private double totalTurnAroundTime, totalWaitingTime;
    private int numOfProcesses;

    public RoundRobinScheduler(int timeQuantum) {
        this.queue1 = new LinkedList<>();
        this.timeQuantum = timeQuantum;
        time = 0; 
    }

    @Override
    public void addProcessToReadyQueue(Process process) {
        queue1.add(process);
        process.setInitialBurstTime(process.getBurstTime());
        numOfProcesses++;
    }
    
    public void setQuantum(int quantum){
        this.timeQuantum = quantum;
        System.out.println(this.timeQuantum);
    }

    @Override
    public ExecutionResult executeProcess() {
        return ExecutionResult.CPU_IDLE;
    }

    @Override
    public void clear_context() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clear_context'");
    }

    @Override
    public double getAverageWaitingTime() {
        return totalWaitingTime / (double) numOfProcesses;
    }
    @Override 
    public double getAverageTurnaroundTime() {
        return totalTurnAroundTime / (double) numOfProcesses;
    }
    @Override
    public Process getCPUHookedProcess() {
        return queue1.peek();
    }

    @Override
    public boolean isCPUBuzy() {
        if (!queue1.isEmpty() && queue1.peek().getArrivalTime()<=App.getCurrentTime()) {
            System.out.println("isCPUBuzy() true");
            return true;
        } else {
            System.out.println("isCPUBuzy() false");
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RoundRobinScheduler scheduler = new RoundRobinScheduler(2);
        Process p1 = new Process(1, 0, 5, 1, Color.RED);
        Process p2 = new Process(2, 1, 3, 2, Color.GREEN);
        Process p3 = new Process(3, 2, 8, 3, Color.BLUE);
        scheduler.addProcessToReadyQueue(p1);
        scheduler.addProcessToReadyQueue(p2);
        scheduler.addProcessToReadyQueue(p3);
        scheduler.executeProcess();
    }

    @Override
    public boolean isReadyQueueEmpty() {
        return queue1.isEmpty();
    }
}
