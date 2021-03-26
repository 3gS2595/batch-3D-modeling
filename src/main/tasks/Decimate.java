package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;
import java.util.ArrayList;
import java.util.List;

public class Decimate implements Task {
    int i;
    int offIndex;
    ThreadPool pool;
    Form parent;

    public Decimate(int i, int offIndex, ThreadPool pool) {
        this.i = i;
        this.offIndex = offIndex;
        this.pool = pool;
        this.parent = pool.parent[0];
    }

    //use public HashMap<Integer,List<Integer>> siblingPoints = new HashMap<>(); ?

    @Override
    public void run() {
        // collects from qthypercube2
//        Collection nearestNeighbor = parent[1].KdTree.knnQuery(parent[0].v.get(i), 1);
//        double[] closeV = ((QEntry)nearestNeighbor.toArray()[0]).point();


        // inserts refreshed point
        pool.output.get(offIndex).newPoints.put(i, parent.v.get(i));

        // checks for completion
        if (pool.output.get(offIndex).newPoints.size() == parent.v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < parent.v.size(); i++) {
                newV.add(pool.output.get(offIndex).newPoints.get(i));
            }
            pool.output.get(offIndex).v = new ArrayList<>(newV);
        }
    }
}








