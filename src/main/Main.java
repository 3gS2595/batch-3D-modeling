package main;
import com.rits.cloning.Cloner;
import main.generate.Generate;
import main.generate.GenerateDistPrioritize;
import main.generate.GenerateThreaded;
import main.model.Model;
import main.model.Output;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class Main {
    long startTime = time();
    public static class Settings {
        public String                 file1 = "cow.obj";
        public String                 file2 = "arrow.obj";
        public String        outputFilePath = "/media/sf_digital/hephaestus/dump/";
        public String    outputFileNameNote = "";
        public int                threadCnt = 4;
        public int[]             iterations = {0, 10};
        public double                 ratio = 0.5;
        public double[]         maxDistance = {2,0,0};  // range of iteration movement
        public boolean             threaded = true;    // somewhat broken
        public boolean   removeUsedVertices = false;     // works in standard
        public boolean     standardizeScale = true;
        public boolean        centerObjects = true;
        public boolean prioritizeByDistance = false;   // broken
        public boolean        VertexNormals = false;
        public boolean     avgVertexNormals = false;

        public double[]           translate = {0,0,0}; // variable used during moving
        public double[]            moveStep = {0,0,0}; // distance each iteration moves
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
                (settings.maxDistance[0] * 2) / settings.iterations[1],
                (settings.maxDistance[1] * 2) / settings.iterations[1],
                (settings.maxDistance[2] * 2) / settings.iterations[1]};

        ThreadPool main = new ThreadPool(settings.threadCnt);
        for ( double i = 0; i < settings.iterations[1]; i++) {
            parent[0].settings.translate = new double[]{
                    parent[0].settings.moveStep[0],
                    parent[0].settings.moveStep[1],
                    parent[0].settings.moveStep[2]};
            parent[0].translate();

            Cloner cloner=new Cloner();
            Model[] parentClone = cloner.deepClone(parent);
            main.createTasks(parentClone);
            main.run();
            main.pb0.setExtraMessage((i+1) +  "/" + settings.iterations[1]);
            offspring.add(main.ret());
        }
        main.pb0.close();
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
        if (parent[0].settings.prioritizeByDistance) offspring = GenerateDistPrioritize.gen(parentClone);
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


