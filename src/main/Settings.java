package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {

    // hardware
    public String           inputFolder = "/home/trinkner/Desktop/camp/Documents/hepheastus/input/test/";
    public String          outputFolder = "/home/trinkner/Documents/hepheastus/output/";
    public String   outputFileNameNotes = "staining_tongue";
    public int                threadCnt = 19;
    // routine
    public boolean             decimate = false;       // todo partly dysfunctional ?

    public boolean       nearestVertice = false;
    public boolean       nearestSurface = false; public int subDivRecursionLvl = 3;

    // render
    public double                 ratio = .5;
    public double[]         maxDistance = {0,0,0};
    public double[]            rotation = {0,360,0};
    public int             iterationCnt = 5;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;


// a farmers will ; till these flake well
    // TODO -----------------------------------------------------------
// PRIORITIES
    // verify and test decimate
    // support control over parent choice
    // instead of using thread pool as object have run use a special object to house the array

// TASK OPTIMIZATIONS/ TWEAKS
    // compute both objects as mother for standard runs

// STEP
    // allow min and max (start here, end there, rather than end there)
            // on rotational bounds ( will allow fine tuning runs post initial output)

// GENERAL
    // texture support
    // certify the middle iterations objects are both cleanly centered


// NEW TASKs
    // remove used with prioritizing by farthest distance first.
    // generate subdivisions to produce nasty polygon disbursement
    // standard but limit how many times a point can be used
    // center object and from the center pont shoot rays through the mother vertex and look
    // for father vertex in ray path, use this as the nn

    // rename single to wellspring and paris as couples or honestly pairSpring



























// dust bin
    public List<String>           files;
    public List<Form> wellsprings;
    public List<Form[]>      workingSet = new ArrayList<>();
    public double[]          tempRotate = {0,0,0}; // variable used during moving might be worth it to test rotate
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public double[]           groupStep = {0,0,0};
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public int                 groupCnt;
    public String        outputFilePath = ObjOutput.createOutputFolder(this);

// Current failings
    public boolean   removeUsedVertices = false;    // broken
    public boolean        findBySegment = false;
    public boolean prioritizeByDistance = false;    // broken

    public Settings(){
        optionSelection();
        SetUp setup = new SetUp(this);
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

    public static void printSettings(Settings settings) {
        System.out.print("\n\n");
        System.out.print("          sum of the run " + settings.iterationCnt * settings.groupCnt + "\n");
        System.out.print(" iterations per subgroup " + settings.iterationCnt + "\n");
        System.out.print("               subgroups " + settings.groupCnt + "\n");
        System.out.print("          nearestVertice " + settings.nearestVertice + "\n");
        System.out.print("          nearestSurface " + settings.nearestSurface + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("terminus " + settings.outputFilePath + "\n");
        System.out.print("\n");
    }


    void optionSelection() {
        System.out.println("[1] closest vertex");
        System.out.println("[2] closest surface point");
        System.out.println("[3] decimate");
        System.out.print("inputs:");

        Scanner keyboard = new Scanner(System.in);
        String selection = keyboard.next();
        if(selection.contains("1")) this.nearestVertice = true;
        if(selection.contains("2")) this.nearestSurface = true;
        if(selection.contains("3")) this.decimate = true;
    }
}












