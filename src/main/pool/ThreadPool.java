package main.pool;

import main.model.Model;
import main.tasks.ProduceTask;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    // args[]
    private static int THREAD_CNT;
    public Model[] parent;

    // Book keeping
    // private static final LinkedList<Task>  priority = new LinkedList<>();
    private static final LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    public Model offspring;
    public ProgressBar pb0;

    public ThreadPool(int threadCnt) {
        ThreadPool.THREAD_CNT = threadCnt;
    }

    public void run(){
        //initializes workerThreads
        if(this.pb0 == null) {
            System.out.print("                                               \n");
            System.out.print("              Lineage " + parent[0].settings.file1 + ", " + parent[0].settings.file2 + "\n");
            System.out.print("   removeUsedVertices " + parent[0].settings.removeUsedVertices + "\n");
            System.out.print("     standardizeScale " + parent[0].settings.standardizeScale + "\n");
            System.out.print("        centerObjects " + parent[0].settings.centerObjects + "\n");
            System.out.print(" prioritizeByDistance " + parent[0].settings.prioritizeByDistance + "\n");
            System.out.print("        vertexNormals " + parent[0].settings.VertexNormals + "\n");
            System.out.print("     avgVertexNormals " + parent[0].settings.avgVertexNormals + "\n");
            this.pb0 = new ProgressBar("producing offspring", parent[0].v.size() * parent[0].settings.iterations[1]);
        }
        WorkerThread[] threads = new WorkerThread[THREAD_CNT];
        for (int i = 0; i < THREAD_CNT; i++) {
            threads[i] = new WorkerThread(this);
            threads[i].start();
        }
        for(WorkerThread temp:threads){
            try {
                temp.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    public void createTasks(Model[] parent) {
        this.parent = parent;
        for(int i = 0; i < parent[0].v.size(); i++) {
            execute(new ProduceTask(i, parent, this));
        }
    }

    private void execute(Task task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    public Model ret() {
        return offspring;
    }
}