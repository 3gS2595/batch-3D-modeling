package main;

import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;


public class Main {
	public static void main(String[] args) {
		Main task = new Main();
		Settings setting = new Settings();
		task.run(setting);
		task.runTime();
	}

	void run(Settings settings) {
		ThreadPool pool = new ThreadPool(settings);
		runSingl(pool);
		runGroup(pool);
		pool.writeInfoFile();
	}

	void runSingl(ThreadPool pool) {
		if (pool.setting.decimate) {
			Settings.printSettingsSingle(pool.setting);

			// pb declare
			double vertCnt = 0;
			for (Form form : pool.setting.wellsprings) vertCnt += form.f.size();
			try (ProgressBar prodpb = new ProgressBar(" producing offspring" + 0, (long) vertCnt);
				 ProgressBar savepb = new ProgressBar("", pool.setting.wellsprings.size())) {
				prodpb.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

				// commands run
				for (Form form : pool.setting.wellsprings) {
					pool.initializeTaskSingle(form);
					System.out.println("_r" + " " + form.id);
					pool.run(prodpb);
				}
				// saving file
				pool.output.addAll(pool.setting.wellsprings);
				ObjOutput.output(pool, savepb, 0);
				pool.output.clear();
				pool.setting.groupStep[1] += 2;
			}
		}
	}

	double[] initialStep = new double[4];
	boolean reverseRun = false;
	void runGroup(ThreadPool pool) {
		initialStep = pool.setting.groupStep;

		if (pool.setting.nearestVertice || pool.setting.nearestSurface) {
			int runCnt = 1;
			pool.setting.group();
			Settings.printSettingsGroup(pool.setting);

			if(pool.setting.reversedRepeat && reverseRun){
				pool.setting.groupStep = initialStep;
				double[] ro = new double[]{10.0,180.0,10.0};
				for(int i =0; i < pool.setting.workingSet.size(); i++){
					System.out.println(pool.setting.workingSet.get(i)[1].v.get(5)[0]);
					pool.setting.workingSet.get(i)[1].rotate(ro);
					pool.setting.workingSet.get(i)[1].buildTree();
					System.out.println(pool.setting.workingSet.get(i)[1].v.get(5)[0]);
					System.out.println();

				}
			}

			// coupling iterate
			for (Form[] springPair : pool.setting.workingSet) {
				String filesUsed = "";
				for (String file : springPair[0].filesUsed) filesUsed = filesUsed.concat(", " + file);
				System.out.println("_r" + runCnt + " " + filesUsed);


				// pb declare
				double vertCnt = springPair[0].v.size() * springPair[0].settings.iterationCnt;
				try (ProgressBar prod = new ProgressBar(" " + runCnt + " producing offspring", (long) vertCnt);
					 ProgressBar save = new ProgressBar(" " + runCnt, pool.setting.iterationCnt)) {
					prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

					// commands run
					for (double i = 0; i < pool.setting.iterationCnt; i++) {
						Form.step(springPair);
						pool.initializeTaskGroup(springPair);
						pool.run(prod);
					}
					// saving file
					ObjOutput.output(pool, save, runCnt);
					pool.output.clear();
					pool.setting.groupStep[1] += 2;
					runCnt++;
				}
			}
		}
		if(pool.setting.reversedRepeat && !reverseRun){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reverseRun = true;
			runGroup(pool);
		}
	}

	long startTime = time();
	long time() {
		return System.currentTimeMillis();
	}
	void runTime() {
		long m = ((time() - startTime) / 1000) / 60;
		long s = ((time() - startTime) / 1000) % 60;
		System.out.format("runtime-%d.%dm", m, s);
	}
}









