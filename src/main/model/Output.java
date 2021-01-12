package main.model;

import main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Output {
    private static DecimalFormat df = new DecimalFormat("#############0.00000000000000");
    public Output(List<Model> outputs) {
        try {
            if (outputs.get(0).settings.VertexNormals) outputs.get(0).calculateNormals();

            // names file based on time-date
            String time = (java.time.LocalTime.now() + "").replace(':', '_');
            String date = (java.time.LocalDate.now() + "").replace('-', '_');
            String dirPath = outputs.get(0).settings.outputFilePath
                    +   date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8)
                    + outputs.get(0).settings.outputFileNameNote
                    + ".obj";
            File file = new File(dirPath);
            FileWriter writer = new FileWriter(file);

            int cnt = 0;
            int Vcnt = 0;
            int VNcnt = 0;
            double[] move = {0,0,0};
            for (Model offspring : outputs) {
                // Prints .obj
                int vs = 0;
                offspring.standardizeScale();
                offspring.centerObject();
                offspring.settings.translate = move;
                offspring.translate();

                Main.Settings settings = offspring.settings;
                writer.write("# -hephaestus " + "offspring_" + cnt + "\n");
                writer.write("# Lineage, " + settings.file1 + ", " + settings.file2 + "\n");
                writer.write("# moved"  + " x:" + (settings.translate[0] - settings.maxDistance[0])
                                            + " y:" + (settings.translate[1] - settings.maxDistance[1])
                                            + " z:" + (settings.translate[2] - settings.maxDistance[2]) + "\n");
                writer.write("#                                               \n");
                writer.write("#             threaded " + settings.threaded + "\n");
                writer.write("#   removeUsedVertices " + settings.removeUsedVertices + "\n");
                writer.write("#     standardizeScale " + settings.standardizeScale + "\n");
                writer.write("#        centerObjects " + settings.centerObjects + "\n");
                writer.write("# prioritizeByDistance " + settings.prioritizeByDistance + "\n");
                writer.write("#        VertexNormals " + settings.VertexNormals + "\n");
                writer.write("#     avgVertexNormals " + settings.avgVertexNormals + "\n");
                writer.write("o offspring_" + cnt + "\n");
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
                for (String line : offspring.rawPolys) {
                    String f = "f ";
                    String[] split = line.split(" ");
                    String parse = "/";
                    if (line.contains("//")) parse = "//";
                    for (int i = 1; i < split.length; i++) {
                        int v1 = Integer.parseInt(split[i].split(parse)[0]);

                        int v2 = Integer.parseInt(split[i].split(parse)[1]);
                        if (!parse.equals("//")) v2 = Integer.parseInt(split[i].split(parse)[2]);
                        f = f.concat((v1 + Vcnt) + "//" + (v2 + VNcnt) + " ");
                    }
                    f = f.concat("\n");
                    writer.write(f);
                }
                move[0] = move[0] + 1;
                Vcnt += vs;
                cnt++;
            }

            writer.close();
            System.out.println("\noffspring filepath: " + dirPath);
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
