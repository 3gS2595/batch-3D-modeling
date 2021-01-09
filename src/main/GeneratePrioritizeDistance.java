package main;

import me.tongfei.progressbar.ProgressBar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeneratePrioritizeDistance  {
    public static Model gen(Model[] parent){
        // iterates through each point in mother
        Map<Integer, Double[][]> closePoints = new HashMap<>();

        try (ProgressBar pb0 = new ProgressBar("analyzing vertices", parent[0].v.size())) {
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
                pb0.step();
            }
        }

        try (ProgressBar pb1 = new ProgressBar("producing offspring", closePoints.size())) {
            // finds the longest distances
            // iterate through their closest points
            for (int l = 1; l < closePoints.size(); l++) {
                double recordIndexM = -1;
                double recordIndexF = -1;
                double record = 0;
                for (int i = 0; i < closePoints.size(); i++) {
                    if(closePoints.get(i)[1][1] > record){
                        recordIndexM = closePoints.get(i)[0][0];
                        recordIndexF = closePoints.get(i)[1][0];
                        record = closePoints.get(i)[1][1];
                    }
                }
                // calculates and overwrites vertex with midpoint
                double[] midpoint = {(parent[0].v.get((int)recordIndexM)[0] + parent[1].v.get((int)recordIndexF)[0]) / 2
                        , (parent[0].v.get((int)recordIndexM)[1] + parent[1].v.get((int)recordIndexF)[1]) / 2
                        , (parent[0].v.get((int)recordIndexM)[2] + parent[1].v.get((int)recordIndexF)[2]) / 2};
                parent[0].v.set((int)recordIndexM, midpoint);
                closePoints.remove(recordIndexM);
                // removes used point in father
                // REMOVE IS NOT IMPLEMENTED
                // if (parent[0].settings.removeUsedVertices) parent[1].v.remove((int)recordIndexM);
                pb1.stepBy(1);
            }
        }

        return parent[0];
    }
}
