package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;

import java.util.*;
import java.util.function.Predicate;

public class Decimate implements Task {
        ThreadPool pool;
        Form wellspring;
        double edgLenAvrgAll = 0;
        int edgecnt = 0;

        public Decimate(Form wellspring, ThreadPool pool) {
            this.wellspring = wellspring;
            this.pool = pool;
        }

        @Override
        public void run() {

            double midpointRatio = 0.5;
            // salience found in the leaves of a tree
            int j = wellspring.f.size();
            for(int i = 0; i < j; i++) {
                double edgLenAvrg = 0;
                // gets indices used in triangle
                List<Integer> triRawvIndices = wellspring.f.get(i);
                recursiveDecimate(triRawvIndices, wellspring);
                pool.pb0.step();
            }
            System.out.println("\naverage distance" + edgLenAvrgAll / edgecnt);
        }

        // using three vertices
        // creates new vertices at midpoints
        // these midpoints creates the new sub triangle
        // 1->2, 1->3, 2->3
        public void recursiveDecimate(List<Integer> rawF, Form form){
            double midpointRatio = 0.5;
            //sums triangle edges
            ArrayList<Double> edgLengths = new ArrayList<>();
            //sums single edge lengths (reused - reset preceding each triangle
            double edgeLength = 0;

            //collects vertex coordinates
            int offset0 = wellspring.v.size();
            int offset1 = offset0 + 1;
            int offset2 = offset0 + 2;
            double[][] triVrts = new double[rawF.size()][3];
            for(int h = 0; h < rawF.size(); h++) triVrts[h] = wellspring.v.get(rawF.get(h));

          // 1->2
            edgeLength = 0;
            for (int z = 0; z <triVrts.length; z++) edgeLength += Math.pow((triVrts[0][z] - triVrts[1][z]), 2);
            double dist0 = Math.sqrt(edgeLength);
            edgLengths.add(dist0);
            // midpoint
            double ratio = (midpointRatio * dist0) / dist0;
            double[] midPnt0 = new double[triVrts[0].length];
            for(int a = 0; a < triVrts[0].length; a++)
                midPnt0[a] = (triVrts[0][a] + (ratio * (triVrts[1][a] - triVrts[0][a])));

          // 1->3
            edgeLength = 0;
            for (int z = 0; z <triVrts.length; z++) edgeLength += Math.pow((triVrts[0][z] - triVrts[2][z]), 2);
            double dist1 = Math.sqrt(edgeLength);
            edgLengths.add(dist1);
            // midpoint
            ratio = (midpointRatio * dist1) / dist1;
            double[] midPnt1 = new double[triVrts[0].length];
            for(int a = 0; a < triVrts[0].length; a++) {
                midPnt1[a] = (triVrts[0][a] + (ratio * (triVrts[2][a] - triVrts[0][a])));
            }

          // 2->3
            edgeLength = 0;
            for (int z = 0; z <triVrts.length; z++) edgeLength += Math.pow((triVrts[1][z] - triVrts[2][z]), 2);
            double dist2 = Math.sqrt(edgeLength);
            edgLengths.add(dist2);
            // midpoint
            ratio = (midpointRatio * dist2) / dist2;
            double[] midPnt2 = new double[triVrts[0].length];
            for(int a = 0; a < triVrts[0].length; a++) {
                midPnt2[a] = (triVrts[1][a] + (ratio * (triVrts[2][a] - triVrts[1][a])));
            }

//            continue this thread, every moment chance can be imbued should be capitalized on
//            double min =  0.0010484950137484723;
//            double max =  0.0020484950137484723;
//            Random r = new Random();
//            double cut = min + (max - min) * r.nextDouble();
            double cut =  0.002984950137484723;

            // decides which triangle pattern to use
            // compensates if unusually oblong
            double edgeLenAvg  = 0;
            double edgeMin = Double.MAX_VALUE;
            double edgeMax = 0;
            for(double cur : edgLengths) {
                if(cur == 0) {
                    System.out.println("EDGE ZERO FLAG HIT");
                    for(Integer curs : rawF){
                        System.out.println(curs);
                    }
                }
                edgeLenAvg += cur;
                if(cur > edgeMax) edgeMax = cur;
                if(cur < edgeMin) edgeMin = cur;
            }

            if((edgeLenAvg/edgLengths.size()) > cut) {
                // Set of the entries from the
                // HashMap

                if(edgLengths.size() != 3){
                    System.out.println("wait what check out edge length size");
                }
                // creates triangles
                ArrayList<ArrayList<Integer>> tris = new ArrayList<>();
                form.newPoints.put(offset0, midPnt0);
                form.newPoints.put(offset0 + 1, midPnt1);
                form.newPoints.put(offset0 + 2, midPnt2);
                for(Map.Entry cur :  form.newPoints.entrySet()){
                    double[] temp = (double[])cur.getValue();

                    // iterates through created points checking for repeats
                    if((temp[0] == midPnt0[0] || temp[0] == midPnt0[1] || temp[0] == midPnt0[2])
                            && (temp[1] == midPnt0[0] || temp[1] == midPnt0[1] || temp[1] == midPnt0[2])
                            && (temp[2] == midPnt0[0] || temp[2] == midPnt0[1] || temp[2] == midPnt0[2])) {
                        offset0 = (Integer)cur.getKey();

                    } else {
                        form.newPoints.put(offset0, deepCopy(midPnt0));
                        wellspring.v.add(deepCopy(midPnt0));
                    }

                    if((temp[0] == midPnt1[0] || temp[0] == midPnt1[1] || temp[0] == midPnt1[2])
                            && (temp[1] == midPnt1[0] || temp[1] == midPnt1[1] || temp[1] == midPnt1[2])
                            && (temp[2] == midPnt1[0] || temp[2] == midPnt1[1] || temp[2] == midPnt1[2])) {
                        offset1 = (Integer)cur.getKey();

                    } else {
                        form.newPoints.put(offset1, deepCopy(midPnt1));
                        wellspring.v.add(deepCopy(midPnt1));
                    }

                    if((temp[0] == midPnt2[0] || temp[0] == midPnt2[1] || temp[0] == midPnt2[2])
                            && (temp[1] == midPnt2[0] || temp[1] == midPnt2[1] || temp[1] == midPnt2[2])
                            && (temp[2] == midPnt2[0] || temp[2] == midPnt2[1] || temp[2] == midPnt2[2])) {
                        offset2 = (Integer)cur.getKey();

                    } else {
                        form.newPoints.put(offset2, deepCopy(midPnt2));
                        wellspring.v.add(deepCopy(midPnt2));
                    }
                }


                tris.add(insertTri(rawF.get(0), offset0, offset0 + 1, wellspring));
                tris.add(insertTri(rawF.get(1), offset0, offset0 + 2, wellspring));
                tris.add(insertTri(rawF.get(2), offset0 + 1, offset0 + 2, wellspring));
                tris.add(insertTri(offset0, offset0 + 1, offset0 + 2, wellspring));

                // recursion call canonizing point cloud density
                for(ArrayList<Integer> cur : tris) {
                    recursiveDecimate(cur, wellspring);
                }
            } else {
                System.out.println("touch non triangle flag ");
            }
        }
        double[] deepCopy(double[] matrix) {
            double[] doubles = new double[matrix.length];
            System.arraycopy(matrix, 0, doubles, 0,matrix.length);
            return doubles;
        }

        private ArrayList<Integer> insertTri(int a, int b , int c, Form Wellspring){
            ArrayList<Integer> tempF = new ArrayList<>();
            tempF.add(a);
            tempF.add(b);
            tempF.add(c);
            wellspring.f.add(tempF);
            return tempF;
        }
    }
