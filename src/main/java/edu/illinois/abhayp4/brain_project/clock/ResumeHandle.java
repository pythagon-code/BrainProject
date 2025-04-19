package edu.illinois.abhayp4.brain_project.clock;

public class ResumeHandle {
    private volatile boolean resumed = false;

    public void waitUntilResumed() {
        resumed = false;
        do {
            Thread.onSpinWait();
        } while (!resumed);
    }

    void waitUntilPaused() {
        do {
            Thread.onSpinWait();
        } while (resumed);
    }

    void resume() {
        resumed = true;
    }
}