package main;

import me.tongfei.progressbar.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenerateThreaded {
    static int cnt;
    static List<double[]> synlist;

    public static Model gen(Model[] parent) {
        GenerateThreaded run = new GenerateThreaded();
        parent[0].v = run.run(parent);
        return parent[0];
    }
    public List<double[]> run(Model[] parent){
        cnt = -1;
        synlist = Collections.synchronizedList(new ArrayList<>(parent[0].v));
        // iterates through each point in mother
        // finds closest point in father
        // calculates and records midpoint record holder
        // removes used point in father (toggleable)
        GenerateThreaded gen = new GenerateThreaded();
        try (ProgressBar pb0 = new ProgressBar("producing offspring", parent[0].v.size())) {
            parent[0].v.parallelStream().forEach(item -> gen.generate(getAndIncrement(), parent, synlist, pb0));
        }
        return synlist;
    }
    public void generate(int i, Model[] parent, List<double[]> synl, ProgressBar pb0) {
        double record = Double.MAX_VALUE;
        int recordIndex = -1;
        for (int j = 0; j < parent[1].v.size(); j++) {
            double num = Math.pow((parent[0].v.get(i)[0] - parent[1].v.get(j)[0]), 2)
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
        synl.set(i, midpoint);

        if(recordIndex!=-1 && parent[0].settings.removeUsedVertices)
            System.out.println("remove vertice feature broken in generateThreaded");
//        if(recordIndex!=-1 && parent[0].settings.removeUsedVertices) synlist.remove(recordIndex);
        pb0.step();
    }

    public static int getAndIncrement(){
        cnt = cnt + 1;
        return cnt;
    }
}
