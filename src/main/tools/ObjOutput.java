package main.tools;

import main.form.Form;
import main.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ObjOutput {
    private static final DecimalFormat df = new DecimalFormat("#############0.00000000000000");
    public static String createOutputFolder(Settings settings){
        String time = (java.time.LocalTime.now() + "").replace(':', '_');
        String date = (java.time.LocalDate.now() + "").replace('-', '_');
        String dirPath = settings.outputFilePath + "\\"
                +   date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8)
                + settings.outputFileNameNotes + "\\";
        System.out.println(dirPath);
        File file = new File(dirPath);

        //Creating the directory
        boolean bool = file.mkdir();
        if(bool) return dirPath;
        else return settings.outputFilePath;
    }

    public ObjOutput(List<Form> outputs, double[] groupStep) {
        try {
            if (outputs.get(0).settings.VertexNormals) outputs.get(0).calculateNormals();

            // names file based on time-date
            String time = (java.time.LocalTime.now() + "").replace(':', '_');
            String date = (java.time.LocalDate.now() + "").replace('-', '_');
            String dirPath = outputs.get(0).settings.outputFilePath
                    +   date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8)
                    + outputs.get(0).settings.outputFileNameNotes
                    + ".obj";
            File file = new File(dirPath);
            FileWriter writer = new FileWriter(file);

            int cnt = 0;
            int Vcnt = 0;
            int VNcnt = 0;
            double[] seperate = {0,0,0};

            // accounts for movement change introduced
            // by separating forms in current series
            double[] retainCorrectMove = {0,0,0};
            for (Form offspring : outputs) {

                // Prints .obj
                int vs = 0;
                offspring.standardizeScale();
                offspring.centerObject();
                offspring.translate(groupStep);
                offspring.translate(seperate);
                offspring.moved[0] -= retainCorrectMove[0];

                Settings settings = offspring.settings;
                writer.write("# -hephaestus " + "offspring_" + cnt + "\n");
                writer.write("# Lineage, " + settings.file0 + ", " + settings.file1 + "\n");
                writer.write("#                moved "  + " x:" + (offspring.moved[0])
                                            + " y:" + (offspring.moved[1])
                                            + " z:" + (offspring.moved[2]) + "\n");
                writer.write("#              rotated "  + " x:" + (offspring.moved[3])
                                            + " y:" + (offspring.moved[4])
                                            + " z:" + (offspring.moved[5]) + "\n");
                writer.write("#                                               \n");
                writer.write("#   removeUsedVertices " + settings.removeUsedVertices + "\n");
                writer.write("#     standardizeScale " + settings.standardizeScale + "\n");
                writer.write("#        centerObjects " + settings.centerObjects + "\n");
                writer.write("# prioritizeByDistance " + settings.prioritizeByDistance + "\n");
                writer.write("#        VertexNormals " + settings.VertexNormals + "\n");
                writer.write("o offspring_" + cnt + "\n");

                // MTL file name
                writer.write("mtllib " + offspring.MtlName + "\n");
                // V
                for (int i = 0; i < offspring.v.size(); i++) {
                    vs++;
                    writer.write("v" + " " + df.format(offspring.v.get(i)[0])
                                         + " " + df.format(offspring.v.get(i)[1])
                                         + " " + df.format(offspring.v.get(i)[2])
                                         + "\n");
                }

                // VN
                for (double[] vn : offspring.vn) {
                    writer.write("vn" + " " + df.format(vn[0])
                                          + " " + df.format(vn[1])
                                          + " " + df.format(vn[2])
                                          + "\n");
                }
                writer.write("usemtl None\n");
                writer.write("s off\n");
                writer.write("g objects\n");

                // F
                String curMat = "";
                for(int j = 0; j < offspring.rawf.size(); j++){
//                    if (curMat != offspring.rawMats.get(j)){
//                        curMat = offspring.rawMats.get(j);
//                        writer.write("usemtl " + curMat + "\n");
//                    }
                    String f = "f ";
                    String[] split = offspring.rawf.get(j).split(" ");
                    String parse = "/";
                    if (offspring.rawf.get(j).contains("//")) parse = "//";
                    for (int i = 1; i < split.length; i++) {
                        int v1 = Integer.parseInt(split[i].split(parse)[0]);

                        int v2 = Integer.parseInt(split[i].split(parse)[1]);
                        if (!parse.equals("//") && split[i].split(parse).length > 2)
                            v2 = Integer.parseInt(split[i].split(parse)[2]);
                        f = f.concat((v1 + Vcnt) + "//" + (v2 + VNcnt) + " ");
                    }
                    f = f.concat("\n");
                    writer.write(f);
                }
                Vcnt += vs;
                cnt++;
                seperate[0] += 2;
                retainCorrectMove[0] += 2;
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
