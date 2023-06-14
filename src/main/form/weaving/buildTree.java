package main.form.weaving;

import main.form.Form;
import org.tinspin.index.qthypercube2.QuadTreeKD2;

public class buildTree {
	public static void buildTree(Form form) {
		form.KdTree.clear();
		form.KdTree = QuadTreeKD2.create(3);
		for(int i = 0; i < form.v.size(); i++){
			form.KdTree.insert(new double[]{form.v.get(i)[0], form.v.get(i)[1], form.v.get(i)[2]}, i);
		}
	}
}
