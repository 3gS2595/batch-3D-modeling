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

	// normal averaging
	public List<double[]>                         vnavg = new ArrayList<>();
	public HashMap<Integer,List<Integer>> siblingPoints = new HashMap<>();

	// thread coordination
	public ConcurrentHashMap<Integer, double[]> newV = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer[], Integer> newF = new ConcurrentHashMap<>();

	public ConcurrentHashMap<Integer, double[]> newPoints       = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, double[]> usedPoints      = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, double[]> newSubdivisions = new ConcurrentHashMap<>();

	// form sectioning
	public List<List<double[]>> section = new ArrayList<>();
	public List<List<double[]>> minMax  = new ArrayList<>();

	// record keeping
	public List<String>       filesUsed = new ArrayList<>();
	public double[] moved = {0,0,0,0,0,0}; // variable used during moving






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
    public Form(Form form) {
		this.id = form.id;
		this.ObjName = form.ObjName;
		this.MtlName = form.MtlName;
		this.siblingPoints = form.siblingPoints;
		this.settings = form.settings;
		this.v = new ArrayList<>(form.v);
		this.vn = new ArrayList<>(form.vn);
		this.rawf = new ArrayList<>(form.rawf);
		this.f = new ArrayList<>(form.f);
		this.filesUsed = new ArrayList<>(form.filesUsed);
		this.buildTree();
	}

    public void buildTree() {
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
		wellspring[0].rotate();
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
			double[] corrected = {this.v.get(i)[0] * scaleMult, this.v.get(i)[1] * scaleMult, this.v.get(i)[2] * scaleMult};
			v.set(i, corrected);
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
	public void rotate() {
		if ( this.settings.tempRotate[0] != 0 || this.settings.tempRotate[1] != 0 || this.settings.tempRotate[2] != 0) {
			double[][] tranM = new double[4][4];
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					tranM[i][j] = 0;

			tranM[0][0] = 1;
			tranM[1][1] = 1;
			tranM[2][2] = 1;
			tranM[3][3] = 1;
			if (this.settings.tempRotate[0] > 0) {
				double theta = Math.toRadians(this.settings.tempRotate[0]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[1][1] = cos;
				tranM[1][2] = -sin;
				tranM[2][1] = sin;
				tranM[2][2] = cos;
				executeRotate(tranM);
			}
			if (this.settings.tempRotate[1] > 0) {
				double theta = Math.toRadians(this.settings.tempRotate[1]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][0] = cos;
				tranM[0][2] = sin;
				tranM[2][0] = -sin;
				tranM[2][2] = cos;
				executeRotate(tranM);
			}
			if (this.settings.tempRotate[2] > 0) {
				double theta = Math.toRadians(this.settings.tempRotate[2]);
				double cos = Math.cos(theta);
				double sin = Math.sin(theta);
				tranM[0][1] = cos;
				tranM[0][1] = -sin;
				tranM[1][0] = sin;
				tranM[1][1] = cos;
				executeRotate(tranM);
			}
			this.moved[3] += this.settings.tempRotate[0];
			this.moved[4] += this.settings.tempRotate[1];
			this.moved[5] += this.settings.tempRotate[2];
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

	public void calculateNormals(){
		// uses the cross product of two edges
		// implemented as (v2 - v1) cross-product (v3 - v1)

		double[] wait = {0,0,0};
		this.vn.add(wait);
		int[] verts;
		for(String curPoly: this.rawf){
			String[] raw = curPoly.split(" ");
			curPoly = curPoly.substring(1).replace(" ","");
			if(curPoly.contains("//")){
				verts = new int[]{Integer.parseInt(raw[1].split("//")[0]),
						Integer.parseInt(raw[2].split("//")[0]),
						Integer.parseInt(raw[3].split("//")[0])};
			} else {
				verts = new int[]{Integer.parseInt(raw[1].split("/")[0]),
						Integer.parseInt(raw[2].split("/")[0]),
						Integer.parseInt(raw[3].split("/")[0])};
			}

			double [] u = { //B-A
					this.v.get(verts[1]-1)[0] - this.v.get(verts[0]-1)[0],
					this.v.get(verts[1]-1)[1] - this.v.get(verts[0]-1)[1],
					this.v.get(verts[1]-1)[2] - this.v.get(verts[0]-1)[2]};
			double [] v = { //A-B
					this.v.get(verts[2]-1)[0] - this.v.get(verts[0]-1)[0],
					this.v.get(verts[2]-1)[1] - this.v.get(verts[0]-1)[1],
					this.v.get(verts[2]-1)[2] - this.v.get(verts[0]-1)[2]};
			double[] vn = {
					(u[1] * v[2]) - (u[2] * v[1]),
					(u[2] * v[0]) - (u[0] * v[2]),
					(u[1] * v[1]) - (u[1] * v[0])
			};

			double length = Math.sqrt(vn[0] * vn[0] + vn[1] * vn[1] + vn[2] * vn[2]);
			double[] vnNormal = {vn[0]/length, vn[1]/length, vn[2]/length};
			this.vn.add(vnNormal);
		}

		// averages
		if(this.settings.avgVertexNormals) {
			this.vnavg = new ArrayList<>(this.vn);
			for (Integer key : this.siblingPoints.keySet()) {
				List<Integer> t = this.siblingPoints.get(key);
				int cnt = 0;
				double[] build = {0, 0, 0};
				for (Integer integer : t) {
					build[0] += this.vn.get(integer)[0];
					build[1] += this.vn.get(integer)[1];
					build[2] += this.vn.get(integer)[2];
					cnt++;
				}
				build[0] = build[0] / cnt;
				build[1] = build[1] / cnt;
				build[2] = build[2] / cnt;

				double length = Math.sqrt(build[0] * build[0] + build[1] * build[1] + build[2] * build[2]);
				double[] vnNormal = {build[0] / length, build[1] / length, build[2] / length};
				this.vnavg.set(key, vnNormal);
			}

			this.vn = new ArrayList<>(this.vnavg);
		}
	}

	public void split(){
		double[] max = this.v.get(1).clone();
		double[] min = this.v.get(1).clone();

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
		double[] haf = {min[0] + (max[0] - min[0])/2, min[1] + (max[1] - min[1])/2, min[2] + (max[2] - min[2])/2};
		List<double[]> minXyz = new ArrayList<>();
		List<double[]> maxXyz = new ArrayList<>();
		minXyz.add(new double[]{min[0], min[1], min[2]});
		maxXyz.add(new double[]{haf[0], haf[1], haf[2]});

		minXyz.add(new double[]{min[0], min[1], haf[2]});
		maxXyz.add(new double[]{haf[0], haf[1], max[2]});

		minXyz.add(new double[]{min[0], haf[1], haf[2]});
		maxXyz.add(new double[]{haf[0], max[1], max[2]});

		minXyz.add(new double[]{haf[0], haf[1], haf[2]});
		maxXyz.add(new double[]{max[0], max[1], max[2]});

		minXyz.add(new double[]{haf[0], min[1], min[2]});
		maxXyz.add(new double[]{max[0], haf[1], haf[2]});

		minXyz.add(new double[]{min[0], haf[1], min[2]});
		maxXyz.add(new double[]{haf[0], max[1], haf[2]});

		minXyz.add(new double[]{haf[0], min[1], haf[2]});
		maxXyz.add(new double[]{max[0], haf[1], max[2]});

		minXyz.add(new double[]{haf[0], haf[1], min[2]});
		maxXyz.add(new double[]{max[0], max[1], haf[2]});

		minMax.add(minXyz);
		minMax.add(maxXyz);
		for (int j = 0; j < 8; j++) {
			this.section.add(new ArrayList<>());
		}
		for (double[] point : this.v) {
			for (int j = 0; j < 8; j++) {
				if (
						minXyz.get(j)[0] <= point[0] && point[0] <= maxXyz.get(j)[0] &&
								minXyz.get(j)[1] <= point[1] && point[1] <= maxXyz.get(j)[1] &&
								minXyz.get(j)[2] <= point[2] && point[2] <= maxXyz.get(j)[2]
				) {
					this.section.get(j).add(point);
				}
			}
		}
	}
}