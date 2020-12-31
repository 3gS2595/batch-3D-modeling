package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainClass {
    public static void main(String[] args) throws IOException {
        Model file0 = new Model("sphere1.obj", 0);
        Model file1 = new Model("cow.obj", 1);

        // assigns roles based on vertex counts
        // [0] = mother ( fewer vertices, looks for closest)
        // [1] = father ( more vertices, used to find closest)
        Model[] parent = {null, null};
        if (file0.verts.size() > file1.verts.size()) {
            parent[0] = file1;
            parent[1] = file0;
        } else {
            parent[0] = file0;
            parent[1] = file1;
        }

        // iterates through each point in mother
        // finds closest point in father
        // calculates and records midpoint
        // removes used point in father (toggleable)
        for (int i = 0; i < parent[0].verts.size(); i++){
            double record = Double.MAX_VALUE;
            double[] recordHolder = null;
            int recordIndex = -1;
            for (int j = 0; j < parent[1].verts.size(); j++){
                double num = Math.pow((parent[0].verts.get(i)[0] - parent[1].verts.get(j)[0]), 2)
                        + Math.pow((parent[0].verts.get(i)[1] - parent[1].verts.get(j)[1]), 2)
                        + Math.pow((parent[0].verts.get(i)[2] - parent[1].verts.get(j)[2]), 2);
                double distance = Math.sqrt(num);
//                System.out.println(v2[0] + " " + v2[1] + " " + v2[2]);
                if (distance < record){
                    record = distance;
                    recordHolder = parent[1].verts.get(j).clone();
                    recordIndex = j;
                }
            }
            System.out.println(i);
            double[] midpoint = {(parent[0].verts.get(i)[0] + parent[1].verts.get(recordIndex)[0])/2
                    , (parent[0].verts.get(i)[1] + parent[1].verts.get(recordIndex)[1])/2
                    , (parent[0].verts.get(i)[2] + parent[1].verts.get(recordIndex)[2])/2};
            parent[0].verts.set(i, midpoint);
            parent[1].verts.remove(recordIndex);
        }
        output(parent[0]);
    }

    public static void output(Model child) throws IOException {
        String dirPath = "child.obj";
        File file = new File(dirPath);
        System.out.println(dirPath);

        // Prints .obj
        FileWriter writer = new FileWriter(file);
        writer.write("# Blender v2.79 (sub 0) OBJ File: 'checker.blend'\n");
        writer.write("# www.blender.org\n");
        for(double[] v: child.verts) {
            writer.write("v " + v[0] + " " + v[1] + " " + v[2] + "\n");
        }
        for(String line: child.rawPolys) {
            writer.write(line + "\n");
        }
        writer.close();
    }
}
