package com.cpuscheduler.Non_Preemptive_SJF;
import java.util.Comparator;
import com.cpuscheduler.Utils.Process;

public class SJFSProcessComparatorA implements Comparator<Process>{
    @Override
    public int compare(Process p1, Process p2) {

        //added to fix the issue of removing processes which have same burst and arrival time
        //but are different
        //now processes are distinguished by their id,burst and arrival time
        if(p1.getArrivalTime() ==  p2.getArrivalTime() && p1.getBurstTime() ==  p2.getBurstTime()){
            return Integer.compare(p1.getId(), p2.getId());
        }
        // Compare by arrival time
        int compareByArrivalTime = Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
        if (compareByArrivalTime != 0) {
            return compareByArrivalTime;
        }

        // If arrival times are the same, compare by burst time
        return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
    }
}
