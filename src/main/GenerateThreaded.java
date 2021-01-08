package main;

import me.tongfei.progressbar.ProgressBar;

import java.io.IOException;
import java.util.Arrays;

public class GenerateThreaded {
    int cnt = -1;
    public GenerateThreaded(Model[] parent){
        // iterates through each point in mother
        // finds closest point in father
        // calculates and records midpoint record holder
        // removes used point in father (toggleable)
        try (ProgressBar pb0 = new ProgressBar("producing offspring", parent[0].v.size())) {
            parent[0].v.parallelStream().forEach(item -> generate(getAndIncrement(), parent, pb0));
        }
        new Output(parent[0], parent[0].OBJname, parent[1].OBJname);
    }
    public void generate(int i, Model[] parent, ProgressBar pb0) {
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

        double[] midpoint = {(parent[0].v.get(i)[0] + parent[1].v.get(recordIndex)[0]) / 2
                , (parent[0].v.get(i)[1] + parent[1].v.get(recordIndex)[1]) / 2
                , (parent[0].v.get(i)[2] + parent[1].v.get(recordIndex)[2]) / 2};
        parent[0].v.set(i, midpoint);

        //create synchronized list and modify that and then switch it back in for the models.v
        // might not need to switch in since it will be thrown away i think
        //if(recordIndex!=-1 && parent[0].settings.removeUsedVertices)parent[1].v.remove(recordIndex);
        pb0.step();
    }

    public int getAndIncrement(){
        cnt = cnt + 1;
        return cnt;
    }
}
