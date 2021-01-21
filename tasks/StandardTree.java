package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;
import org.tinspin.index.qthypercube2.QEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StandardTree implements Task {
    int i;
    ThreadPool pool;
    Form[] parent;

    public StandardTree(int i, ThreadPool pool) {
        this.i = i;
        this.pool = pool;
        this.parent = pool.parent;
    }

    @Override
    public void run() {
        Collection check = parent[1].KdTree.knnQuery(parent[0].v.get(i), 1);
        double[] closeV = ((QEntry)check.toArray()[0]).point();

        // found xyz and distance
        double num =
                Math.pow((parent[0].v.get(i)[0] - closeV[0]), 2) +
                Math.pow((parent[0].v.get(i)[1] - closeV[1]), 2) +
                Math.pow((parent[0].v.get(i)[2] - closeV[2]), 2);
        double distance = Math.sqrt(num);

        // midpoint
        double ratio = (parent[0].settings.ratio * distance)/distance;
        double[] midpoint = {
                parent[0].v.get(i)[0] + (ratio*(closeV[0] - parent[0].v.get(i)[0])),
                parent[0].v.get(i)[1] + (ratio*(closeV[1] - parent[0].v.get(i)[1])),
                parent[0].v.get(i)[2] + (ratio*(closeV[2] - parent[0].v.get(i)[2]))};

        pool.parent[0].newPoints.put(i, midpoint);

        // checks for completion
        if (parent[0].newPoints.size() == parent[0].v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < parent[0].v.size(); i++) {
                newV.add(pool.parent[0].newPoints.get(i));
            }
            parent[0].v = new ArrayList<>(newV);
            pool.offspring = parent[0];
        }
    }
}








