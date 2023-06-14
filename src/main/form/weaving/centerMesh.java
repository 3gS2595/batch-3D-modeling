package main.form.weaving;

import main.form.Form;

public class centerMesh {
	public static void centerMesh(Form form) {
		double[] max = form.v.get(0).clone();
		double[] min = form.v.get(0).clone();
		for (double[] doubles : form.v) {
			double[] vert = doubles.clone();
			// loops xyz
			for (int j = 0; j < 3; j++) {
				if (vert[j] > max[j]) {
					max[j] = vert[j];
				}
				if (vert[j] < min[j]) {
					min[j] = vert[j];
				}
			}
		}

		double[] center = {(max[0]+min[0])/2,(max[1]+min[1])/2,(max[2]+min[2])/2};
		for(int i = 0; i < form.v.size(); i ++) {
			double[] corrected = {form.v.get(i)[0] - center[0], form.v.get(i)[1] - center[1], form.v.get(i)[2] - center[2]};
			form.v.set(i, corrected);
		}
	}
}
