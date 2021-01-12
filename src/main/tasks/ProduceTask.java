package main.tasks;

import main.model.Model;
import main.model.Output;
import main.pool.Task;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class ProduceTask implements Task {
    int i;
    Model[] parent;
    ThreadPool pool;

    public ProduceTask(int i, Model[] parent, ThreadPool pool) {
        this.parent = parent;
        this.i = i;
        this.pool = pool;
    }

    @Override
    public void run() {
        if(parent[0].synlist.size() < parent[0].v.size()) {
        double record = Double.MAX_VALUE;
        int recordIndex = -1;
        for (int j = 0; j < parent[1].v.size(); j++) {
            double num =
                    Math.pow((parent[0].v.get(i)[0] - parent[1].v.get(j)[0]), 2)
                            + Math.pow((parent[0].v.get(i)[1] - parent[1].v.get(j)[1]), 2)
                            + Math.pow((parent[0].v.get(i)[2] - parent[1].v.get(j)[2]), 2);
            double distance = Math.sqrt(num);
            if (distance < record) {
                record = distance;
                recordIndex = j;
            }
        }

        double distance = (parent[0].settings.ratio * record)/record;
        double x = parent[0].v.get(i)[0] + (distance*(parent[1].v.get(recordIndex)[0] - parent[0].v.get(i)[0]));
        double y = parent[0].v.get(i)[1] + (distance*(parent[1].v.get(recordIndex)[1] - parent[0].v.get(i)[1]));
        double z = parent[0].v.get(i)[2] + (distance*(parent[1].v.get(recordIndex)[2] - parent[0].v.get(i)[2]));

        double[] midpoint = {x,y,z};
        pool.parent[0].synlist.put(i, midpoint);

//        if(recordIndex!=-1 && parent[0].settings.removeUsedVertices) synlist.remove(recordIndex);
//        System.out.println(parent[0].synlist.size());
        }
        synchronized(this) {
            if (parent[0].synlist.size() == parent[0].v.size()) {
                List<double[]> newV = new ArrayList<>();
                for (int i = 0; i < parent[0].v.size(); i++) {
                    newV.add(pool.parent[0].synlist.get(i));
                }
                parent[0].v = new ArrayList<>(newV);
                pool.offspring = parent[0];
            } else if (parent[0].synlist.size() > parent[0].v.size()) {
                System.out.println(parent[0].synlist.size());
            }
        }
    }
}
