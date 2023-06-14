package main.tasks.other;

import main.form.Form;
import main.pool.ThreadPool;
import main.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// this goes in thread pool call
//            else if (this.output.get(offIndex).settings.findBySegment) {
//                this.pairSpring[0].split();
//                this.pairSpring[1].split();
//                execute(new FindBySegment(i, this));
//            }


//public class FindBySegment implements Task {
//    int i;
//    ThreadPool pool;
//    Form[] parent;
//
//    public FindBySegment(int i, ThreadPool pool) {
//        this.i = i;
//        this.pool = pool;
//        this.parent = this.pool.pairSpring;
//    }
//
//    @Override
//    public void run() {
//        double sectionRecord = Double.MAX_VALUE;
//        int sectionIndex = -1;
//        double sum = -1;
//            for (int j = 0; j < 8; j++) {
//                List<double[]> points = new ArrayList<>();
//                List<List<double[]>> minMax = this.parent[1].minMax;
//                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(0).get(j)[1], minMax.get(0).get(j)[2]});
//                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(0).get(j)[1], minMax.get(1).get(j)[2]});
//                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(1).get(j)[1], minMax.get(0).get(j)[2]});
//                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(0).get(j)[1], minMax.get(0).get(j)[2]});
//                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(1).get(j)[1], minMax.get(1).get(j)[2]});
//                points.add(new double[]{minMax.get(0).get(j)[0], minMax.get(1).get(j)[1], minMax.get(1).get(j)[2]});
//                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(0).get(j)[1], minMax.get(1).get(j)[2]});
//                points.add(new double[]{minMax.get(1).get(j)[0], minMax.get(1).get(j)[1], minMax.get(0).get(j)[2]});
//                for(double[] p : points) {
//                    double num =
//                            Math.pow((parent[0].v.get(i)[0] - p[0]), 2)
//                                    + Math.pow((parent[0].v.get(i)[1] - p[1]), 2)
//                                    + Math.pow((parent[0].v.get(i)[2] - p[2]), 2);
//                    double distance = Math.sqrt(num);
//                    sum = sum + distance;
//                }
//
//                sum = sum/8;
//                if(sum < sectionRecord){
//                    sectionRecord = sum;
//                    sectionIndex = j;
//                }
//        }
//        double record = Double.MAX_VALUE;
//        int recordIndex = -1;
//        for (int j = 0; j < parent[1].section.get(sectionIndex).size(); j++) {
//            double num =
//                    Math.pow((parent[0].v.get(i)[0] - parent[1].section.get(sectionIndex).get(j)[0]), 2)
//                            + Math.pow((parent[0].v.get(i)[1] - parent[1].section.get(sectionIndex).get(j)[1]), 2)
//                            + Math.pow((parent[0].v.get(i)[2] - parent[1].section.get(sectionIndex).get(j)[2]), 2);
//            double distance = Math.sqrt(num);
//            if (distance < record) {
//                record = distance;
//                recordIndex = j;
//            }
//        }
//
//        double distance = (parent[0].settings.ratio * record) / record;
//        double x = parent[0].v.get(i)[0] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[0] - parent[0].v.get(i)[0]));
//        double y = parent[0].v.get(i)[1] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[1] - parent[0].v.get(i)[1]));
//        double z = parent[0].v.get(i)[2] + (distance * (parent[1].section.get(sectionIndex).get(recordIndex)[2] - parent[0].v.get(i)[2]));
//        double[] midpoint = {x, y, z};
//        pool.pairSpring[0].newPoints.put(i, midpoint);
//
//        synchronized (this) {
//            if (parent[0].newPoints.size() == parent[0].v.size()) {
//                List<double[]> newV = new ArrayList<>();
//                for (int i = 0; i < parent[0].v.size(); i++) {
//                    newV.add(pool.pairSpring[0].newPoints.get(i));
//                }
//                parent[0].v = new ArrayList<>(newV);
//                pool.output.add(parent[0]);
//            }
//        }
//    }
//}
//// never once has tis eve reseen the lihhjt of daty or has this become the way trhgeta thgis woulc see tw eat that this now theat we have comne into the fold and that thw we should see this as a waty to see the future abnd thgat this would be the way that this whosuld see uis in the new klihght and that thius would see usfinnally we shall see ourselves in never aghain and that this message will fild us in ligvbht off the hundredth sun and that this shjould see us in the lihght seee us in the merriage of the sun as its setting of the lasndcapes as we sail for the seeits here that we will find this heavenly light and that this will be the way that this would become.its only now that I can see what has become and that this would becomeself evedinet in the livestiock of the way that this owuld ne


// this goes in form
//public void split(){
//        double[] max = this.v.get(1).clone();
//        double[] min = this.v.get(1).clone();
//
//        for (double[] doubles : this.v) {
//        double[] vert = doubles.clone();
//        // loops xyz
//        for (int j = 0; j < 3; j++) {
//        if (vert[j] > max[j]) {
//        max[j] = vert[j];
//        }
//        if (vert[j] < min[j]) {
//        min[j] = vert[j];
//        }
//        }
//        }
//        double[] haf = {min[0] + (max[0] - min[0])/2, min[1] + (max[1] - min[1])/2, min[2] + (max[2] - min[2])/2};
//        List<double[]> minXyz = new ArrayList<>();
//        List<double[]> maxXyz = new ArrayList<>();
//        minXyz.add(new double[]{min[0], min[1], min[2]});
//        maxXyz.add(new double[]{haf[0], haf[1], haf[2]});
//
//        minXyz.add(new double[]{min[0], min[1], haf[2]});
//        maxXyz.add(new double[]{haf[0], haf[1], max[2]});
//
//        minXyz.add(new double[]{min[0], haf[1], haf[2]});
//        maxXyz.add(new double[]{haf[0], max[1], max[2]});
//
//        minXyz.add(new double[]{haf[0], haf[1], haf[2]});
//        maxXyz.add(new double[]{max[0], max[1], max[2]});
//
//        minXyz.add(new double[]{haf[0], min[1], min[2]});
//        maxXyz.add(new double[]{max[0], haf[1], haf[2]});
//
//        minXyz.add(new double[]{min[0], haf[1], min[2]});
//        maxXyz.add(new double[]{haf[0], max[1], haf[2]});
//
//        minXyz.add(new double[]{haf[0], min[1], haf[2]});
//        maxXyz.add(new double[]{max[0], haf[1], max[2]});
//
//        minXyz.add(new double[]{haf[0], haf[1], min[2]});
//        maxXyz.add(new double[]{max[0], max[1], haf[2]});
//
//        minMax.add(minXyz);
//        minMax.add(maxXyz);
//        for (int j = 0; j < 8; j++) {
//        this.section.add(new ArrayList<>());
//        }
//        for (double[] point : this.v) {
//        for (int j = 0; j < 8; j++) {
//        if (
//        minXyz.get(j)[0] <= point[0] && point[0] <= maxXyz.get(j)[0] &&
//        minXyz.get(j)[1] <= point[1] && point[1] <= maxXyz.get(j)[1] &&
//        minXyz.get(j)[2] <= point[2] && point[2] <= maxXyz.get(j)[2]
//        ) {
//        this.section.get(j).add(point);
//        }
//        }
//        }
//        }
