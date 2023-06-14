package main.tasks.other;

import main.form.Form;
import main.pool.ThreadPool;
import main.tasks.Task;
import org.tinspin.index.qthypercube2.QEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoveUsedTree implements Task {
    int i;
    ThreadPool pool;
    Form[] parent;

    public RemoveUsedTree(int i, ThreadPool pool) {
        this.i = i;
        this.pool = pool;
        this.parent = pool.pairSpring;
    }

    @Override
    public void run() {
        Collection check = parent[1].KdTree.knnQuery(parent[0].v.get(i), 1);
        double num =
                Math.pow((parent[0].v.get(i)[0] - ((QEntry)check.toArray()[0]).point()[0]), 2) +
                        Math.pow((parent[0].v.get(i)[1] - ((QEntry)check.toArray()[0]).point()[1]), 2) +
                        Math.pow((parent[0].v.get(i)[2] - ((QEntry)check.toArray()[0]).point()[2]), 2);
        double distance = Math.sqrt(num);
        double ratio = (parent[0].settings.ratio * distance)/distance;
        double[] midpoint = {
                parent[0].v.get(i)[0] + (ratio*(((QEntry)check.toArray()[0]).point()[0] - parent[0].v.get(i)[0])),
                parent[0].v.get(i)[1] + (ratio*(((QEntry)check.toArray()[0]).point()[1] - parent[0].v.get(i)[1])),
                parent[0].v.get(i)[2] + (ratio*(((QEntry)check.toArray()[0]).point()[2] - parent[0].v.get(i)[2]))};
        pool.pairSpring[0].newPoints.put(i, midpoint);

        pool.pairSpring[0].KdTree.remove(new double[]{((QEntry)check.toArray()[0]).point()[0],
                ((QEntry)check.toArray()[0]).point()[1],
                ((QEntry)check.toArray()[0]).point()[2]});

        if (parent[0].newPoints.size() == parent[0].v.size()) {
            List<double[]> newV = new ArrayList<>();
            for (int i = 0; i < parent[0].v.size(); i++) {
                newV.add(pool.pairSpring[0].newPoints.get(i));
            }
            parent[0].v = new ArrayList<>(newV);
            pool.output.add(parent[0]);
        }
    }
}
