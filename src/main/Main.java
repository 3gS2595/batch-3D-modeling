package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import main.tools.SetUp;
import java.util.List;

public class Main {
    public static void main(String[] args){
        System.out.println("-utemporia");
        Main task = new Main();
        task.run(new Settings());
        task.runTime();
    }
    void run(Settings settings){
        List<Form[]> wells = SetUp.loadWells(settings);
        settings.groups = wells.size();
        Settings.printSettings(settings);
        settings.outputFilePath = ObjOutput.createOutputFolder(settings);

        double[] groupStep = {0,0,0};
        for(Form[] well : wells) {
            ThreadPool pool = new ThreadPool(settings.threadCnt);
            for (double i = 0; i < settings.iterations; i++) {
                Form.step(well);
                pool.initializeTask(well);
                pool.run();
                pool.pb0.setExtraMessage((i + 1) + "/" + settings.iterations);
            }
            pool.pb0.close();
            ObjOutput.output(pool.offspring, groupStep);
            groupStep[1] = groupStep[1] + 2;
        }
    }

    long startTime = time();
    long time() { return System.currentTimeMillis();}
    void runTime(){
        long m = ((time() - startTime) / 1000) / 60;
        long s = ((time() - startTime) / 1000) % 60;
        System.out.format("runtime %d.%dm ", m, s);
    }
}


