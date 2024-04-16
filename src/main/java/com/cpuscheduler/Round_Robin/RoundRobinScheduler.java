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
    public void executeProcess() {
        Process currentProcess = queue1.poll();

        System.out.println(" burst:" + currentProcess.getBurstTime() + " currentTime:" + App.getLastTime() + " arrival:" + currentProcess.getArrivalTime());
        int runTime = Math.min(timeQuantum, currentProcess.getBurstTime());
        time += runTime;
        currentProcess.runProcess(runTime);
        System.out.println("Arrival:" + currentProcess.getArrivalTime() + " App Current:" + App.getLastTime() + " Process ID:" + currentProcess.getId() + " burst:" + currentProcess.getBurstTime());
        if (currentProcess.isFinished()) {
            int completionTime = App.getLastTime() + runTime;
            currentProcess.setTurnAroundTime(completionTime - currentProcess.getArrivalTime());
            totalTurnAroundTime += currentProcess.getTurnAroundTime();
            currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getInitialBurstTime());
            totalWaitingTime += currentProcess.getWaitingTime();
            System.out.println("Process " + currentProcess.getId() + " finished at time " + App.getLastTime() + " burst:" + currentProcess.getBurstTime());
        } else {
            queue1.add(currentProcess);
            System.out.println("Process " + currentProcess.getId() + " is executing at time " + App.getLastTime() + " burst:" + currentProcess.getBurstTime());
        }
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
    public void checkFutureArrivalProcessesInReadyQueue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkFutureArrivalProcessesInReadyQueue'");
    }

    @Override
    public void rearrangeProcesses() {
        Queue<Process> queue2 = new LinkedList<>();             // carries processes that have arrival time <= current time
        Queue<Process> queue3 = new LinkedList<>();             // carries processes that have arrival time > current time
        boolean flag=false;
        Process currentProcess=null;
        while (!queue1.isEmpty()) {
            if (queue1.peek().getArrivalTime() <= App.getCurrentTime()) {
                if (!flag) {
                    currentProcess = queue1.poll();
                    flag = true;
                }
                else queue2.add(queue1.poll());
            } else queue3.add(queue1.poll());
        }
        if(currentProcess!=null) queue1.add(currentProcess);
        while(!queue2.isEmpty()) queue1.add(queue2.poll());
        while(!queue3.isEmpty()) queue1.add(queue3.poll());
    }

    @Override
    public boolean isReadyQueueEmpty() {
        return queue1.isEmpty();
    }
}
