package main;
import com.rits.cloning.Cloner;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import main.tools.FileLoader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static class Settings {
        public String           inputFolder = "/media/sf_digital/hephaestus/intake";
        public String        outputFilePath = "/media/sf_digital/hephaestus/dump/";
        public String   outputFileNameNotes = "";
        public boolean   removeUsedVertices = false;
        public boolean prioritizeByDistance = false;    // broken
        public double                 ratio = 0.5;
        public double[]         maxDistance = {5,0,0};  // range of iteration movement
        public double[]            rotation = {0,0,0};  // range of iteration movement
        public int               iterations = 5;
        public boolean     standardizeScale = true;
        public boolean        centerObjects = true;
        public boolean        VertexNormals = false;
        public boolean     avgVertexNormals = false;
        // closet variables
        public int                threadCnt = 4;
        public double[]           translate = {0,0,0}; // variable used during moving
        public double[]            moveStep = {0,0,0}; // distance each iteration moves
        public String                 file0 = "";
        public String                 file1 = "";
    }

    public static void main(String[] args){
        System.out.println("-hephaestus");
        Main task = new Main();
        Settings settings = new Settings();
        task.run(settings);
        task.runTime();
    }
    void run(Settings settings){
        List<Form[]> wells = FileLoader.loadWells(settings);
        List<Form> offspring = new ArrayList<>();
        printSettings(settings);

        for(Form[] well : wells) {
            ThreadPool pool = new ThreadPool(settings.threadCnt);

            for (double i = 0; i < settings.iterations; i++) {
                step(well);
                pool.initializeTask(clone(well));
                pool.run();
                pool.pb0.setExtraMessage((i + 1) + "/" + settings.iterations);
                offspring.add(pool.ret());
            }
            pool.pb0.close();
            new ObjOutput(offspring);
        }
    }


    // ---------------------------------------------------------------------
    // you are a farmer now , till your fields well
    // rotate the forms rather than move
    // output in stepped output to be viewed together in blender
    // decide on a placement hierarchy / organization

    // moves the form before each iteration
    private void step(Form[] wellspring){
        wellspring[0].settings.translate = new double[]{
                wellspring[0].settings.moveStep[0],
                wellspring[0].settings.moveStep[1],
                wellspring[0].settings.moveStep[2]};
        wellspring[0].translate();
    }

    private void printSettings(Settings settings) {
        System.out.print("           iterations " + settings.iterations + "\n");
        System.out.print("   removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("     standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("        centerObjects " + settings.centerObjects + "\n");
        System.out.print(" prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("        vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("     avgVertexNormals " + settings.avgVertexNormals + "\n");
    }

    private Form[] clone(Form[] wellspring){
        Cloner cloner = new Cloner();
        return cloner.deepClone(wellspring);
    }

    long startTime = time();
    long time() { return System.currentTimeMillis();}
    void runTime(){
        long m = ((time() - startTime) / 1000) / 60;
        long s = ((time() - startTime) / 1000) % 60;
        System.out.format("runtime %d.%dm ", m, s);
    }
}


