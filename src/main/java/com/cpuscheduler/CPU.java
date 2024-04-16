package com.cpuscheduler;

import com.cpuscheduler.Utils.Process;

public class CPU {
    public enum CPUState {
        IDLE,
        BUZY
    }

    private CPUState state = CPUState.IDLE;

    private Process hookedProcess = null;

    public CPUState getState() {
        return state;
    }

    public void switchState(CPUState state) {
        this.state = state;
    }

    public void hookProcess(Process process) {
        this.hookedProcess = process;
    }

    public void unHookProcess() {
        this.hookedProcess = null;
    }

    public Process getHookedProcess() {
        return hookedProcess;
    }

    public int getHookedProcessPriority() {
        return this.hookedProcess.getPriority();
    }

    public boolean isBuzy() { return (state == CPUState.BUZY); }
}
