package main.tasks.other;

import main.form.Form;
import main.pool.ThreadPool;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class empty implements Task {
    int i;
    int offIndex;
    ThreadPool pool;
    Form[] parent;

    public empty(int i, int offIndex, ThreadPool pool) {
        this.i = i;
        this.offIndex = offIndex;
        this.pool = pool;
        this.parent = pool.parent;
    }

    @Override
    public void run() {
        // collects from qthypercube2
//        Collection nearestNeighbor = parent[1].KdTree.knnQuery(parent[0].v.get(i), 1);
//        double[] closeV = ((QEntry)nearestNeighbor.toArray()[0]).point();


        // inserts refreshed point
//        pool.offspring.get(offIndex).newPoints.put(i, midpoint);

        // checks for completion
        if (pool.offspring.get(offIndex).newPoints.size() == parent[0].v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < parent[0].v.size(); i++) {
                newV.add(pool.offspring.get(offIndex).newPoints.get(i));
            }
            pool.offspring.get(offIndex).v = new ArrayList<>(newV);
        }
    }
}