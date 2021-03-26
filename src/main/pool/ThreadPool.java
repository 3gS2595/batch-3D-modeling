package main.pool;

import main.Settings;
import main.form.Form;
import main.tasks.*;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    public Settings setting;
    public Form[] parent;
    public List<Form> output = new ArrayList<>();
    public List<String> runRecord = new ArrayList<>();

    // Book keeping
    // private static final LinkedList<Task>  priority = new LinkedList<>();
    public static final LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    public ProgressBar pb0;

    public ThreadPool(Settings setting) {
        this.setting = setting;
    }

    public void run(ProgressBar pb){
        //initializes workerThreads
        this.pb0 = pb;
        WorkerThread[] threads = new WorkerThread[setting.threadCnt];
        for (int i = 0; i < setting.threadCnt; i++) {
            threads[i] = new WorkerThread(this);
            threads[i].start();
        }

        //waits for all threads to return home
        for(WorkerThread temp:threads){
            try { temp.join();} catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void initializeTaskSingle(Form parent) {
        this.parent = new Form[]{parent};
        this.output.add(new Form(parent));
        int offIndex = this.output.size()-1;
        for(int i = 0, j = this.setting.forms.get(this.setting.forms.size()-1).v.size(); i < j; i++) {
            if (this.setting.forms.get(offIndex).settings.decimate) {
                execute(new Decimate(i, offIndex, this));
            }
        }
    }

    public void initializeTaskGroup(Form[] parent) {
        this.parent = parent;
        this.output.add(new Form(parent[0]));
        int offIndex = this.output.size()-1;

        // vertex' task creation
        for(int i = 0, j = this.output.get(this.output.size()-1).v.size(); i < j; i++) {
                 if (this.output.get(offIndex).settings.standard) {
                execute(new StandardTree(i, offIndex, this));
            }
            else if (this.output.get(offIndex).settings.removeUsedVertices) {
                execute(new RemoveUsedTree(i, this));
            }
            else if (this.output.get(offIndex).settings.prioritizeByDistance) {
                execute(new PrioritizeDistance(i, this));
            }
            else if (this.output.get(offIndex).settings.findBySegment) {
                this.parent[0].split();
                this.parent[1].split();
                execute(new FindBySegment(i, this));
            }
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
            this.pb0.step();
            return task;
        }
    }

}