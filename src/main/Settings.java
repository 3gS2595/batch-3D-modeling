package main;
import main.tools.ObjOutput;

public class Settings {

    // hardware
    public String           inputFolder = "C:\\Users\\dbtri\\Documents\\studio\\digital\\hephaestus\\intake\\b";
    public String          outputFolder = "C:\\Users\\dbtri\\IdeaProjects\\hephaestus-\\out";
    public String   outputFileNameNotes = "_blazing_sun";
    public int                threadCnt = 3;
    // routine
    public boolean   removeUsedVertices = false;    // broken
    public boolean        findBySegment = false;
    public boolean prioritizeByDistance = false;    // broken
    public boolean            singleOut = false;    // empty
    // render
    public double                 ratio = 0.5;
    public double[]         maxDistance = {0,0,0};    // iteration movement range
    public double[]            rotation = {0,0,0};    // iteration movement range
    public int             iterationCnt = 1;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;

// as farmers will ; till these flakes well
    // TODO -----------------------------------------------------------
    // generate subdivisions to produce nasty polygon dispersment
    
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











// dust bin
    public double[]          tempRotate = {0,0,0}; // variable used during moving might be worth it to test rotate
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public double[]           groupStep = {0,0,0};
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public String                 file0 = "";
    public String                 file1 = "";
    public int                   groups = 0;
    public String        outputFilePath = ObjOutput.createOutputFolder(this);

    public static void printSettings(Settings settings) {
        System.out.print("\n\n");
        System.out.print("          sum of the run " + settings.iterationCnt * settings.groups + "\n");
        System.out.print(" iterations per subgroup " + settings.iterationCnt + "\n");
        System.out.print("               subgroups " + settings.groups + "\n");
        System.out.print("      removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("           findBySegment " + settings.findBySegment + "\n");
        System.out.print("    prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("               singleOut " + settings.singleOut + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("terminus " + settings.outputFilePath + "\n");
        System.out.print("\n");
    }
}












