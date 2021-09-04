package main;

import main.form.Form;
import main.tools.ObjOutput;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class Main {
	public static void main(String[] args) throws FileNotFoundException {

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

	// reverse run logs
	boolean reverseRun = false;
	double initRatio;
	ArrayList<double[]> moves = new ArrayList<>();
	double initialStep = Double.MAX_VALUE;

	void runGroup(ThreadPool pool) {
		if (pool.setting.nearestVertice || pool.setting.nearestSurface || pool.setting.nearestSurfaceProj) {
			pool.group();
			Settings.printSettingsGroup(pool.setting);

			// reverse run configuration
			if(!reverseRun && pool.workingSet.size() != 0) {
				initRatio = pool.setting.ratio;
				for(Form cur : pool.workingSet.get(0)){
					moves.add(new double[7]);
					System.arraycopy(cur.moved, 0, moves.get(moves.size()-1), 0, cur.moved.length);
				}
			}
			if(initialStep == Double.MAX_VALUE) initialStep = pool.setting.groupStep[1];
			if(pool.setting.reversedRepeat && reverseRun){
				pool.setting.groupStep[1] = initialStep;
				double[] ro = new double[]{0,180,0};
				for(int i =0; i < pool.workingSet.size(); i++){
					pool.setting.ratio = initRatio;
					moves.get(1)[4] += 180;
					pool.workingSet.get(i)[1].rotate(ro);
					for(int x = 0; x < pool.workingSet.get(i).length; x ++)
						for(int m = 0; m < moves.size(); m++)
							pool.workingSet.get(i)[m].parentInfo.replace(pool.workingSet.get(i)[m].ObjName, moves.get(m));

					pool.setting.groupStep[0] =
							(pool.setting.separationDistanceX * pool.setting.iterationCnt)
							+
							(pool.setting.separationDistanceX / 2);
				}

			}

			// coupling iterate
			for (Form[] springPair : pool.workingSet) {

				String filesUsed = "";
				for (String file : springPair[0].parentInfo.keySet()) {
					String[] temp = file.split("/");
					filesUsed = filesUsed.concat(", " + temp[temp.length-1]);
				}
				System.out.println("_r" + runCnt + " " + filesUsed);

				// pb declare
				double vertCnt = springPair[0].v.size() * springPair[0].settings.iterationCnt;
				try (ProgressBar prod = new ProgressBar(" " + runCnt + " producing offspring", (long) vertCnt);
					 ProgressBar save = new ProgressBar(" " + runCnt, pool.setting.iterationCnt)) {
					prod.setExtraMessage((1) + "/" + pool.setting.iterationCnt);

					// commands run
					if(pool.setting.iterateRatio) springPair[0].settings.ratio = springPair[0].settings.minRatio;
					for (double i = 0; i < pool.setting.iterationCnt; i++) {
						Form.step(springPair);
						pool.initializeTaskGroup(springPair);
						pool.run(prod);
					}
					// saving file
					System.out.println();
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





















