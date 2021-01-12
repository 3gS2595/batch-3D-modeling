package main.generate;

import main.model.Model;
import me.tongfei.progressbar.ProgressBar;

public class Generate {
    public static Model gen(Model[] parent) {
        // iterates through each point in mother to find closest in father
        // calculates and records record midpoint
        // removes used point in father (toggleable)

        try (ProgressBar pb0 = new ProgressBar("producing offspring", parent[0].v.size())) {
            for (int i = 0; i < parent[0].v.size(); i++) {
                double   record = Double.MAX_VALUE;
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
                parent[0].v.set(i, midpoint);
                if (parent[0].settings.removeUsedVertices) parent[1].v.remove(recordIndex);
                pb0.step();
            }
        }
        return parent[0];
    }
}