package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;

import java.util.List;

//todo  check for point pre-creation
    //if (flag) skip pre existing vertex insertion , retrieve vertex index and insert new face

public class DecimateThreaded implements Task {
    int i;
    int offIndex;
    int offset;
    Form form;

    public DecimateThreaded(int i, int offset, Form form) {
        this.i = i;
        this.offset = offset;
        this.form = form;
    }

    @Override
    public void run() {
        // using three vertices
        // creates new vertices at midpoints
        // these midpoints create new sub triangle
        // 1->2 1->3 2->3

        double midpointRatio = 0.5;

        // salience found in the leaves of a tree
        List<Integer> surface = form.f.get(i);
        double[][] vertices = new double[surface.size()][3];
        for(int h = 0; h< surface.size(); h++){
            vertices[h] = form.v.get(surface.get(h));
        }
        if(vertices[0].length == 3){
            // 1->2
            double num =
                Math.pow((vertices[0][0] - vertices[1][0]), 2) +
                        Math.pow((vertices[0][1] - vertices[1][1]), 2) +
                        Math.pow((vertices[0][2] - vertices[1][2]), 2);
            double distance = Math.sqrt(num);
            // distance*ratio -> midpoint
            double ratio = (midpointRatio * distance)/distance;
            double[] midpoint0 = {
                    vertices[0][0] + (ratio*(vertices[1][0] - vertices[0][0])),
                    vertices[0][1] + (ratio*(vertices[1][1] - vertices[0][1])),
                    vertices[0][2] + (ratio*(vertices[1][2] - vertices[0][2]))};
            if(form.newPoints.contains(midpoint0)) {
                System.out.println("catch reused point");
            }
            form.newPoints.put(offset, midpoint0);

            // 1->3
            num =
                Math.pow((vertices[0][0] - vertices[2][0]), 2) +
                        Math.pow((vertices[0][1] - vertices[2][1]), 2) +
                        Math.pow((vertices[0][2] - vertices[2][2]), 2);
            distance = Math.sqrt(num);
            // distance*ratio -> midpoint
            ratio = (midpointRatio * distance)/distance;
            double[] midpoint1= new double[]{
                    vertices[0][0] + (ratio * (vertices[2][0] - vertices[0][0])),
                    vertices[0][1] + (ratio * (vertices[2][1] - vertices[0][1])),
                    vertices[0][2] + (ratio * (vertices[2][2] - vertices[0][2]))};
            if(form.newPoints.contains(midpoint1)) {
                System.out.println("catch reused point");
            }
            form.newPoints.put( offset+1, midpoint1);

            // 2->3
            num =
                Math.pow((vertices[1][0] - vertices[2][0]), 2) +
                        Math.pow((vertices[1][1] - vertices[2][1]), 2) +
                        Math.pow((vertices[1][2] - vertices[2][2]), 2);
            distance = Math.sqrt(num);
            // distance*ratio -> midpoint
            ratio = (midpointRatio * distance)/distance;
            double[] midpoint2 = new double[]{
                    vertices[1][0] + (ratio * (vertices[2][0] - vertices[1][0])),
                    vertices[1][1] + (ratio * (vertices[2][1] - vertices[1][1])),
                    vertices[1][2] + (ratio * (vertices[2][2] - vertices[1][2]))};
            if(form.newPoints.contains(midpoint2)) {
                System.out.println("catch reused point");
            }
            form.newPoints.put(offset + 2, midpoint2);


            // creates triangles
            form.newFaces.put(new Integer[]{surface.get(0), offset, offset+1},1);
            form.newFaces.put(new Integer[]{surface.get(1), offset, offset+2},1);
            form.newFaces.put(new Integer[]{surface.get(2), offset+1, offset+2},1);
            form.newFaces.put(new Integer[]{offset, offset+1, offset+2},1);
        } else {
            System.out.println("NON-TRIANGLE MESH, SUBDIVISION FAILURE");
        }
    }
}








