package main.tasks.other;

import main.form.Form;
import main.pool.ThreadPool;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrioritizeDistance implements Task {
    int i;
    ThreadPool pool;
    Form[] parent;
    Map<Integer, Double[][]> closePoints = new HashMap<>();

    public PrioritizeDistance(int i, ThreadPool pool) {
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

//        if(recordIndex!=-1 && parent[0].settings.removeUsedVertices) synlist.remove(recordIndex);
//        System.out.println(parent[0].synlist.size());
        }
        synchronized(this) {
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

    private void analyze(){
        for (int i = 0; i < parent[0].v.size(); i++) {
            Double[][] topPicks = {{-1.0, -1.0}, {-1.0, -1.0}, {-1.0, -1.0}, {-1.0, -1.0}};
            double[] records = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
            // finds closest point in father

            // collect data on closest distance to each point
            // the farthest should have priority
            // collect top 5 for possible collision
            // back up system for top 5 demand overflow
            for (int j = 0; j < parent[1].v.size(); j++) {
                double num = Math.pow((parent[0].v.get(i)[0] - parent[1].v.get(j)[0]), 2)
                        + Math.pow((parent[0].v.get(i)[1] - parent[1].v.get(j)[1]), 2)
                        + Math.pow((parent[0].v.get(i)[2] - parent[1].v.get(j)[2]), 2);
                double distance = Math.sqrt(num);

                // need to collect vertices
                for (int h = 0; h < topPicks.length; h++) {
                    if (distance < records[h]) {
                        records[h] = distance;
                        Double[] temp = {(double) i, (double) j, records[h]};
                        topPicks[h] = temp;
                        h = topPicks.length + 1;
                    }
                }
            }
            closePoints.put(i, topPicks);
        }
    }
}
