package main.pool;

import main.Settings;
import main.form.Form;
import main.tasks.*;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    public Settings setting;
    public Form[] parent;
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

        //waits for all threads to return home
        for(WorkerThread temp:threads){
            try { temp.join();} catch (InterruptedException e) { e.printStackTrace(); }
        }
    }


    public void initializeTaskSingle(Form parent) {
        this.parent = new Form[]{parent};
        this.output.add(new Form(parent));
        int offIndex = this.output.size()-1;

        if (this.output.get(offIndex).settings.decimate) {

            // copies v into new v
            for(int i = 0; i < this.output.get(offIndex).v.size(); i++){
                this.output.get(offIndex).newV.put(i, this.output.get(offIndex).v.get(i));
            }

            execute(new DecimateSingleThread(offIndex, this));
            // sends index for newly created vertices stepping by 3
            // allowing 3 new points without thread collision
//            int offset = this.output.get(offIndex).newV.size();
//            for(int i = 0, j = this.output.get(offIndex).f.size(); i < j; i++) {
//                execute(new Decimate(i, offset, offIndex, this));
//                offset+=3;
//            }
        }
    }

    public void initializeTaskGroup(Form[] parent) {
        this.parent = parent;
        this.output.add(new Form(parent[0]));
        int offIndex = this.output.size()-1;

        // vertex' task creationX
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

    public void writeInfoFile() {
        try {
            String dirPath = this.setting.outputFilePath + "infoFile" + ".txt";
            File file = new File(dirPath);
            FileWriter writer = new FileWriter(file);
            for (String entry : this.logText) {
                writer.write(entry);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}