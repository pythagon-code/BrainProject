package edu.illinois.abhayp4.brain_project.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Heartbeat {
    private final List<ResumeHandle> resumeHandles;

    public Heartbeat() {
        resumeHandles = new ArrayList<>();
    }

    public ResumeHandle newResumeHandle() {
        resumeHandles.add(ResumeHandle.create());
        return resumeHandles.getLast();
    }

    public void pulse() {
        for (ResumeHandle resumeHandle : resumeHandles) {
            resumeHandle.resume();
        }
    }
}