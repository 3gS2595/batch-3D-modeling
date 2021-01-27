package main;

public class Settings {

    // hardware
    public String           inputFolder = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\utemporia-\\input\\b";
    public String        outputFilePath = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\utemporia-\\out";
    public String   outputFileNameNotes = "_blazing_sun";
    public int                threadCnt = 5;
    // routine
    public boolean   removeUsedVertices = false;    // broken
    public boolean        findBySegment = false;
    public boolean prioritizeByDistance = false;    // broken
    // render
    public double                 ratio = 0.5;
    public double[]         maxDistance = {0,0,0};    // iteration movement range
    public double[]            rotation = {0,360,0};  // iteration movement range
    public int               iterations = 3;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;


// as farmers will ; till our flakes well
    // TODO -----------------------------------------------------------
    // compute both objects as mother for standard runs
    // certify the middle iterations objects are both cleanly centered
    // recursivley intake from sub folders/dirs?

    // split up output files toggle in case program runs out of memory on run
    // texture support
    // allow control over parent choice/ allow single form operations
    // support spinning on multiple axis somehow

    // dust bin
    public double[]          tempRotate = {0,0,0}; // variable used during moving
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public String                 file0 = "";
    public String                 file1 = "";
    public int                   groups = 0;

    public static void printSettings(Settings settings) {
        System.out.println();
        System.out.println();
        System.out.print("               subgroups " + settings.groups + "\n");
        System.out.print(" iterations per subgroup " + settings.iterations + "\n");
        System.out.print("          sum of the run " + settings.iterations * settings.groups + "\n");
        System.out.print("      removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("           findBySegment " + settings.findBySegment + "\n");
        System.out.print("    prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("        standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("           centerObjects " + settings.centerObjects + "\n");
        System.out.print("           vertexNormals " + settings.VertexNormals + "\n");
    }
}












