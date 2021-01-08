package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class Output {
    private static DecimalFormat df = new DecimalFormat("#############0.00000000000000");

    public Output(Model offspring, String parent0, String parent1){ try {
            if (offspring.settings.VertexNormals) offspring.calculateNormals();
            offspring.centerObject();
            String time = "" + java.time.LocalTime.now();
            time = time.substring(0, 8);
            time = time.replace(':', '_');
            String date = "" + java.time.LocalDate.now();
            date = date.replace('-', '_');
            offspring.centerObject();
            String dirPath = null;

                dirPath = new File(".").getCanonicalPath() + "/offspring/" + time + "__" + date + ".obj";

            File file = new File(dirPath);

            // Prints .obj
            FileWriter writer = new FileWriter(file);
            writer.write("# offspring of hephaestus v0.1\n");
            writer.write("# Lineage: \n");
            writer.write("# " + parent0 + "\n");
            writer.write("# " + parent1 + "\n");
            writer.write("# \n");
            writer.write("# please reach \n");
            writer.write("# cbtrinkner@gmail.com\n");
            for (int i = 1; i < offspring.v.size(); i++) {
                writer.write("v"
                        + " " + df.format(offspring.v.get(i)[0])
                        + " " + df.format(offspring.v.get(i)[1])
                        + " " + df.format(offspring.v.get(i)[2])
                        + "\n");
            }
            for (double[] vn : offspring.vn) {
                writer.write("vn"
                        + " " + df.format(vn[0])
                        + " " + df.format(vn[1])
                        + " " + df.format(vn[2])
                        + "\n");
            }
            for (String line : offspring.rawPolys) {
                writer.write(line + "\n");
            }
            writer.close();

            System.out.println("offspring location: " + dirPath);
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
