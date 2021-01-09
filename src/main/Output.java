package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Output {
    private static DecimalFormat df = new DecimalFormat("#############0.00000000000000");
    public Output(List<Model> outputs) {
        try {
            if (outputs.get(0).settings.VertexNormals) outputs.get(0).calculateNormals();
            outputs.get(0).centerObject();

            // names file based on time-date
            String time = "" + java.time.LocalTime.now();
            time = time.substring(0, 8);
            time = time.replace(':', '_');
            String date = "" + java.time.LocalDate.now();
            date = date.replace('-', '_');
            String dirPath = outputs.get(0).settings.outputFilePath
                    + time + "__" + date
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

                writer.write("# -hephaestus " + "offspring_" + cnt + "\n");
                writer.write("# Lineage, " + offspring.settings.file1 + ", " + offspring.settings.file2 + "\n");
                writer.write("# moved"
                        + " x:" + (offspring.settings.translate[0] - offspring.settings.maxDistance[0])
                        + " y:" + (offspring.settings.translate[1] - offspring.settings.maxDistance[1])
                        + " z:" + (offspring.settings.translate[2] - offspring.settings.maxDistance[2])  + "\n");
                writer.write("# \n");
                writer.write("#             threaded " + offspring.settings.threaded + "\n");
                writer.write("#   removeUsedVertices " + offspring.settings.removeUsedVertices + "\n");
                writer.write("#     standardizeScale " + offspring.settings.standardizeScale + "\n");
                writer.write("#        centerObjects " + offspring.settings.centerObjects + "\n");
                writer.write("# prioritizeByDistance " + offspring.settings.prioritizeByDistance + "\n");
                writer.write("#        VertexNormals " + offspring.settings.VertexNormals + "\n");
                writer.write("#     avgVertexNormals " + offspring.settings.avgVertexNormals + "\n");
                writer.write("o offspring_" + cnt + "\n");
                for (int i = 1; i < offspring.v.size(); i++) {
                    writer.write("v"
                            + " " + df.format(offspring.v.get(i)[0])
                            + " " + df.format(offspring.v.get(i)[1])
                            + " " + df.format(offspring.v.get(i)[2])
                            + "\n");
                            vs++;
                }
                for (double[] vn : offspring.vn) {
                    writer.write("vn"
                            + " " + df.format(vn[0])
                            + " " + df.format(vn[1])
                            + " " + df.format(vn[2])
                            + "\n");
                }
                writer.write("usemtl None\n");
                writer.write("s off\n");
                for (String line : offspring.rawPolys) {
                    String[] split = line.split(" ");
                    List<ArrayList<Integer>> xyz = new ArrayList<>();
                    if(split[1].contains("//")) {
                        for (int i = 1; i < split.length; i++) {
                            ArrayList<Integer> temp = new ArrayList<>();
                            temp.add(0, Integer.parseInt(split[i].split("//")[0]));
                            xyz.add(i - 1, temp);
                        }
                        for (int i = 1; i < split.length; i++) {
                            ArrayList<Integer> temp = new ArrayList<>(xyz.get(i - 1));
                            temp.add(1, Integer.parseInt(split[i].split("//")[1]));
                            xyz.set(i - 1, temp);
                        }
                    } else {
                        for (int i = 1; i < split.length; i++) {
                            ArrayList<Integer> temp = new ArrayList<>();
                            temp.add(0, Integer.parseInt(split[i].split("/")[0]));
                            xyz.add(i - 1, temp);
                        }
                        for (int i = 1; i < split.length; i++) {
                            ArrayList<Integer> temp = new ArrayList<>(xyz.get(i - 1));
                            temp.add(1, Integer.parseInt(split[i].split("/")[2]));
                            xyz.set(i - 1, temp);
                        }
                    }

                    String f = "f ";
                    for(ArrayList<Integer> cur : xyz) {
                        f = f.concat((cur.get(0) + Vcnt) + "//" + (cur.get(1) + VNcnt) + " ");
                    }
                    f = f.concat("\n");
                    writer.write(f);
                }
                move[0] = move[0] + 1;
                Vcnt += vs;
                cnt++;
            }
            writer.close();

            System.out.println("offspring location: " + dirPath);
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
