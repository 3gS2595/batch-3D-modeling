package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;
import org.tinspin.index.qthypercube2.QEntry;
import org.tinspin.index.qthypercube2.QEntryDist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Standard implements Task {
    private final ThreadPool pool;
    private final Form[] pairSpring;
    private final int i;
    private final int offIndex;

    public Standard(int i, int offIndex, ThreadPool pool) {
        this.pool = pool;
        this.pairSpring = pool.pairSpring;
        this.i = i;
        this.offIndex = offIndex;
    }

    @Override
    public void run() {
        // retrieves nearest neighbor (nn
        Collection<QEntryDist<Object>> nearestNeighbor = pairSpring[1].KdTree.knnQuery(pairSpring[0].v.get(i), 1);
        double[] closeV = ((QEntry<?>)nearestNeighbor.toArray()[0]).point();

        // calculates distance to nn
        double num = 0;
        for (int z = 0; z < pairSpring[0].v.get(i).length; z++) num += Math.pow((pairSpring[0].v.get(i)[z] - closeV[z]), 2);
        double distance = Math.sqrt(num);

        // calculates cartesian point
        double ratio = (pairSpring[0].settings.ratio * distance)/distance;
        double[] midpoint = new double[closeV.length];
        for(int a = 0; a < pairSpring[0].v.get(i).length; a++) {
            midpoint[a] = ( pairSpring[0].v.get(i)[a] + (ratio * (closeV[a] - pairSpring[0].v.get(i)[a])));
        }

        // inserts refreshed point
        pool.output.get(offIndex).newPoints.put(i, midpoint);
        // completion check
        if (pool.output.get(offIndex).newPoints.size() == pairSpring[0].v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < pairSpring[0].v.size(); i++) {
                newV.add(pool.output.get(offIndex).newPoints.get(i));
            }
            pool.output.get(offIndex).v = new ArrayList<>(newV);
        }
    }
}








