package main.form.weaving;

import main.form.Form;

public class standardizeSeparation {
	public static void standardizeSeparation(Form form){
		double maxX = form.v.get(1)[0];
		double maxZ = form.v.get(1)[2];

		double minX = form.v.get(1)[0];
		double minZ = form.v.get(1)[2];

		// loops xyz's
		for (double[] doubles : form.v) {
			double[] vert = doubles.clone();
			if (vert[0] > maxX) maxX = vert[0];
			if (vert[0] < minX) minX = vert[0];

			if (vert[2] < maxZ) maxZ = vert[2];
			if (vert[2] < minZ) minZ = vert[2];
		}

		double x = maxX - minX;
		double z = maxZ - minZ;

		form.settings.separateDistX = x+0.1;
		form.settings.separateDistZ = z+0.1;

		System.out.println(form.settings.separateDistX);
		System.out.println(form.settings.separateDistZ);
	}
}
