package main;
import com.rits.cloning.Cloner;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import main.tools.FileSetUp;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        System.out.println("-hephaestus");
        Main task = new Main();
        Settings settings = new Settings();
        task.run(settings);
        task.runTime();
    }
    void run(Settings settings){
        List<Form[]> wells = FileSetUp.loadWells(settings);
        printSettings(settings);

        for(Form[] well : wells) {
            ThreadPool pool = new ThreadPool(settings.threadCnt);
            List<Form> offspring = new ArrayList<>();
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

    // moves the form before each iteration
    private void step(Form[] wellspring){
        wellspring[0].settings.translate = new double[]{
                wellspring[0].settings.moveStep[0],
                wellspring[0].settings.moveStep[1],
                wellspring[0].settings.moveStep[2]};
        wellspring[0].translate();
        wellspring[0].rotate();
    }

    private Form[] clone(Form[] wellspring){
        Cloner cloner = new Cloner();
        return cloner.deepClone(wellspring);
    }

    private void printSettings(Settings settings) {
        System.out.print("           iterations " + settings.iterations + "\n");
        System.out.print("   removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("        findBySegment " + settings.findBySegment + "\n");
        System.out.print(" prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("     standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("        centerObjects " + settings.centerObjects + "\n");
        System.out.print("        vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("     avgVertexNormals " + settings.avgVertexNormals + "\n");
        System.out.println();
    }

    long startTime = time();
    long time() { return System.currentTimeMillis();}
    void runTime(){
        long m = ((time() - startTime) / 1000) / 60;
        long s = ((time() - startTime) / 1000) % 60;
        System.out.format("runtime %d.%dm ", m, s);
    }
}


