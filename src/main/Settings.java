package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {

    // repeat after turning form 180d
    // KNOWN BUGS
        // infoFile move records remain null
        // decimate produces a handful of 0 points only on complex meshes
        // MAJOR rotational iteration changes nothing in form

    // hardware
    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\input\\sticks1";
    public String          outputFolder = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\vein\\output";
//    public String   outputFileNameNotes = "a_cold_moon_shines_brightest_in_the_sun";
    public String   outputFileNameNotes = "stained";
    public int                threadCnt = 18;
    // routine
    public boolean             decimate = false;       // todo dysfunctional ?
    public boolean       nearestVertice = false;
    public boolean       nearestSurface = false;
                                        public int subDivRecursionLvl = 3;

    // render
    public double[]         maxDistance = {0,0,0};
    public double[]            rotation = {0,360,0};
    public double                 ratio = .5;
    public int             iterationCnt = 5;
    public boolean         iterateRatio = false;
    public boolean       reversedRepeat = true;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;


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
//        System.out.println("[3] resink");
        System.out.println("[3] decimate");
        System.out.print("select:");

        Scanner keyboard = new Scanner(System.in);
        String selection = keyboard.nextLine();
        if(selection.contains("1")) this.nearestVertice = true;
        if(selection.contains("2")) this.nearestSurface = true;
        if(selection.contains("3")) this.decimate = true;
        selection = selection.replaceAll("[0-9]","");
        selection = selection.replaceAll(" ", "_");
        selection = "_".concat(selection);
        outputFileNameNotes = "" + outputFileNameNotes.concat(selection);

    }
}












