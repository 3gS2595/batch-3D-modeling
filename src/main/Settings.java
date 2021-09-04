package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {

    //TODO write move and rotation for both parents in infoFile.
    // place reverse run iteration next to their counterparts
    // an array of ratio values to run for each correlating iteration
    // nomenclature based on current population of the output folder
    //      finds what numbers have been used and adds to the last ( or fills gaps)
    // KNOWN BUGS
        // decimate produces a handful of 0 points only on complex meshes
        // decimate seems to be  fully broken

    // hardware
        public String           inputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\input\\sticks";
    public String          outputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\output";
    public String   outputFileNameNotes = "cold_moon_brights_in_the_sun";

    // routine
    public boolean             decimate = false;       // todo dysfunctional ?
    public boolean       nearestVertice = false;
    public boolean   nearestSurfaceProj = false;
    public boolean       nearestSurface = false;
                                        public int subDivRecursionLvl = 3;

    // render
    public double[]         maxDistance = {0,0,0};
    public double[]            rotation = {0,360,0};
    public double                 ratio = .2;
    public double              minRatio = .1;
    public int             iterationCnt = 5;
    public boolean         iterateRatio = false;
    public boolean       reversedRepeat = false;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;

    // optionals
    public boolean manualParentSelection = false;
    public double    separationDistanceX = 1;
    public double    separationDistanceY = 1.2;
    public boolean            saveOutput = true;
    public boolean          un_spinForms = true;
    public int                threadCnt = 18;











    // produce family tree charts automatically in info file ? could make sure i switch
    // //information oer during exports and build info within each obj during generation

    // pairs should be done through a list string arrays correlating to spring ID
    // splice a new spring and take what you need from any number of forms
    // us this in file selection
    //   scan all of a database folder
    //   select mother, than select father from list
    // live update on web gui
    //   refreshes file saves and code changes/re compiles

    // dust bin
    public List<String>           files;
    public List<Form>       wellsprings;
    public double[]          tempRotate = {0,0,0};   // variable used during moving might be worth it to test rotate
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
        this.groupCnt = this.wellsprings.size();
        this.moveStep = new double[]{
                0,
                0,
                0,
                ((this.ratio - this.minRatio) / this.iterationCnt)};
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
        this.ratio = 0+ settings.ratio;
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
        this.tempRotate = settings.tempRotate;
        this.moveStep = settings.moveStep;
        this.groupStep = settings.groupStep;
        this.VertexNormals = settings.VertexNormals;
        this.avgVertexNormals = settings.avgVertexNormals;
        this.groupCnt = settings.groupCnt;
        this.removeUsedVertices = settings.removeUsedVertices;
        this.prioritizeByDistance = settings.prioritizeByDistance;
        this.nearestSurfaceProj = settings.nearestSurfaceProj;
    }

    public static void printSettingsGroup(Settings settings) {
        System.out.print("\n\n");
        System.out.print("          sum of the run " + settings.iterationCnt * settings.groupCnt + "\n");
        System.out.print(" iterations per subgroup " + settings.iterationCnt + "\n");
        if(settings.iterateRatio){
            double step = (settings.ratio - settings.minRatio) / settings.iterationCnt;
            System.out.print("                   ratios\n");
            for(int i = 0; i < settings.iterationCnt; i++){
                String num = String.format("%.4g%n", settings.minRatio + (settings.moveStep[3] * (i+1)));
                System.out.print("                      i"+ i + " " + num + "");
            }
        } else {
            System.out.print("                   ratio " + settings.minRatio + "\n");
        }
        System.out.print("                decimate " + settings.decimate + "\n");
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
        System.out.print("                   ratio " + settings.minRatio + "\n");
        System.out.print("                         " + settings.ratio + "\n");
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
        System.out.println("[8] closest surface point");
        System.out.println("[3] decimate");
        System.out.println("*");
        System.out.println("[4] iterate ratio");
        System.out.println("[5] reverse run");
        System.out.println("[6] manual wellsprings");
        System.out.println("[9] primary well designation");
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
        if(selection.contains("8")) this.nearestSurfaceProj = true;
        selection = selection.replaceAll("[0-9]","");
        selection = selection.replaceAll(" ", "_");
        selection = "__".concat(selection);
        outputFileNameNotes = "" + outputFileNameNotes.concat(selection);
    }
}












