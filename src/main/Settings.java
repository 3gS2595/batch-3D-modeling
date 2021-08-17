package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {

    // KNOWN BUGS
        // decimate produces a handful of 0 points only on complex meshes

    // hardware
    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\input\\inhouse";
    public String          outputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\output";
    public String   outputFileNameNotes = "cold_moon_brights_in_the_sun";

    // routine
    public boolean             decimate = false;       // todo dysfunctional ?
    public boolean       nearestVertice = false;
    public boolean       nearestSurface = false;
                                        public int subDivRecursionLvl = 3;

    // render
    public double[]         maxDistance = {0.01,0,0};
    public double[]            rotation = {0,0,0};
    public double                 ratio = .4;
    public int             iterationCnt = 3;
    public boolean         iterateRatio = false;
    public boolean       reversedRepeat = false;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;

    // optionals
    public boolean manualParentSelection = false;
    public double    separationDistanceX = 0.7;
    public double    separationDistanceY = 1.5;
    public boolean            saveOutput = true;
    public boolean          un_spinForms = true;
    public int                threadCnt = 18;


    // dust bin
    public List<String>           files;
    public List<Form>       wellsprings;
    public List<Form[]>      workingSet = new ArrayList<>();
    public double[]          tempRotate = {0,0,0}; // variable used during moving might be worth it to test rotate
    public double[]            moveStep = {0,0,0,0}; // distance each iteration moves merge ratio@[3] ??
    public double[]           groupStep = {0,0,0};

    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public int                 groupCnt;

// Current failings
    public boolean   removeUsedVertices = false;    // broken
    public boolean prioritizeByDistance = false;    // broken

    public Settings(){
        optionSelection();
        SetUp setup = new SetUp(this);
        this.outputFolder = ObjOutput.createOutputFolder(this);
        this.wellsprings = setup.singles;
        this.files = setup.files;
        this.groupCnt = this.wellsprings.size();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Settings(Settings settings) {
        this.inputFolder = settings.inputFolder;
        this.outputFolder = settings.outputFolder;
        this.outputFileNameNotes = settings.outputFileNameNotes;
        this.decimate = settings.decimate;
        this.nearestVertice = settings.nearestVertice;
        this.nearestSurface = settings.nearestSurface;
        System.arraycopy(settings.maxDistance, 0, this.maxDistance, 0, settings.maxDistance.length);
        System.arraycopy(settings.rotation, 0, this.rotation, 0, settings.rotation.length);
        this.ratio = 0 + settings.ratio;
        this.iterationCnt = settings.iterationCnt;
        this.iterateRatio = settings.iterateRatio;
        this.reversedRepeat = settings.reversedRepeat;
        this.standardizeScale = settings.standardizeScale;
        this.centerObjects = settings.centerObjects;
        this.manualParentSelection = settings.manualParentSelection;
        this.separationDistanceX = settings.separationDistanceX;
        this.separationDistanceY = settings.separationDistanceY;
        this.saveOutput = settings.saveOutput;
        this.un_spinForms = settings.un_spinForms;
        this.threadCnt = settings.threadCnt;
        this.files = settings.files;
        this.wellsprings = settings.wellsprings;
        this.workingSet = settings.workingSet;
        this.tempRotate = settings.tempRotate;
        this.moveStep = settings.moveStep;
        this.groupStep = settings.groupStep;
        this.VertexNormals = settings.VertexNormals;
        this.avgVertexNormals = settings.avgVertexNormals;
        this.groupCnt = settings.groupCnt;
        this.removeUsedVertices = settings.removeUsedVertices;
        this.prioritizeByDistance = settings.prioritizeByDistance;
    }


    // could output single run into working set, and then use that as input to get (grouped) working set
    public void group(){
        SetUp setup = new SetUp();
        this.workingSet = setup.group(this);
        this.groupCnt = workingSet.size();
    }

    public static void printSettingsGroup(Settings settings) {
        System.out.print("\n\n");
        System.out.print("          sum of the run " + settings.iterationCnt * settings.groupCnt + "\n");
        System.out.print(" iterations per subgroup " + settings.iterationCnt + "\n");
        System.out.print("               subgroups " + settings.groupCnt + "\n");
        System.out.print("          nearestVertice " + settings.nearestVertice + "\n");
        System.out.print("          nearestSurface " + settings.nearestSurface + "\n");
        System.out.print("                   ratio " + settings.ratio + "\n");
        System.out.print("           iterate ratio " + settings.iterateRatio + "\n");
        System.out.print("                decimate " + settings.decimate + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("terminus " + settings.outputFolder + "\n");
        System.out.print("\n");
    }
    public static void printSettingsSingle(Settings settings) {
        System.out.print("\n\n");
        System.out.print("          sum of the run " + settings.wellsprings.size() + "\n");
        System.out.print(" iterations per subgroup " + "1" + "\n");
        System.out.print("               subgroups " + settings.groupCnt + "\n");
        System.out.print("          nearestVertice " + settings.nearestVertice + "\n");
        System.out.print("          nearestSurface " + settings.nearestSurface + "\n");
        System.out.print("                   ratio " + settings.ratio + "\n");
        System.out.print("           iterate ratio " + settings.iterateRatio + "\n");
        System.out.print("                decimate " + settings.decimate + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("terminus " + settings.outputFolder + "\n");
        System.out.print("\n");
    }


    void optionSelection() {
        System.out.println("[1] closest vertex");
        System.out.println("[2] closest surface point");
        System.out.println("[3] decimate");
        System.out.println("*");
        System.out.println("[4] iterate ratio");
        System.out.println("[5] reverse run");
        System.out.println("[6] manual wellsprings");
        System.out.println("*");
        System.out.println("[7] no saving");
        System.out.println("****");
        System.out.print("inputs:");

        Scanner keyboard = new Scanner(System.in);
        String selection = keyboard.nextLine();
        if(selection.contains("1")) this.nearestVertice = true;
        if(selection.contains("2")) this.nearestSurface = true;
        if(selection.contains("3")) this.decimate = true;
        if(selection.contains("4")) this.iterateRatio = true;
        if(selection.contains("5")) this.reversedRepeat = true;
        if(selection.contains("6")) this.manualParentSelection = true;
        if(selection.contains("7")) this.saveOutput = false;
        selection = selection.replaceAll("[0-9]","");
        selection = selection.replaceAll(" ", "_");
        selection = "_".concat(selection);
        outputFileNameNotes = "" + outputFileNameNotes.concat(selection);
    }
}












