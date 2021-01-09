package main;
import com.rits.cloning.Cloner;

import java.util.ArrayList;
import java.util.List;

public class Main {
    long startTime = time();
    public static class Settings {
        String                 file1 = "father.obj";
        String                 file2 = "cow.obj";
        String        outputFilePath = "/media/sf_digital/hephaestus/dump/";
        String    outputFileNameNote = "";
        int               iterations = 4;
        double                 ratio = 0.5;
        double[]         maxDistance = {2,1,1};  // range of iteration movement
        boolean             threaded = false;    // somewhat broken
        boolean   removeUsedVertices = true;     // works in standard
        boolean     standardizeScale = false;
        boolean        centerObjects = true;
        boolean prioritizeByDistance = false;   // broken
        boolean        VertexNormals = false;
        boolean     avgVertexNormals = false;

        double[]           translate = {0,0,0}; // variable used during moving
        double[]            moveStep = {0,0,0}; // distance each iteration moves
    }

    public static void main(String[] args){
        System.out.println("-hephaestus");
        Main run = new Main();

        Settings settings = new Settings();
        run.mainRun(settings);

        run.runTime();
    }
    void mainRun(Settings settings){
        Model[] parent = createParents(settings);
        List<Model> offspring = new ArrayList<>();

        // sets it back and then iterates forward
        parent[0].settings.translate = new double[]{
                -settings.maxDistance[0],
                -settings.maxDistance[1],
                -settings.maxDistance[2]};
        parent[0].translate();
        parent[0].settings.moveStep = new double[]{
                (settings.maxDistance[0] * 2) / settings.iterations,
                (settings.maxDistance[1] * 2) / settings.iterations,
                (settings.maxDistance[2] * 2) / settings.iterations};

        for ( double i = 0; i < settings.iterations; i++) {
            parent[0].settings.translate = new double[]{
                    parent[0].settings.moveStep[0],
                    parent[0].settings.moveStep[1],
                    parent[0].settings.moveStep[2]};
            parent[0].translate();
            offspring.add(generate(parent));
        }
        new Output(offspring);
    }

    // you are a farmer now , till your fields well
    // intake from a folder , run permutations between all objects in folder
    // handle dimension of polygon correctly support n-dimensions
    // clean out moveStep somehow

    // ---------------------------------------------------------------------
    Model generate(Model[] parent){
        Cloner cloner=new Cloner();
        Model[] parentClone = cloner.deepClone(parent);
        Model offspring;
        if (parent[0].settings.prioritizeByDistance) offspring = GeneratePrioritizeDistance.gen(parentClone);
        else if (parent[0].settings.threaded) offspring = GenerateThreaded.gen(parentClone);
        else offspring = Generate.gen(parentClone);

        return offspring;
    }

    Model[] createParents(Settings settings){
        Model file0 = new Model(settings.file1, 0, settings);
        Model file1 = new Model(settings.file2, 1, settings);

        // [0] = mother , donates polygons
        Model[] parent = {null, null};
        if (file0.v.size() > file1.v.size()) {
            parent[0] = file1;
            parent[1] = file0;
        } else {
            parent[0] = file0;
            parent[1] = file1;
        }
        return parent;
    }

    long time() { return System.currentTimeMillis();}
    void runTime(){
        long m = ((time() - startTime) / 1000) / 60;
        long s = ((time() - startTime) / 1000) % 60;
        System.out.format("runtime %d.%dm ", m, s);
    }
}


