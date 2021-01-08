package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.Intake.intake;

public class Model {

	public int id;
	public String OBJname;
	public String MTLname;
	public Main.Settings settings;

	public HashMap<Double, String> matUsed = new HashMap<>();
	public HashMap<Integer,List<Integer>> siblingPoints = new HashMap<>();
	public List<double[]>   vnavg = new ArrayList<>();
	public List<String>  rawPolys = new ArrayList<>();

	public List<double[]>       v = new ArrayList<>();
	public List<double[]>      vn = new ArrayList<>();
	public List<double[][]>     f = new ArrayList<>();
	public List<Material>    mats = new ArrayList<>();

	public Model(String filepath, int id, Main.Settings settings){
		this.settings = settings;
		this.OBJname = "./input/" + filepath;
		this.id = id;
		intake(this);
		if (this.settings.centerObjects) this.centerObject();
	}

	void centerObject() {
		double[] max = this.v.get(1).clone();
		double[] min = this.v.get(1).clone();
		if (this.settings.standardizeScale) {// scales model to a dimension canon of 1
			for(int i = 1; i < this.v.size(); i++){
				double[] vert = this.v.get(i).clone();

				// loops xyz
				for(int j = 0; j <= 2; j++){
					if (vert[j] > max[j]){ max[j] = vert[j];}
					if (vert[j] < min[j]){ min[j] = vert[j];}
				}

			}

			double num = Math.pow((max[0] - min[0]), 2)
					+ Math.pow((max[1] - min[1]), 2)
					+ Math.pow((max[2] - min[2]), 2);
			double distance = Math.sqrt(num);
			double scaleMult = 1 / distance;
			for (int i = 1; i < this.v.size(); i++) {
				double[] corrected = {this.v.get(i)[0] * scaleMult, this.v.get(i)[1] * scaleMult, this.v.get(i)[2] * scaleMult};
				v.set(i, corrected);
			}
		}

		max = this.v.get(1).clone();
		min = this.v.get(1).clone();
		for(int i = 1; i < this.v.size(); i ++){
			double[] vert = this.v.get(i).clone();
			// loops xyz
			for(int j = 0; j < 3; j++){
				if (vert[j] > max[j]){ max[j] = vert[j];}
				if (vert[j] < min[j]){ min[j] = vert[j];}
			}
		}
		double[] center = {(max[0]+min[0])/2,(max[1]+min[1])/2,(max[2]+min[2])/2};
		for(int i = 1; i < this.v.size(); i ++) {
			double[] corrected = {this.v.get(i)[0] - center[0], this.v.get(i)[1] - center[1], this.v.get(i)[2] - center[2]};
			v.set(i, corrected);
		}
	}

	void translate(){
		double[] center = this.settings.translate;
		if (this.settings.translate != new double[]{0,0,0}) {
			for (int i = 1; i < this.v.size(); i++) {
				double[] corrected = {this.v.get(i)[0] + center[0], this.v.get(i)[1] + center[1], this.v.get(i)[2] + center[2]};
				v.set(i, corrected);
			}
		}
	}

	void calculateNormals(){
		// uses the cross product of two edges
		// implemented as (v2 - v1) cross-product (v3 - v1)

		double[] wait = {0,0,0};
		this.vn.add(wait);
		int[] verts;
		for(String curPoly: this.rawPolys){
			String[] raw = curPoly.split(" ");
			int[] vertices;
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
					this.v.get(verts[1])[0] - this.v.get(verts[0])[0],
					this.v.get(verts[1])[1] - this.v.get(verts[0])[1],
					this.v.get(verts[1])[2] - this.v.get(verts[0])[2]};
			double [] v = { //A-B
					this.v.get(verts[2])[0] - this.v.get(verts[0])[0],
					this.v.get(verts[2])[1] - this.v.get(verts[0])[1],
					this.v.get(verts[2])[2] - this.v.get(verts[0])[2]};
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


}