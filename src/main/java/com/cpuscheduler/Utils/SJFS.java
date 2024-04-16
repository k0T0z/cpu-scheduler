package com.cpuscheduler.Utils;

import java.util.ArrayList;
import java.util.TreeSet;

public class SJFS {
	private TreeSet<Process> processes = new TreeSet<>(new SJFSProcessComparator());
	private ArrayList<Process> finished = new ArrayList<Process>();
	
	public void addProcess(Process p) {
		this.processes.add(p);
	}
	public void printProcess() {
		for(Process p: processes) {
			System.out.println(p.getArrivalTime() + "-" + p.getBurstTime());
		}
	}
}
