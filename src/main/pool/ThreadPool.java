package main.pool;

import main.form.Form;
import main.tasks.*;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    private static int THREAD_CNT;
    public Form[] parent;
    public List<Form> offspring = new ArrayList<>();

    // Book keeping
    // private static final LinkedList<Task>  priority = new LinkedList<>();
    private static final LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    public ProgressBar pb0;

    public ThreadPool(int threadCnt) {
        ThreadPool.THREAD_CNT = threadCnt;
    }

    public void run(){
        //initializes progress bar
        if(this.pb0 == null) {
            System.out.println(parent[0].id + " , " + parent[1].id );
            double vertCnt = parent[0].v.size() * parent[0].settings.iterations;
            this.pb0 = new ProgressBar("producing offspring", (long) vertCnt);
        }

        //initializes workerThreads
        WorkerThread[] threads = new WorkerThread[THREAD_CNT];
        for (int i = 0; i < THREAD_CNT; i++) {
            threads[i] = new WorkerThread(this);
            threads[i].start();
        }

        //waits for all threads to return home
        for(WorkerThread temp:threads){
            try { temp.join();} catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void initializeTask(Form[] parent) {
        this.parent = parent;
        this.offspring.add(new Form(parent[0]));
        int offIndex = this.offspring.size()-1;
        for(int i = 0, j = this.offspring.get(this.offspring.size()-1).v.size(); i < j; i++) {
            if (this.offspring.get(offIndex).settings.removeUsedVertices) {
                execute(new RemoveUsedTree(i, this));
            }
            else if (this.offspring.get(offIndex).settings.prioritizeByDistance) {
                execute(new PrioritizeDistance(i, this));
            }
            else if (this.offspring.get(offIndex).settings.findBySegment) {
                this.parent[0].split();
                this.parent[1].split();
                execute(new FindBySegment(i, this));
            }
            else execute(new StandardTree(i, offIndex, this));
        }
    }

    void execute(Task task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    Task get(){
        Task task;
        synchronized (ThreadPool.queue) {
            task = ThreadPool.queue.poll();
            assert task != null;
            pb0.step();
            return task;
        }
    }

    public List<Form> ret() {
        return offspring;
    }
}