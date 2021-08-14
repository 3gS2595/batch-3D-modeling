package main.pool;

import main.Settings;
import main.form.Form;
import main.tasks.*;
import main.tasks.other.PrioritizeDistance;
import main.tasks.other.RemoveUsedTree;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    public Settings setting;
    public Form[] pairSpring;
    public List<Form> output = new ArrayList<>();
    public List<String> logText = new ArrayList<>();

    // Book keeping
    // private static final LinkedList<Task>  priority = new LinkedList<>();
    public static final LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    public ProgressBar pb0;

    public ThreadPool(Settings setting) {
        this.setting = setting;
    }

    public void run(ProgressBar pb0){
        this.pb0 = pb0;

        //initializes workerThreads
        WorkerThread[] threads = new WorkerThread[setting.threadCnt];
        for (int i = 0; i < setting.threadCnt; i++) {
            threads[i] = new WorkerThread(this);
            threads[i].start();
        }

        //waits for all threads to come home
        for(WorkerThread temp:threads){
            try { temp.join();} catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void initializeTaskSingle(Form parent) {
        // vertex' taskX
        if (parent.settings.decimate) {
            execute(new Decimate(parent, this));
        }
    }

    public void initializeTaskGroup(Form[] parent) {
        this.pairSpring = parent;
        this.output.add(new Form(parent[0]));
        int offIndex = this.output.size()-1;

        // vertex' task X
        for(int i = 0, j = this.output.get(this.output.size()-1).v.size(); i < j; i++) {
            if (this.output.get(offIndex).settings.nearestVertice)
                execute(new Standard(i, offIndex, this));

            else if (this.output.get(offIndex).settings.nearestSurface)
                execute(new NearestSurface(i, offIndex, this));

            else if (this.output.get(offIndex).settings.removeUsedVertices)
                execute(new RemoveUsedTree(i, this));

            else if (this.output.get(offIndex).settings.prioritizeByDistance)
                execute(new PrioritizeDistance(i, this));
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

    public void writeInfoFile() {
        try {
            String dirPath = this.setting.outputFolder + "infoFile" + ".txt";
            File file = new File(dirPath);
            FileWriter writer = new FileWriter(file);
            for (String entry : this.logText)
                writer.write(entry);

            writer.flush();
            writer.close();
        } catch (IOException e){e.printStackTrace();}
    }
}