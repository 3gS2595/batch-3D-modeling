package main.pool;

import main.tasks.Task;

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
            } else return;
        }
    }
}