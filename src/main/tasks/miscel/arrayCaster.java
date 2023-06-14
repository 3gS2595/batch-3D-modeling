package main.tasks.miscel;

import main.form.Form;
import main.form.weaving.translate;
import main.pool.ThreadPool;
import main.tasks.Task;
import main.tools.ObjOutput;
import me.tongfei.progressbar.ProgressBar;

// organizes all forms into an array based orginization
public class arrayCaster implements Task {
	private final ThreadPool pool;
	private ProgressBar pb0;

	public arrayCaster(ThreadPool pool, ProgressBar pb0){
		this.pool = pool;
		this.pb0 = pb0;
		pool.setting.separateDistZ = 1.4;
		pool.setting.separateDistX = 1.4;
	}

	@Override
	public void run() {
		System.out.println("Collecting");
		int size = pool.setting.wellsprings.size();
		int xLen = (int) Math.sqrt(size);
		System.out.println("size: " + size);
		int prog = 0;
		int cnt = 0;
		int runCnt = 0;
		double[] trans = {0,0,0};

		while (prog < size){
			if(cnt > xLen || prog == size-1){
				cnt = 0;
				System.out.println("outputting size: " + pool.output.size());
				System.out.println("progress: " + prog);
				if(pool.output.size() != 0) {
					ObjOutput.output(pool, pb0, runCnt);
					runCnt++;
					pool.setting.groupStepBy[1] += pool.setting.separateDistZ;
				} else {
					System.exit(1);
				}
				pool.output.clear();
			} else {
				Form form = pool.setting.wellsprings.get(prog);
				translate.translate(form, trans);
				System.out.println(form.ObjName);
				pool.output.add(form);
				cnt++;
				prog++;
				trans[1]++;
			}
		}
	}
}
