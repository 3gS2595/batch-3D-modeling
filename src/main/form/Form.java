package main.form;

import main.form.weaving.*;
import main.Settings;
import org.tinspin.index.qthypercube2.QuadTreeKD2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static main.tools.ObjIntake.intake;

public class Form {

	// foundational object data
	public String id;
	public String ObjName;
	public String MtlName = null;
	public Settings settings;
	public QuadTreeKD2<Object> KdTree = QuadTreeKD2.create(3);
	public QuadTreeKD2<Object> newPointss = QuadTreeKD2.create(3);

	// file input
	public ArrayList<double[]>     v = new ArrayList<>();
	public List<double[]>    vn = new ArrayList<>();
	public List<String>    rawf = new ArrayList<>();
	public List<List<Integer>>f = new ArrayList<>();
	public List<List<Integer>>newf = new ArrayList<>();
	public List<Material>  mats = new ArrayList<>();

	// thread coordination
	public ConcurrentHashMap<Integer, double[]> newPoints  = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, double[]> usedPoints = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer[],Integer> newFaces   = new ConcurrentHashMap<>();

	// record keeping
	public HashMap<String, double[]> parentInfo = new HashMap<>();
	public double[] moved = {0,0,0,0,0,0}; // variable used during moving

	// normal averaging ;unless optioned untouched
	public List<double[]> 						  vNavg = new ArrayList<>();
	public HashMap<Integer,List<Integer>> siblingPoints = new HashMap<>();
	public HashMap<Integer,List<List<Integer>>> siblingPolys = new HashMap<>();


	// constructor----------------------------------
	public Form(String filepath, Settings settings){
		this.settings = settings;
		this.ObjName = filepath.replace("\\", "/");

		String[] nameSplit = ObjName.split("/");
		this.id = nameSplit[nameSplit.length-1];

		intake(this);
		System.out.println("STEOP" + this.settings.moveStep[0]);
		if (this.settings.centerObjects) centerMesh.centerMesh(this);
		if (this.settings.standardizeScale) standardizeScale.standardizeScale(this);
		standardizeSeparation.standardizeSeparation(this);
		buildTree.buildTree(this);
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
		this.KdTree = form.KdTree;
		this.siblingPolys = form.siblingPolys;
		System.arraycopy(form.settings.moveStep, 0, this.settings.moveStep, 0, form.settings.moveStep.length);
		for (Map.Entry<String, double[]> entry : form.parentInfo.entrySet()) {
			double[] arrayCopy = new double[entry.getValue().length];
			System.arraycopy(entry.getValue(), 0, arrayCopy, 0, entry.getValue().length);
			this.parentInfo.put(entry.getKey(), arrayCopy);
		}
	}
}