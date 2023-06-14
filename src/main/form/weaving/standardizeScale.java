package main.form.weaving;

import main.form.Form;

public class standardizeScale {
	public standardizeScale(Form form) {
	}

	public static void standardizeScale(Form form){
		double[] max = form.v.get(1).clone();
		double[] min = form.v.get(1).clone();

		// loops xyz's
		for (double[] doubles : form.v) {
			double[] vert = doubles.clone();
			for (int j = 0; j <= 2; j++) {
				if (vert[j] > max[j]) {
					max[j] = vert[j];
				}
				if (vert[j] < min[j]) {
					min[j] = vert[j];
				}
			}
		}

		double num = Math.pow((max[0] - min[0]), 2) + Math.pow((max[1] - min[1]), 2) + Math.pow((max[2] - min[2]), 2);
		double distance = Math.sqrt(num);
		double scaleMult = 1 / distance;
		for (int i = 0; i < form.v.size(); i++) {
			form.v.set(i, new double[]{form.v.get(i)[0] * scaleMult, form.v.get(i)[1] * scaleMult, form.v.get(i)[2] * scaleMult});
		}
	}
}
