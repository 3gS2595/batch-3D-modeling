package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;

import java.util.ArrayList;
import java.util.List;

    public class Decimate implements Task {
        int offIndex;
        ThreadPool pool;
        Form wellspring;
        double edgLenAvrgAll = 0;
        int edgecnt = 0;
        int newPointsCnt = 0;

        public Decimate(Form wellspring, ThreadPool pool) {
            this.wellspring = wellspring;
            this.pool = pool;
        }

        @Override
        public void run() {
            // using three vertices
            // creates new vertices at midpoints
            // these midpoints create new sub triangle
            // 1->2 1->3 2->3

            double midpointRatio = 0.5;
            // salience found in the leaves of a tree
            for(int i = 0, j = wellspring.f.size(); i < j; i++) {
                double edgLenAvrg = 0;
                // gets indices used in triangle
                // places them in triVrts
                List<Integer> triRawvIndices = wellspring.f.get(i);
                double[][] triVrts = new double[triRawvIndices.size()][3];
                for(int h = 0; h < triRawvIndices.size(); h++){
                    triVrts[h] = wellspring.v.get(triRawvIndices.get(h));
                }

                // double checks model uses triangles
                if(triVrts.length != 2) {
                    int offset = wellspring.v.size();
                    double num = 0;

                    // 1->2
                    num = 0;
                    for (int z = 0; z <triVrts.length; z++) num += Math.pow((triVrts[0][z] - triVrts[1][z]), 2);
                    double dstnce0 = Math.sqrt(num);
                    edgLenAvrg += dstnce0;
                    edgLenAvrgAll += dstnce0;
                    // midpoint
                    double ratio = (midpointRatio * dstnce0) / dstnce0;
                    double[] midpoint0 = new double[triVrts[0].length];
                    for(int a = 0; a < triVrts[0].length; a++)
                        midpoint0[a] = (triVrts[0][a] + (ratio * (triVrts[1][a] - triVrts[0][a])));

                    // 1->3
                    num = 0;
                    for (int z = 0; z <triVrts.length; z++) num += Math.pow((triVrts[0][z] - triVrts[2][z]), 2);
                    double dstnce1 = Math.sqrt(num);
                    edgLenAvrg += dstnce1;
                    edgLenAvrgAll += dstnce1;
                    // midpoint
                    ratio = (midpointRatio * dstnce1) / dstnce1;
                    double[] midpoint1 = new double[triVrts[0].length];
                    for(int a = 0; a < triVrts[0].length; a++) {
                        midpoint1[a] = (triVrts[0][a] + (ratio * (triVrts[2][a] - triVrts[0][a])));
                    }

                    // 2->3
                    num = 0;
                    for (int z = 0; z <triVrts.length; z++) num += Math.pow((triVrts[1][z] - triVrts[2][z]), 2);
                    double dstnce2 = Math.sqrt(num);
                    edgLenAvrg += dstnce2;
                    edgLenAvrgAll += dstnce2;
                    // 2->3 midpoint
                    ratio = (midpointRatio * dstnce2) / dstnce2;
                    double[] midpoint2 = new double[triVrts[0].length];
                    for(int a = 0; a < triVrts[0].length; a++) {
                        midpoint2[a] = (triVrts[1][a] + (ratio * (triVrts[2][a] - triVrts[1][a])));
                    }

                    if((edgLenAvrg/3) > 0.000004851310009745261) {
                        wellspring.v.add(offset, midpoint0);
                        wellspring.v.add(offset + 1, midpoint1);
                        wellspring.v.add(offset + 2, midpoint2);

                        // creates triangles
                        ArrayList<Integer> tempF = new ArrayList<>();
                        tempF.add(triRawvIndices.get(0));
                        tempF.add(offset);
                        tempF.add( offset + 1);
                        wellspring.f.add(tempF);
                        tempF.clear();
                        tempF.add(triRawvIndices.get(1));
                        tempF.add(offset);
                        tempF.add( offset + 2);
                        wellspring.f.add(tempF);
                        tempF.clear();
                        tempF.add(triRawvIndices.get(2));
                        tempF.add(offset + 1);
                        tempF.add( offset + 2);
                        wellspring.f.add(tempF);
                        tempF.clear();
                        tempF.add(offset);
                        tempF.add(offset + 1);
                        tempF.add( offset + 2);
                        wellspring.f.add(tempF);
                        newPointsCnt = newPointsCnt + 3;
                        edgecnt += 3;
                    } else {
                        // TODO this causes issues in DECIMATE
                        // TODO this is most likely do to the tri raws not being populated
                        pool.output.get(offIndex).newFaces.put(new Integer[]{triRawvIndices.get(0), triRawvIndices.get(1), triRawvIndices.get(2)}, 1);
                        edgecnt += 1;
                    }
                } else {
                        System.out.println("CARE FULL-NON-TRIANGLE");
                }
                pool.pb0.step();
            }
            System.out.println("\naverage distance" + edgLenAvrgAll / edgecnt);
        }
    }
