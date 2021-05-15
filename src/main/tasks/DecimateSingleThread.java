package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;
import java.util.List;

    public class DecimateSingleThread implements Task {
        int offIndex;
        ThreadPool pool;
        Form parent;
        double averageAll = 0;
        int cntAll = 0;
        int newPointsCnt = 0;

        public DecimateSingleThread(int offIndex, ThreadPool pool) {
            this.offIndex = offIndex;
            this.pool = pool;
            this.parent = pool.parent[0];
        }

        @Override
        public void run() {
            // using three vertices
            // creates new vertices at midpoints
            // these midpoints create new sub triangle
            // 1->2 1->3 2->3

            double midpointRatio = 0.5;
            // salience found in the leaves of a tree
            for(int i = 0, j = pool.output.get(offIndex).f.size(); i < j; i++) {
                double averageOne = 0;
                // gets indices used in triangle
                // places them in triVerts
                List<Integer> triRawvIndices = parent.f.get(i);
                double[][] triVerts = new double[triRawvIndices.size()][3];
                for(int h = 0; h < triRawvIndices.size(); h++){
                    triVerts[h] = parent.v.get(triRawvIndices.get(h));
                }

                // double checks model uses triangles
                if(triVerts[0].length == 3) {
                    // 1->2
                    int offset = pool.output.get(offIndex).newV.size();
                    double num =
                            Math.pow((triVerts[0][0] - triVerts[1][0]), 2) +
                                    Math.pow((triVerts[0][1] - triVerts[1][1]), 2) +
                                    Math.pow((triVerts[0][2] - triVerts[1][2]), 2);
                    double distance0 = Math.sqrt(num);
                    averageAll += distance0;
                    cntAll++;
                    averageOne += distance0;
                    // midpoint
                    double ratio = (midpointRatio * distance0) / distance0;
                    double[] midpoint0 = {
                            triVerts[0][0] + (ratio * (triVerts[1][0] - triVerts[0][0])),
                            triVerts[0][1] + (ratio * (triVerts[1][1] - triVerts[0][1])),
                            triVerts[0][2] + (ratio * (triVerts[1][2] - triVerts[0][2]))};


                    // 1->3
                    num =
                            Math.pow((triVerts[0][0] - triVerts[2][0]), 2) +
                                    Math.pow((triVerts[0][1] - triVerts[2][1]), 2) +
                                    Math.pow((triVerts[0][2] - triVerts[2][2]), 2);
                    double distance1 = Math.sqrt(num);
                    averageAll += distance1;
                    cntAll++;
                    averageOne += distance0;
                    // midpoint
                    ratio = (midpointRatio * distance1) / distance1;
                    double[] midpoint1 = new double[]{
                            triVerts[0][0] + (ratio * (triVerts[2][0] - triVerts[0][0])),
                            triVerts[0][1] + (ratio * (triVerts[2][1] - triVerts[0][1])),
                            triVerts[0][2] + (ratio * (triVerts[2][2] - triVerts[0][2]))};


                    // 2->3
                    num =
                            Math.pow((triVerts[1][0] - triVerts[2][0]), 2) +
                                    Math.pow((triVerts[1][1] - triVerts[2][1]), 2) +
                                    Math.pow((triVerts[1][2] - triVerts[2][2]), 2);
                    double distance2 = Math.sqrt(num);
                    averageAll += distance2;
                    cntAll++;
                    averageOne += distance0;
                    // 2->3 midpoint
                    ratio = (midpointRatio * distance2) / distance2;
                    double[] midpoint2 = new double[]{
                            triVerts[1][0] + (ratio * (triVerts[2][0] - triVerts[1][0])),
                            triVerts[1][1] + (ratio * (triVerts[2][1] - triVerts[1][1])),
                            triVerts[1][2] + (ratio * (triVerts[2][2] - triVerts[1][2]))};




                    if((averageOne/3) > 0.00025566332731181767) {
                        pool.output.get(offIndex).newV.put(offset, midpoint0);
                        pool.output.get(offIndex).newV.put(offset + 1, midpoint1);
                        pool.output.get(offIndex).newV.put(offset + 2, midpoint2);

                        // creates triangles
                        pool.output.get(offIndex).newF.put(new Integer[]{triRawvIndices.get(0), offset, offset + 1}, 1);
                        pool.output.get(offIndex).newF.put(new Integer[]{triRawvIndices.get(1), offset, offset + 2}, 1);
                        pool.output.get(offIndex).newF.put(new Integer[]{triRawvIndices.get(2), offset + 1, offset + 2}, 1);
                        pool.output.get(offIndex).newF.put(new Integer[]{offset, offset + 1, offset + 2}, 1);
                        newPointsCnt = newPointsCnt + 3;
                    } else {
                        pool.output.get(offIndex).newF.put(new Integer[]{triRawvIndices.get(0), triRawvIndices.get(1), triRawvIndices.get(2)}, 1);
                    }
                } else {
                        System.out.println("NON-TRIANGLE MESH, SUBDIVISION FAILURE");
                }
                pool.pb0.step();
            }
            System.out.println("\naverage distance" + averageAll / cntAll);
        }
    }
