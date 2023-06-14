package main;

import main.form.Form;
import main.form.weaving.rotate;
import main.form.weaving.step;
import main.tasks.miscel.ImageCollect;
import main.tasks.miscel.arrayCaster;
import main.tasks.miscel.txtFileCollector;
import main.tasks.miscel.csv;
import main.tasks.miscel.gestaltClean;
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
		runMaintenance(pool);
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
				pool.setting.groupStepBy[1] += pool.setting.separateDistZ;
			}
			runCnt++;
		}
	}


	// reverse run logs
	boolean reverseRun = false;
	double initRatio;
	double[] moveStep = {0,0,0,0};
	ArrayList<double[]> moves = new ArrayList<>();
	double initialStep = Double.MAX_VALUE;

	void runGroup(ThreadPool pool) {
		if (pool.setting.nearestVertice || pool .setting.nearestSurface || pool.setting.nearestSurfaceProj) {
			pool.group();
			Settings.printSettingsGroup(pool.setting);

			// reverse run configuration
			if(!reverseRun && pool.workingSet.size() != 0) {
				initRatio = pool.setting.ratio;
				for(Form cur : pool.workingSet.get(0)){
					moves.add(new double[7]);
					moveStep = pool.setting.moveStep;
					System.arraycopy(cur.moved, 0, moves.get(moves.size()-1), 0, cur.moved.length);
					System.arraycopy(moveStep, 0, pool.setting.moveStep, 0, pool.setting.moveStep.length);
				}
			}
			if(initialStep == Double.MAX_VALUE) initialStep = pool.setting.groupStepBy[1];
			if(pool.setting.reversedRepeat && reverseRun){
				pool.setting.groupStepBy[1] = initialStep;
				double[] tempRotation = new double[]{0,180,0};
				for(int i =0; i < pool.workingSet.size(); i++){
					pool.setting.ratio = initRatio;
					moves.get(1)[4] += 180;
					rotate.rotate(pool.workingSet.get(i)[1], tempRotation);
					for(int x = 0; x < pool.workingSet.get(i).length; x ++)
						for(int m = 0; m < moves.size(); m++)
							pool.workingSet.get(i)[m].parentInfo.replace(pool.workingSet.get(i)[m].ObjName, moves.get(m));

					pool.setting.groupStepBy[0] =
							(pool.setting.separateDistX * pool.setting.iterationCnt) + (pool.setting.separateDistX / 2);
				}
			}


			// coupling iterate
			for (Form[] springPair : pool.workingSet) {
				// todo why is this be like this do be though
				if(reverseRun){
					springPair[0].settings.moveStep = moveStep;
					springPair[1].settings.moveStep = moveStep;
				}
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
						for(double x : springPair[0].settings.moveStep){
							System.out.println(x);
						}
						System.out.println();
						step.step(springPair);
						pool.initializeTaskGroup(springPair);
						pool.run(prod);
					}
					// saving file
					System.out.println();
					if(pool.setting.saveOutput) ObjOutput.output(pool, save, runCnt);
					pool.setting.groupStepBy[1] += pool.setting.separateDistZ;
					runCnt++;
				}
			}
		}
		pool.output.clear();
		if(pool.setting.reversedRepeat && !reverseRun){
			reverseRun = true;
			runGroup(pool);
		}
	}


	void runMaintenance(ThreadPool pool){
		if (pool.setting.imageCollect) {
			ImageCollect imgCollect = new ImageCollect();
			imgCollect.run();
		}
		if (pool.setting.txtCollect) {
			txtFileCollector txtCollect = new txtFileCollector();
			txtCollect.run();
		}
		if (pool.setting.csv) {
			csv csv = new csv();
			csv.run();
		}
		if (pool.setting.gestaltClean) {
			gestaltClean gc = new gestaltClean();
			gc.run();
		}
		if (pool.setting.arrayCast) {
			try (ProgressBar pb0 = new ProgressBar(" " + runCnt + "casting array", pool.workingSet.size())) {
				arrayCaster arrayCast = new arrayCaster(pool, pb0);
				arrayCast.run();
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





















