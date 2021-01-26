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
        settings.groups = wells.size();
        settings.outputFilePath = ObjOutput.createOutputFolder(settings);
        Settings.printSettings(settings);

        double[] groupStep = {0,0,0};
        for(Form[] well : wells) {
            ThreadPool pool = new ThreadPool(settings.threadCnt);
            List<Form> offspring = new ArrayList<>();
            for (double i = 0; i < settings.iterations; i++) {
                Form.step(well);
                pool.initializeTask(clone(well));
                pool.run();
                pool.pb0.setExtraMessage((i + 1) + "/" + settings.iterations);
                offspring.add(pool.ret());
            }
            pool.pb0.close();
            new ObjOutput(offspring, groupStep);
            groupStep[1] = groupStep[1] + 2;
        }
        System.out.println(settings.outputFilePath);
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


