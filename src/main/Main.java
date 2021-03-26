package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        Main task = new Main();
        task.run(new Settings());
        task.runTime();
    }
    void run(Settings settings){
        ThreadPool pool = new ThreadPool(settings);
        runSingl(pool);

        // fuck whatever happening this is
        System.out.println(ThreadPool.queue.size());
        ThreadPool.queue.clear();

        runGroup(pool);
    }

    void runSingl(ThreadPool pool){
        if (pool.setting.decimate) {
            int runCnt = 1;
            Settings.printSettings(pool.setting);
            // pb declare
            double vertCnt = 0;
            for (Form form : pool.setting.forms) vertCnt+=form.v.size();
            try (ProgressBar prod = new ProgressBar(" " + runCnt + " producing offspring", (long) vertCnt);
                 ProgressBar save = new ProgressBar(" " + runCnt, pool.setting.iterationCnt)) {
                prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

                for (Form form : pool.setting.forms) {
                    String filesUsed = "";
                    for (String file : form.filesUsed) filesUsed = filesUsed.concat(", " + file);
                    System.out.println("_r" + runCnt + " " + filesUsed);

                    pool.initializeTaskSingle(form);
                    runCnt++;
                }
                ObjOutput.output(pool.output, save, runCnt);
                pool.setting.forms = new ArrayList<>(pool.output);
                pool.output.clear();
                pool.setting.workingSet.clear();
                pool.setting.groupStep[1] = pool.setting.groupStep[1] + 2;
            }
        }
    }

    void runGroup(ThreadPool pool){
        int runCnt = 1;
        pool.setting.group();
        Settings.printSettings(pool.setting);

        // pair iterate
        for (Form[] group : pool.setting.workingSet) {
            String filesUsed = "";
            for (String file : group[0].filesUsed) filesUsed = filesUsed.concat(", " + file);
            System.out.println("_r" + runCnt + " " + filesUsed);

            // pb declare
            double vertCnt = group[0].v.size() * group[0].settings.iterationCnt;
            try (ProgressBar prod = new ProgressBar(" " + runCnt + " producing offspring", (long) vertCnt);
                 ProgressBar save = new ProgressBar(" " + runCnt, pool.setting.iterationCnt)) {
                prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

                // command run
                for (double i = 0; i < pool.setting.iterationCnt; i++) {
                    Form.step(group);
                    pool.initializeTaskGroup(group);
                    pool.run(prod);
                }
                ObjOutput.output(pool.output, save, runCnt);
                pool.output.clear();
                pool.setting.groupStep[1] = pool.setting.groupStep[1] + 2;
                runCnt++;
            }
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









