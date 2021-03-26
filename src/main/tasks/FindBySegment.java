package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;

import java.util.ArrayList;
import java.util.List;

public class FindBySegment implements Task {
    int i;
    ThreadPool pool;
    Form[] parent;

    public FindBySegment(int i, ThreadPool pool) {
        this.i = i;
        this.pool = pool;
        this.parent = this.pool.parent;
    }

    @Override
    public void run() {
        double sectionRecord = Double.MAX_VALUE;
        int sectionIndex = -1;
        double sum = -1;
            for (int j = 0; j < 8; j++) {
                List<double[]> points = new ArrayList<>();
                List<List<double[]>> minMax = this.parent[1].minMax;
                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(0).get(j)[1], minMax.get(0).get(j)[2]});
                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(0).get(j)[1], minMax.get(1).get(j)[2]});
                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(1).get(j)[1], minMax.get(0).get(j)[2]});
                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(0).get(j)[1], minMax.get(0).get(j)[2]});
                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(1).get(j)[1], minMax.get(1).get(j)[2]});
                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(1).get(j)[1], minMax.get(1).get(j)[2]});
                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(0).get(j)[1], minMax.get(1).get(j)[2]});
                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(1).get(j)[1], minMax.get(0).get(j)[2]});
                for(double[] p : points) {
                    double num =
                            Math.pow((parent[0].v.get(i)[0] - p[0]), 2)
                                    + Math.pow((parent[0].v.get(i)[1] - p[1]), 2)
                                    + Math.pow((parent[0].v.get(i)[2] - p[2]), 2);
                    double distance = Math.sqrt(num);
                    sum = sum + distance;
                }

                sum = sum/8;
                if(sum < sectionRecord){
                    sectionRecord = sum;
                    sectionIndex = j;
                }
        }
        double record = Double.MAX_VALUE;
        int recordIndex = -1;
        for (int j = 0; j < parent[1].section.get(sectionIndex).size(); j++) {
            double num =
                    Math.pow((parent[0].v.get(i)[0] - parent[1].section.get(sectionIndex).get(j)[0]), 2)
                            + Math.pow((parent[0].v.get(i)[1] - parent[1].section.get(sectionIndex).get(j)[1]), 2)
                            + Math.pow((parent[0].v.get(i)[2] - parent[1].section.get(sectionIndex).get(j)[2]), 2);
            double distance = Math.sqrt(num);
            if (distance < record) {
                record = distance;
                recordIndex = j;
            }
        }

        double distance = (parent[0].settings.ratio * record) / record;
        double x = parent[0].v.get(i)[0] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[0] - parent[0].v.get(i)[0]));
        double y = parent[0].v.get(i)[1] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[1] - parent[0].v.get(i)[1]));
        double z = parent[0].v.get(i)[2] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[2] - parent[0].v.get(i)[2]));
        double[] midpoint = {x, y, z};
        pool.parent[0].newPoints.put(i, midpoint);

        synchronized (this) {
            if (parent[0].newPoints.size() == parent[0].v.size()) {
                List<double[]> newV = new ArrayList<>();
                for (int i = 0; i < parent[0].v.size(); i++) {
                    newV.add(pool.parent[0].newPoints.get(i));
                }
                parent[0].v = new ArrayList<>(newV);
                pool.output.add(parent[0]);
            }
        }
    }
}
