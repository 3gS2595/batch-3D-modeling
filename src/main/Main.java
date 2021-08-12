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

	void run(Settings settings) {
		ThreadPool pool = new ThreadPool(settings);
		runSingl(pool);
		runGroup(pool);
		pool.writeInfoFile();
	}

	void runSingl(ThreadPool pool) {
		if (pool.setting.decimate) {
			Settings.printSettings(pool.setting);

			// pb declare
			double vertCnt = 0;
			for (Form form : pool.setting.wellsprings) vertCnt += form.f.size();
			try (ProgressBar prod = new ProgressBar(" producing offspring" + 0, (long) vertCnt);
				 ProgressBar save = new ProgressBar("", pool.setting.wellsprings.size())) {
				prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

				// commands run
				for (Form form : pool.setting.wellsprings) {
					pool.initializeTaskSingle(form);
					System.out.println("_r" + " " + form.id);
					pool.run(prod);
				}
				// saving file
				pool.output.addAll(pool.setting.wellsprings);
				ObjOutput.output(pool, save, 0);
				pool.output.clear();
				//pool.setting.groupStep[1] += 2;
			}
		}
	}

	void runGroup(ThreadPool pool) {
		if (pool.setting.nearestVertice || pool.setting.nearestSurface) {
			int runCnt = 1;
			pool.setting.group();
			Settings.printSettings(pool.setting);

			// pairs iterate
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









