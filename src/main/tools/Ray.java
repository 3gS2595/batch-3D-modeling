package main.tools;
import main.form.Form;

import java.util.ArrayList;
import java.util.List;

public class Ray {
    public Form[] form;
    public int i;
    public int subDivRecursionLvl;
    public List<double[]> surfPnts = new ArrayList<>();

    public Ray(int i, Form[] form) {
        this.i = i;
        this.form = form;
        this.subDivRecursionLvl = form[0].settings.subDivRecursionLvl;
    }

    // salience found in the leaves of a tree
    public boolean findNearestTris(double[] nn) {
        int nnIndex = -1;
        List<double[][]> nearestTris = new ArrayList<>();
        for(int i = 0; i < this.form[1].v.size();i++){
            if(this.form[1].v.get(i)[0] == nn[0] && this.form[1].v.get(i)[1] == nn[1] && this.form[1].v.get(i)[2] == nn[2] ){
                nnIndex = i;
                i = this.form[1].v.size() + 1;
            }
        }
        List<List<Integer>> polys = form[1].siblingPolys.get(nnIndex);
        if(polys != null) {
            for (List<Integer> poly : polys) {
                double[][] triVerts = new double[poly.size()][form[1].v.get(1).length];
                for (int h = 0; h < poly.size(); h++)
                    triVerts[h] = this.form[1].v.get(poly.get(h));
                nearestTris.add(triVerts.clone());
            }
        } else System.out.println("POLYS WAS NULL WTF " + nnIndex);

        for(double[][] tri : nearestTris) recursiveSubdiv(tri, subDivRecursionLvl);
        return this.surfPnts != null;
    }

    private void recursiveSubdiv(double[][] triVerts, int level) {
        // using three vertices
        // creates new vertices at midpoints
        // these midpoints create new sub triangle
        // 1<->2 1<->3 2<->3
        double midpointRatio = 0.5;
        // double checks model uses triangles
        // 1<->2
        double num = 0;
        for (int z = 0; z < triVerts[0].length; z++) num += Math.pow((triVerts[0][z] - triVerts[1][z]), 2);
        double distance0 = Math.sqrt(num);
        double ratio = (midpointRatio * distance0) / distance0;
        double[] midPnt0 = new double[triVerts.length];
        for(int a = 0; a < triVerts[0].length; a++) {
            midPnt0[a] = (triVerts[0][a] + (ratio * (triVerts[1][a] - triVerts[0][a])));
        }

        // 1<->3
        num = 0;
        for (int z = 0; z <triVerts[0].length; z++) num += Math.pow((triVerts[0][z] - triVerts[2][z]), 2);
        double distance1 = Math.sqrt(num);
        ratio = (midpointRatio * distance1) / distance1;
        double[] midPnt1 = new double[triVerts[0].length];
        for(int a = 0; a < triVerts[0].length; a++) {
            midPnt1[a] = (triVerts[0][a] + (ratio * (triVerts[2][a] - triVerts[0][a])));
        }

        // 2<->3
        num = 0;
        for (int z = 0; z <triVerts[0].length; z++) num += Math.pow((triVerts[0][z] - triVerts[1][z]), 2);
        double distance2 = Math.sqrt(num);
        ratio = (midpointRatio * distance2) / distance2;
        double[] midPnt2 = new double[triVerts[0].length];
        for(int a = 0; a < triVerts[0].length; a++) {
            midPnt2[a] = (triVerts[1][a] + (ratio * (triVerts[2][a] - triVerts[1][a])));
        }

        this.surfPnts.add(midPnt0);
        this.surfPnts.add(midPnt1);
        this.surfPnts.add(midPnt2);
        if (level < subDivRecursionLvl) {
            recursiveSubdiv(new double[][]{triVerts[0], midPnt0, midPnt1}, level + 1);
            recursiveSubdiv(new double[][]{triVerts[1], midPnt0, midPnt2}, level + 1);
            recursiveSubdiv(new double[][]{triVerts[2], midPnt1, midPnt2}, level + 1);
            recursiveSubdiv(new double[][]{midPnt0, midPnt1, midPnt2}, level + 1);
        }
    }
}




