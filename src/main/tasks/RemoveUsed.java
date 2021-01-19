package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;

import java.util.ArrayList;
import java.util.List;

public class RemoveUsed implements Task {
    int i;
    ThreadPool pool;

    public RemoveUsed(int i, ThreadPool pool) {
        this.i = i;
        this.pool = pool;
    }

    @Override
    public void run() {
        if(pool.parent[0].newPoints.size() < pool.parent[0].v.size()) {
        double record = Double.MAX_VALUE;
        int recordIndex = -1;
        for (int j = 0; j < pool.parent[1].v.size(); j++) {
            double num =
                    Math.pow((pool.parent[0].v.get(i)[0] - pool.parent[1].v.get(j)[0]), 2)
                            + Math.pow((pool.parent[0].v.get(i)[1] - pool.parent[1].v.get(j)[1]), 2)
                            + Math.pow((pool.parent[0].v.get(i)[2] - pool.parent[1].v.get(j)[2]), 2);
            double distance = Math.sqrt(num);
            if ((distance < record) && !(pool.parent[0].usedPoints.containsKey(j))) {
                record = distance;
                recordIndex = j;
            }
        }
        double distance = (pool.parent[0].settings.ratio * record)/record;
        double x = pool.parent[0].v.get(i)[0] + (distance*(pool.parent[1].v.get(recordIndex)[0] - pool.parent[0].v.get(i)[0]));
        double y = pool.parent[0].v.get(i)[1] + (distance*(pool.parent[1].v.get(recordIndex)[1] - pool.parent[0].v.get(i)[1]));
        double z = pool.parent[0].v.get(i)[2] + (distance*(pool.parent[1].v.get(recordIndex)[2] - pool.parent[0].v.get(i)[2]));

        double[] midpoint = {x,y,z};
        pool.parent[0].newPoints.put(i, midpoint);
        pool.parent[0].usedPoints.put(recordIndex,new double[]{6,9});

        }
        synchronized(this) {
            if (pool.parent[0].newPoints.size() == pool.parent[0].v.size()) {
                List<double[]> newV = new ArrayList<>();
                for (int i = 0; i < pool.parent[0].v.size(); i++) {
                    newV.add(pool.parent[0].newPoints.get(i));
                }
                pool.parent[0].v = new ArrayList<>(newV);
                pool.offspring = pool.parent[0];
            } else if (pool.parent[0].newPoints.size() > pool.parent[0].v.size()) {
                System.out.println(pool.parent[0].newPoints.size());
            }
        }
    }
}
