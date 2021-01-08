package main;
import com.rits.cloning.Cloner;

public class Main {
    long    startTime = time();
    Settings settings = new Settings();
    public static class Settings {
        String                 file1 = "cow.obj";
        String                 file2 = "mother.obj";
        double                 ratio = .5;
        double[]           translate = {0,0,0};

        boolean             threaded = false; // broken
        boolean   removeUsedVertices = true;  // works in standard
        boolean     standardizeScale = true;
        boolean        centerObjects = true;
        boolean prioritizeByDistance = false; // broken
        boolean        VertexNormals = true;
        boolean     avgVertexNormals = false;
    }

    public static void main(String[] args){
        System.out.println("-hephaestus");
        Main run = new Main();
        run.mainRun();
        run.runTime();
    } void mainRun(){
        Model[] parent = createParents(settings);

        for ( int i = 0; i < 13; i = i + 3) {
            parent[0].settings.translate = new double[]{i, i, 0};
            generate(parent);
        }
    }

    // you are a farmer now , till your fields well
    // intake from a folder , run permutations between all objects in folder
    // accumulate output objects , output the run in multi object .obj






    // ---------------------------------------------------------------------
    void generate(Model[] parent){
        parent[0].translate();
        parent[1].translate();
        Cloner cloner=new Cloner();
        Model[] parentClone = cloner.deepClone(parent);
        if (parent[0].settings.prioritizeByDistance) new GeneratePrioritizeDistance(parentClone);
        else if (parent[0].settings.threaded) new GenerateThreaded(parentClone);
        else new Generate(parentClone);
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


