package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;

public class Settings {


    // hardware
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\cube";
    public String           inputFolder = "C:\\Users\\lucoius\\Pictures\\photogrammetry\\unsorted\\testrun";
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\sticks1";
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\antenna&fence\\f0-fresh";
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\bench5\\2";


    public String          outputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\hepheastus output\\";
    public String   outputFileNameNotes = "staining_tongue";
    public int                threadCnt = 16;
    // routine
    public boolean             standard = true;
    public boolean             decimate = false;       // partly dysfunctional ?
    // render
    public double                 ratio = 0.5;
    public double[]         maxDistance = {0,0,0};    // iteration movement range
    public double[]            rotation = {0,350,0};  // iteration movement range
    public int             iterationCnt = 5;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;
    public boolean       outputUsedObjs = false;      // allows fine-tuning if need be



// as farmers will ; till these flakes well
    // TODO -----------------------------------------------------------
// PRIORITIES
    // fix decimate run pre-print summery
    // parametrically work with tri/quads shouldn't be hard
    // de-rotate forms on completion (as additional option)
    // support control over parent choice
    // ability to recreate placement in blender to use in hopes of raising final quality

// TASK OPTIMIZATIONS/ TWEAKS
    // support control over parent choice
    // compute both objects as mother for standard runs

// NEW TASKS
    // remove used with prioritizing by farthest distance first.
    // generate subdivisions to produce nasty polygon disbursement
    // standard but limit how many times a point can be used
    // center object and from the center pont shoot rays through the mother vertex and look
            // for father vertex in ray path, use this as the nn

// STEP
    // allow min and max (start here, end there, rather than end there)
            // on rotational bounds ( will allow fine tuning runs post initial output)

// GENERAL
    // texture support
    // certify the middle iterations objects are both cleanly centered
    // write your own progress bars

//OUTPUT
    // de-rotate forms on completion (as additional option)
    // output by all permutations of mother,
            // may overlap and double produce in entirety but will be easier to process







// dust bin
    public List<String>           files;
    public List<Form>             forms;
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
        SetUp setup = new SetUp(this);
        this.forms = setup.singles;
        this.files = setup.files;
        this.groupCnt = this.forms.size();
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
        System.out.print("                standard " + settings.standard + "\n");
        System.out.print("      removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("           findBySegment " + settings.findBySegment + "\n");
        System.out.print("    prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("               singleOut " + settings.decimate + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("terminus " + settings.outputFilePath + "\n");
        System.out.print("\n");
    }
}












