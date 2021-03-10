package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import main.tools.SetUp;
import java.util.List;

public class Main {
    public static void main(String[] args){
        Main task = new Main();
        task.run(new Settings());
        task.runTime();
    }
    void run(Settings settings){
        List<Form[]> groupings = SetUp.loadWells(settings);
        settings.groups = groupings.size();
        Settings.printSettings(settings);

        for(Form[] group : groupings) {
            ThreadPool pool = new ThreadPool(settings.threadCnt);
            for (double i = 0; i < settings.iterationCnt; i++) {
                Form.step(group);
                pool.initializeTask(group);
                pool.run();
                pool.pb0.setExtraMessage((i + 1) + "/" + settings.iterationCnt);
            }
            pool.pb0.close();
            ObjOutput.output(pool.offspring);
            settings.groupStep[1] = settings.groupStep[1] + 2;
        }
    }

    long startTime = time();
    long time() { return System.currentTimeMillis();}
    void runTime(){
        long m = ((time() - startTime) / 1000) / 60;
        long s = ((time() - startTime) / 1000) % 60;
        System.out.format("runtime-%d.%dm", m, s);
    }
}





