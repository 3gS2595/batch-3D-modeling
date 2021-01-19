package main.pool;

import main.form.Form;
import main.tasks.RemoveUsed;
import main.tasks.Standard;
import main.tasks.Task;
import me.tongfei.progressbar.ProgressBar;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool extends Thread{
    // args[]
    private static int THREAD_CNT;
    public Form[] parent;

    // Book keeping
    // private static final LinkedList<Task>  priority = new LinkedList<>();
    private static final LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
    public Form offspring;
    public ProgressBar pb0;

    public ThreadPool(int threadCnt) {
        ThreadPool.THREAD_CNT = threadCnt;
    }

    public void run(){
        //initializes workerThreads
        if(this.pb0 == null) {
            System.out.println(parent[0].id + " , " + parent[1].id );
            this.pb0 = new ProgressBar("producing offspring", parent[0].v.size() * parent[0].settings.iterations);
        }
        WorkerThread[] threads = new WorkerThread[THREAD_CNT];
        for (int i = 0; i < THREAD_CNT; i++) {
            threads[i] = new WorkerThread(this);
            threads[i].start();
        }
        for(WorkerThread temp:threads){
            try { temp.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
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

    public void initializeTask(Form[] parent) {
        this.parent = parent;
        for(int i = 0; i < parent[0].v.size(); i++) {
            if (parent[0].settings.removeUsedVertices)
                execute(new RemoveUsed(i, this));
            else execute(new Standard(i, this));
        }
    }

    private void execute(Task task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    public Form ret() {
        return offspring;
    }
}