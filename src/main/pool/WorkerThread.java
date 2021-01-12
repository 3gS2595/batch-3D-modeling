package main.pool;

import main.model.Model;
import main.model.Output;

import java.util.ArrayList;
import java.util.List;

public class WorkerThread extends Thread {
    ThreadPool pool;
    WorkerThread(ThreadPool pool) {
        this.pool = pool;
    }

    public void run() {
        Task task;
        while (true) {
            task = pool.get();
            if(task != null) {
                task.run();
            }
            else return;
        }
    }
}