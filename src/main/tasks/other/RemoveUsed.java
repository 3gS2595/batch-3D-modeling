package main.tasks.other;

import main.form.Form;
import main.pool.ThreadPool;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class RemoveUsed implements Task {
    int i;
    ThreadPool pool;
    Form[] parent;

    public RemoveUsed(int i, ThreadPool pool) {
        this.i = i;
        this.pool = pool;
        this.parent = pool.pairSpring;
    }

    @Override
    public void run() {
        if(pool.pairSpring[0].newPoints.size() < pool.pairSpring[0].v.size()) {
        double record = Double.MAX_VALUE;
        int recordIndex = -1;
        for (int j = 0; j < pool.pairSpring[1].v.size(); j++) {
            double num =
                    Math.pow((pool.pairSpring[0].v.get(i)[0] - pool.pairSpring[1].v.get(j)[0]), 2)
                            + Math.pow((pool.pairSpring[0].v.get(i)[1] - pool.pairSpring[1].v.get(j)[1]), 2)
                            + Math.pow((pool.pairSpring[0].v.get(i)[2] - pool.pairSpring[1].v.get(j)[2]), 2);
            double distance = Math.sqrt(num);
            if ((distance < record) && !(pool.pairSpring[0].usedPoints.containsKey(j))) {
                record = distance;
                recordIndex = j;
            }
        }
        double distance = (pool.pairSpring[0].settings.ratio * record)/record;
        double x = pool.pairSpring[0].v.get(i)[0] + (distance*(pool.pairSpring[1].v.get(recordIndex)[0] - pool.pairSpring[0].v.get(i)[0]));
        double y = pool.pairSpring[0].v.get(i)[1] + (distance*(pool.pairSpring[1].v.get(recordIndex)[1] - pool.pairSpring[0].v.get(i)[1]));
        double z = pool.pairSpring[0].v.get(i)[2] + (distance*(pool.pairSpring[1].v.get(recordIndex)[2] - pool.pairSpring[0].v.get(i)[2]));

        double[] midpoint = {x,y,z};
        pool.pairSpring[0].newPoints.put(i, midpoint);
        pool.pairSpring[0].usedPoints.put(recordIndex,new double[]{6,9});

        }
        if (pool.pairSpring[0].newPoints.size() == pool.pairSpring[0].v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < pool.pairSpring[0].v.size(); i++) {
                newV.add(pool.pairSpring[0].newPoints.get(i));
            }
            pool.pairSpring[0].v = new ArrayList<>(newV);
            pool.output.add(parent[0]);
        } else if (pool.pairSpring[0].newPoints.size() > pool.pairSpring[0].v.size()) {
            System.out.println(pool.pairSpring[0].newPoints.size());
        }
    }
}






