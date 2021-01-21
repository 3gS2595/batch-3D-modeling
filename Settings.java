package main;

public class Settings {
    // hardware
    public String           inputFolder = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\hephaestus-\\input\\a";
//    public String           inputFolder = "C:\\Users\\Dylan Trinkner\\Documents\\studio\\ raw 3d files\\plants\\a";
    public String        outputFilePath = "C:\\Users\\Dylan Trinkner\\Documents\\GitHub\\hephaestus-\\out\\";
    public String   outputFileNameNotes = "_blazing_sun";
    public int                threadCnt = 6;
    // routine
    public boolean   removeUsedVertices = true;
    public boolean        findBySegment = false;
    public boolean prioritizeByDistance = false;    // broken
    // render
    public double                 ratio = 0.5;
    public double[]         maxDistance = {0,0,0};    // iteration movement range
    public double[]            rotation = {0,360,0};  // iteration movement range
    public int               iterations = 1;
    public boolean     standardizeScale = true;
    public boolean        centerObjects = true;
    // closet
    public double[]          tempRotate = {0,0,0}; // variable used during moving
    public double[]           translate = {0,0,0}; // variable used during moving
    public double[]            moveStep = {0,0,0}; // distance each iteration moves
    public boolean        VertexNormals = false;
    public boolean     avgVertexNormals = false;
    public String                 file0 = "";
    public String                 file1 = "";

}
// as a farmer wills , till our fields well
    // TODO -----------------------------------------------------------
    // compute both objects as mother for standard runs
    // certify the middle iterations objects are both cleanly centered

    // each run creates a folder to place its offspring in
    // output in stepped output to be viewed together in blender
    // decide on a placement hierarchy / organization













