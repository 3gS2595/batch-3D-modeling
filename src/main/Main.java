package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;


import java.util.*;

public class Main {
    public static void main(String[] args) {
        Main task = new Main();
        Settings setting = new Settings();
        task.run(setting);
        task.runTime();
    }
    void run(Settings settings){
        ThreadPool pool = new ThreadPool(settings);
        runSingl(pool);
        runGroup(pool);
        pool.writeInfoFile();
    }

    void runSingl(ThreadPool pool){
        if (pool.setting.decimate) {
            int runCnt = 1;
            Settings.printSettings(pool.setting);
            // pb declare
            double vertCnt = 0;
            for (Form form : pool.setting.forms) vertCnt+=form.f.size();
            try (ProgressBar prod = new ProgressBar(" producing offspring"+ runCnt, (long) vertCnt);
                 ProgressBar save = new ProgressBar("", pool.setting.forms.size())) {
                prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

                for (Form form : pool.setting.forms) {
                    String filesUsed = "";
                    for (String file : form.filesUsed) filesUsed = filesUsed.concat(", " + file);
                    pool.initializeTaskSingle(form);
                    System.out.println("_r" + runCnt + " " + filesUsed);
                    pool.run(prod);
                    runCnt++;
                }
                System.out.println("\nmigrating");
                for(Form cur : pool.output){
                    cur.v.clear();
                    SortedSet<Integer> keys = new TreeSet<>(cur.newV.keySet());
                    cur.v = new ArrayList<>(keys.size());
                    for(Integer key : keys){
                        cur.v.add(key, cur.newV.get(key));
                    }
                    cur.f.clear();
                    for(Integer[] key : cur.newF.keySet()){
                        List<Integer> intList = new ArrayList<Integer>(key.length);
                        intList.addAll(Arrays.asList(key));
                        cur.f.add(intList);
                    }
                }
                ObjOutput.output(pool, save, runCnt);
                pool.setting.forms = new ArrayList<>(pool.output);
                pool.output.clear();
                pool.setting.workingSet.clear();
                pool.setting.groupStep[1] = pool.setting.groupStep[1] + 2;
                System.out.println("finished migrating");
            }
            System.out.println("SINGLE pass complete");
        }
    }

    void runGroup(ThreadPool pool){
        if(pool.setting.nearestVertice || pool.setting.nearestSurface) {
            int runCnt = 1;
            pool.setting.group();
            Settings.printSettings(pool.setting);

            // pair iterate
            for (Form[] group : pool.setting.workingSet) {
                String filesUsed = "";
                for (String file : group[0].filesUsed) filesUsed = filesUsed.concat(", " + file);
                System.out.println("_r" + runCnt + " " + filesUsed);
//                for (Form cur : group){
//                    cur.buildTree();
//                }

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
                    ObjOutput.output(pool, save, runCnt);
                    pool.output.clear();
                    pool.setting.groupStep[1] = pool.setting.groupStep[1] + 2;
                    runCnt++;
                }
            }
            System.out.println("GROUP pass complete");
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









