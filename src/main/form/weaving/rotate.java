package main.form.weaving;

import main.form.Form;

public class rotate {
	// ROTATE
	public static void rotate(Form form, double[] rotation) {
		if ( rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0) {
			double[][] tranM = new double[4][4];
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					tranM[i][j] = 0;

			tranM[0][0] = 1;
			tranM[1][1] = 1;
			tranM[2][2] = 1;
			tranM[3][3] = 1;
			if (rotation[0] > 0) {
				double theta = Math.toRadians(rotation[0]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[1][1] = cos;
				tranM[1][2] = -sin;
				tranM[2][1] = sin;
				tranM[2][2] = cos;
				executeRotate(form, tranM);
			}
			if (rotation[1] > 0) {
				double theta = Math.toRadians(rotation[1]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][0] = cos;
				tranM[0][2] = sin;
				tranM[2][0] = -sin;
				tranM[2][2] = cos;
				executeRotate(form, tranM);
			}
			if (rotation[2] > 0) {
				double theta = Math.toRadians(rotation[2]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][1] = cos;
				tranM[0][1] = -sin;
				tranM[1][0] = sin;
				tranM[1][1] = cos;
				executeRotate(form, tranM);
			}
		}
	}

	private static void executeRotate(Form form, double[][] tranM){
		for (int i = 0; i < form.v.size(); i++) {
			double x = form.v.get(i)[0];
			double y = form.v.get(i)[1];
			double z = form.v.get(i)[2];
			double[] prime = new double[3];
			prime[0] = (x * tranM[0][0]) + (y * tranM[0][1]) + (z * tranM[0][2]) + (1 * tranM[0][3]);
			prime[1] = (x * tranM[1][0]) + (y * tranM[1][1]) + (z * tranM[1][2]) + (1 * tranM[1][3]);
			prime[2] = (x * tranM[2][0]) + (y * tranM[2][1]) + (z * tranM[2][2]) + (1 * tranM[2][3]);
			form.v.set(i, new double[]{prime[0], prime[1], prime[2]});
		}
		buildTree.buildTree(form);
	}
}
