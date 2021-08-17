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

	int runCnt = 0;
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
				if(pool.setting.saveOutput) ObjOutput.output(pool, savepb, runCnt);
				pool.setting.groupStep[1] += pool.setting.separationDistanceY;
			}
		}
		runCnt++;
	}

	// reverse should output placing each correlating iteration with the other spaced as tuples etc
	boolean reverseRun = false;
	double[] move0 = new double[7];
	double[] move1 = new double[7];
	double initialStep = Double.MAX_VALUE;
	void runGroup(ThreadPool pool) {
		if (pool.setting.nearestVertice || pool.setting.nearestSurface) {
			pool.setting.group();
			if(!reverseRun) System.arraycopy(pool.setting.workingSet.get(0)[0].moved, 0, move0, 0, pool.setting.workingSet.get(0)[0].moved.length);
			if(!reverseRun) System.arraycopy(pool.setting.workingSet.get(0)[1].moved, 0, move1, 0, pool.setting.workingSet.get(0)[1].moved.length);
			Settings.printSettingsGroup(pool.setting);

			// configuring run REPEAT
			if(initialStep == Double.MAX_VALUE) initialStep = pool.setting.groupStep[1];
			if(pool.setting.reversedRepeat && reverseRun){
				pool.setting.groupStep[1] = initialStep;
				double[] ro = new double[]{0,0,180};
				for(int i =0; i < pool.setting.workingSet.size(); i++){
					pool.setting.workingSet.get(i)[1].rotate(ro);
					// separates repeat from first pass
					pool.setting.groupStep[0] +=
							pool.setting.separationDistanceX * pool.setting.iterationCnt
							+ (pool.setting.separationDistanceX / 2);
					pool.setting.workingSet.get(i)[0].moved = move0;
					pool.setting.workingSet.get(i)[1].moved = move1;
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
					if(pool.setting.iterateRatio) springPair[0].settings.ratio = 0;
					for (double i = 0; i < pool.setting.iterationCnt; i++) {
						Form.step(springPair);
						pool.initializeTaskGroup(springPair);
						pool.run(prod);
					}
					// saving file
					if(pool.setting.saveOutput) ObjOutput.output(pool, save, runCnt);
					pool.setting.groupStep[1] += pool.setting.separationDistanceY;
					runCnt++;
				}
			}
		}
		if(pool.setting.reversedRepeat && !reverseRun){
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









