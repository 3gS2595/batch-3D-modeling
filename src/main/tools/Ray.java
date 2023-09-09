package main.tools;
import main.form.Form;

import java.util.ArrayList;
import java.util.List;

public class Ray {
    public Form[] form;
    public int i;
    public int subDivRecursionLvl;
    public List<double[]> surfPnts = new ArrayList<>();
    public double[] bestPnt;

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

    // salience found in the leaves of a tree
    public boolean findNearestTrisProjection(double[] nn) {
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

        bestPnt = closestPoint(form[0].v.get(i), nearestTris);
        System.out.println(bestPnt[0] + " " + bestPnt[1] + " " + bestPnt[2]);
        System.out.println(form[0].v.get(i)[0] + " " + form[0].v.get(i)[1] + " " + form[0].v.get(i)[2]);
        System.out.println();

        return this.bestPnt != null;
    }


    /**
     * Find the closest orthogonal projection of a point p onto a triangle given by three vertices
     * a, b and c. Returns either the projection point, or null if the projection is not within
     * the triangle.
     */
    public double[] closestPoint(double[] p, List<double[][]> nearestTris) {
        double recordDist = Double.MAX_VALUE;
        double[] recordPnt = null;
        for(double[][] tri : nearestTris) {
            double[] a = tri[0];
            double[] b = tri[1];
            double[] c = tri[2];
            // Find the normal to the plane: n = (b - a) x (c - a)
            double[] n = Maths.cross(Maths.sub(b,a),Maths.sub(c,a));
            // Normalize normal vector
            double nLen = n.length;
            if (nLen < 1.0e-30) {
                return null;  // Triangle is degenerate
            } else {
                n = Maths.mult(n, (1.0f / nLen));
            }

            double dist = Maths.dot(p, n) - (Maths.dot(a, n));
            double[] proj =  Maths.sub(p, Maths.mult(n, dist));

            // Compute edge vectors
            double v0x = c[0] - a[0];
            double v0y = c[1] - a[1];
            double v0z = c[2] - a[2];
            double v1x = b[0] - a[0];
            double v1y = b[1] - a[1];
            double v1z = b[2] - a[2];
            double v2x = proj[0] - a[0];
            double v2y = proj[1] - a[1];
            double v2z = proj[2] - a[2];

            // Compute dot products
            double dot00 = v0x * v0x + v0y * v0y + v0z * v0z;
            double dot01 = v0x * v1x + v0y * v1y + v0z * v1z;
            double dot02 = v0x * v2x + v0y * v2y + v0z * v2z;
            double dot11 = v1x * v1x + v1y * v1y + v1z * v1z;
            double dot12 = v1x * v2x + v1y * v2y + v1z * v2z;

            // Compute barycentric coordinates (u, v) of projection point
            double denom = (dot00 * dot11 - dot01 * dot01);
            if (Math.abs(denom) < 1.0e-30) {
                System.exit(1);
                return null; // Triangle is degenerate
            }
            double invDenom = 1.0 / denom;
            double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

                if(recordDist > dist) {
                    recordDist = dist;
                    recordPnt = proj;
                }
        }
        return recordPnt;
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




