package main.form;

import main.Settings;
import org.tinspin.index.qthypercube2.QuadTreeKD2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import static main.tools.ObjIntake.intake;

public class Form {

	// foundational object data
	public String id;
	public String ObjName;
	public String MtlName = null;
	public Settings settings;
	public QuadTreeKD2<Object> KdTree = QuadTreeKD2.create(3);

	// file input
	public List<double[]>     v = new ArrayList<>();
	public List<double[]>    vn = new ArrayList<>();
	public List<String>    rawf = new ArrayList<>();
	public List<List<Integer>>f = new ArrayList<>();
	public List<Material>  mats = new ArrayList<>();

	// normal averaging ;unless optioned untouched
	public List<double[]> 						  vNavg = new ArrayList<>();
	public HashMap<Integer,List<Integer>> siblingPoints = new HashMap<>();
	public HashMap<Integer,List<List<Integer>>> siblingPolys = new HashMap<>();

	// thread coordination
	public ConcurrentHashMap<Integer, double[]> newPoints  = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, double[]> usedPoints = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer[],Integer> newFaces   = new ConcurrentHashMap<>();

	// record keeping
	public List<String>       filesUsed = new ArrayList<>();
	public double[] moved = {0,0,0,0,0,0,0}; // variable used during moving




	// constructor----------------------------------
	public Form(String filepath, Settings settings){
		this.settings = settings;
		this.ObjName = filepath.replace("\\", "/");

		String[] nameSplit = ObjName.split("/");
		this.id = nameSplit[nameSplit.length-1];

		intake(this);
		if (this.settings.centerObjects) this.centerObject();
		if (this.settings.standardizeScale) this.standardizeScale();

		this.buildTree();
		this.filesUsed.add(this.ObjName);
	}

	// used to make basis for offspring
	@SuppressWarnings("CopyConstructorMissesField")
	public Form(Form form) {
		this.id = form.id;
		this.ObjName = form.ObjName;
		this.MtlName = form.MtlName;
		this.siblingPoints = form.siblingPoints;
		this.settings = new Settings(form.settings);
		this.settings.moveStep = form.settings.moveStep;
		this.v = new ArrayList<>(form.v);
		this.vn = new ArrayList<>(form.vn);
		this.rawf = new ArrayList<>(form.rawf);
		this.f = new ArrayList<>(form.f);
		this.filesUsed = new ArrayList<>(form.filesUsed);
		this.KdTree = form.KdTree;
		this.siblingPolys = form.siblingPolys;
		this.moved = new double[form.moved.length];
		System.arraycopy(form.moved, 0, this.moved, 0, form.moved.length);
	}

	public void buildTree() {
		this.KdTree = QuadTreeKD2.create(3);
		for(int i = 0; i < this.v.size(); i++){
			this.KdTree.insert(new double[]{this.v.get(i)[0], this.v.get(i)[1], this.v.get(i)[2]}, i);
		}
	}

	// moves the form before each iteration
	public static void step(Form[] wellspring){
		double[] step = new double[]{
				wellspring[0].settings.moveStep[0],
				wellspring[0].settings.moveStep[1],
				wellspring[0].settings.moveStep[2]};
		wellspring[0].translate(step);
		wellspring[1].rotate(wellspring[1].settings.tempRotate);
		if(wellspring[0].settings.iterateRatio){
			wellspring[0].settings.ratio += wellspring[0].settings.moveStep[3];
		}
	}

	public void standardizeScale(){
		double[] max = this.v.get(1).clone();
		double[] min = this.v.get(1).clone();

		// loops xyz's
		for (double[] doubles : this.v) {
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
		for (int i = 0; i < this.v.size(); i++) {
			v.set(i, new double[]{this.v.get(i)[0] * scaleMult, this.v.get(i)[1] * scaleMult, this.v.get(i)[2] * scaleMult});
		}
	}

	public void centerObject() {
		double[] max = this.v.get(0).clone();
		double[] min = this.v.get(0).clone();
		for (double[] doubles : this.v) {
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
		for(int i = 0; i < this.v.size(); i ++) {
			double[] corrected = {this.v.get(i)[0] - center[0], this.v.get(i)[1] - center[1], this.v.get(i)[2] - center[2]};
			v.set(i, corrected);
		}
	}

	public void translate(double[] amount){
		if (!Arrays.equals(amount, new double[]{0, 0, 0})) {
			for (int i = 0; i < this.v.size(); i++) {
				double[] corrected = {this.v.get(i)[0] + amount[0], this.v.get(i)[1] + amount[1], this.v.get(i)[2] + amount[2]};
				v.set(i, corrected);
			}
		}
		this.moved[0] += amount[0];
		this.moved[1] += amount[1];
		this.moved[2] += amount[2];
	}

	// ROTATE
	public void rotate(double[] rotation) {
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
				executeRotate(tranM);
			}
			if (rotation[1] > 0) {
				double theta = Math.toRadians(rotation[1]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][0] = cos;
				tranM[0][2] = sin;
				tranM[2][0] = -sin;
				tranM[2][2] = cos;
				executeRotate(tranM);
			}
			if (rotation[2] > 0) {
				double theta = Math.toRadians(rotation[2]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][1] = cos;
				tranM[0][1] = -sin;
				tranM[1][0] = sin;
				tranM[1][1] = cos;
				executeRotate(tranM);
			}
			this.moved[3] += rotation[0];
			this.moved[4] += rotation[1];
			this.moved[5] += rotation[2];
		}
	}
	private void executeRotate(double[][] tranM){
		for (int i = 0; i < this.v.size(); i++) {
			double x = this.v.get(i)[0];
			double y = this.v.get(i)[1];
			double z = this.v.get(i)[2];
			double[] prime = new double[3];
			prime[0] = (x * tranM[0][0]) + (y * tranM[0][1]) + (z * tranM[0][2]) + (1 * tranM[0][3]);
			prime[1] = (x * tranM[1][0]) + (y * tranM[1][1]) + (z * tranM[1][2]) + (1 * tranM[1][3]);
			prime[2] = (x * tranM[2][0]) + (y * tranM[2][1]) + (z * tranM[2][2]) + (1 * tranM[2][3]);
			this.v.set(i, new double[]{prime[0], prime[1], prime[2]});
		}
	}
}