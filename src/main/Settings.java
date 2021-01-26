package main;

public class Settings {

    // hardware
    public String           inputFolder = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\hephaestus-\\vanilla form";
    // public String           inputFolder = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\hephaestus-\\input";
    public String        outputFilePath = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\hephaestus-\\out\\";
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
    public int               iterations = 1;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;


// as a farmer wills , till our fields well
    // TODO -----------------------------------------------------------
    // compute both objects as mother for standard runs
    // certify the middle iterations objects are both cleanly centered
    // recursivley intake from sub folders/dirs?
    // display vertex counts of loaded objects

    // texture support
    // decide on a placement hierarchy / organization
    // allow control over parent choice/ allow single form operations
    // support spinning on multiple axis somehow
    // produce new form rather than form[0] overwrite, do without cloning




    // dust bin
    public double[]          tempRotate = {0,0,0}; // variable used during moving
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public String                 file0 = "";
    public String                 file1 = "";
    public int                   groups = 0;

    public static void printSettings(Settings settings) {
        System.out.print("          total forms " + (settings.iterations * settings.groups) + "\n");
        System.out.print("               groups " + settings.groups + "\n");
        System.out.print(" iterations per group " + settings.iterations + "\n");
        System.out.print("   removeUsedVertices " + settings.removeUsedVertices + "\n");
        System.out.print("        findBySegment " + settings.findBySegment + "\n");
        System.out.print(" prioritizeByDistance " + settings.prioritizeByDistance + "\n");
        System.out.print("     standardizeScale " + settings.standardizeScale + "\n");
        System.out.print("        centerObjects " + settings.centerObjects + "\n");
        System.out.print("        vertexNormals " + settings.VertexNormals + "\n");
        System.out.print("     avgVertexNormals " + settings.avgVertexNormals + "\n");
        System.out.println();
    }
}












