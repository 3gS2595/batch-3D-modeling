package main;
import main.form.Form;
import main.tools.ObjOutput;
import main.tools.SetUp;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    // hardware
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\scans";
    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\unsorted\\digital\\hephaestus\\input\\small";
//    public String           inputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\heoheastus input\\sticks";
    public String          outputFolder = "C:\\Users\\lucoius\\Documents\\virtual_studio\\hepheastus\\hepheastus output";
    public String   outputFileNameNotes = "blazing_sun";
    public int                threadCnt = 18;
    // routine
    public boolean             standard = true;     // broken
    public boolean   removeUsedVertices = false;    // broken
    public boolean        findBySegment = false;
    public boolean prioritizeByDistance = false;    // broken
    public boolean             decimate = true;     // empty
    // render
    public double                 ratio = 0.5;
    public double[]         maxDistance = {0,0,0};    // iteration movement range
    public double[]            rotation = {0,0,0};    // iteration movement range
    public int             iterationCnt = 1;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;



// as farmers will ; till these flakes well
    // TODO -----------------------------------------------------------
    // generate subdivisions to produce nasty polygon disbursement

    // STEP BY RATIO

    // write your own progress bars

    // compute both objects as mother for standard runs
    // certify the middle iterations objects are both cleanly centered
    // separate as many tools from tasks as you can ,
    //      allowing access to a complex toolset when building new routines

    // texture support
    // merge code with desktop that supports control over parent choice
    // is it possible to allow a series of tasks to be executed all in the same output run

    // allow min  and max on rotational bounds

    // center object and from the center pont shoot rays through the mother vertex and look
    // for father vertex in ray path, use this as the nn

    // PRINT OUTPUT SUMMERY WHICH ROUTINES WERE RUN, TOTAL FORMS, TOTAL GROUPS, RUNTIME, LOCATION(LINK?)

    // ALLOW JOBS TO BE RUN IN SUCCESSION

    // de-rotate forms on completion











// dust bin
    public List<String>           files = new ArrayList<>();
    public List<Form>             forms = new ArrayList<>();
    public List<Form[]>      workingSet = new ArrayList<>();
    public double[]          tempRotate = {0,0,0}; // variable used during moving might be worth it to test rotate
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public double[]           groupStep = {0,0,0};
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public int                 groupCnt = 0;
    public String        outputFilePath = ObjOutput.createOutputFolder(this);

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












