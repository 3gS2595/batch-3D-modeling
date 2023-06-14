package main.form.weaving;

import main.form.Form;

import java.util.Arrays;

public class translate {
	public static void translate(Form form, double[] amount){
		if (!Arrays.equals(amount, new double[]{0, 0, 0})) {
			for (int i = 0; i < form.v.size(); i++) {
				double[] corrected = {form.v.get(i)[0] + amount[0], form.v.get(i)[1] + amount[1], form.v.get(i)[2] + amount[2]};
				form.v.set(i, corrected);
			}
		}
		buildTree.buildTree(form);
	}
}
