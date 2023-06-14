package main.form.weaving;

import main.form.Form;

public class step {
	// modulates iterational lip
	public static void step(Form[] wellspring){
		double[] step = new double[]{
				wellspring[1].settings.moveStep[0],
				wellspring[1].settings.moveStep[1],
				wellspring[1].settings.moveStep[2]};
		double[] m0 = new double[wellspring[0].parentInfo.get(wellspring[0].ObjName).length];
		System.arraycopy(wellspring[0].parentInfo.get(wellspring[0].ObjName), 0, m0, 0,wellspring[0].parentInfo.get(wellspring[0].ObjName).length);

		double[] m1 = new double[wellspring[0].parentInfo.get(wellspring[1].ObjName).length];
		System.arraycopy(wellspring[0].parentInfo.get(wellspring[1].ObjName), 0, m1, 0,wellspring[0].parentInfo.get(wellspring[1].ObjName).length);

		translate.translate(wellspring[1], step);
		m1[0] += wellspring[0].settings.moveStep[0];
		m1[1] += wellspring[0].settings.moveStep[1];
		m1[2] += wellspring[0].settings.moveStep[2];
		System.out.println("AKSJHDKJASHD"+ wellspring[0].settings.moveStep[0]);
		System.out.println(wellspring[1].settings.moveStep[0]);

		rotate.rotate(wellspring[1], wellspring[1].settings.tempRotate);
		m1[3] += wellspring[0].settings.tempRotate[0];
		m1[4] += wellspring[0].settings.tempRotate[1];
		m1[5] += wellspring[0].settings.tempRotate[2];

		wellspring[0].parentInfo.replace(wellspring[0].ObjName, m0);
		wellspring[0].parentInfo.replace(wellspring[1].ObjName, m1);
		wellspring[1].parentInfo.replace(wellspring[0].ObjName, m0);
		wellspring[1].parentInfo.replace(wellspring[1].ObjName, m1);
//		System.out.println(wellspring[1].parentInfo.get(wellspring[1].ObjName)[4]);

		if(wellspring[0].settings.iterateRatio){
			wellspring[0].settings.ratio += wellspring[0].settings.moveStep[3];
		}
	}
}
