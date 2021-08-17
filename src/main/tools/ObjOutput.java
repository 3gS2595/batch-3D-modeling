package main.tools;

import main.form.Form;
import main.Settings;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

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
        String dirPath = settings.outputFolder + "/"
                +   date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8)
                + settings.outputFileNameNotes + "/";
        dirPath = dirPath.replace("\\", "/");
        File file = new File(dirPath);

        if(settings.saveOutput) {

            //Creating the directory
            boolean bool = file.mkdir();
            if (bool) {
                return dirPath;
            } else {
                System.out.println("FOLDER CREATION FAILURE");
                return settings.outputFolder;
            }
        }
        return null;
    }

    public static void output(ThreadPool pool, ProgressBar pb1, int runCnt) {
        try {
//            if (pool.output.get(0).settings.VertexNormals) pool.output.get(0).calculateNormals();

            // names file based on time-date
            String time = (java.time.LocalTime.now() + "").replace(':', '_');
            String date = (java.time.LocalDate.now() + "").replace('-', '_');
            System.out.println(pool.output.get(0).settings.outputFolder);
            String filePath = pool.output.get(0).settings.outputFolder
                    +  runCnt + "_" + date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8)
                    + "_" + pool.output.get(0).settings.outputFileNameNotes
                    + ".obj";
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            System.out.println("\n" + filePath);

            int cnt = 0;
            int Vcnt = 0;
            int VNcnt = 0;
            double[] seperate = {0,0,0};

            // accounts for movement change introduced
            // by separating forms in current series
            double[] retainCorrectMove = {0,0,0};

            pb1.setExtraMessage(runCnt + ",_" + date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8));
            for (Form offspring : pool.output) {
                // Prints .obj
                int vs = 0;
                offspring.standardizeScale();
                offspring.centerObject();
                offspring.translate(pool.output.get(0).settings.groupStep);
                offspring.translate(seperate);
                offspring.moved[0] -= retainCorrectMove[0];

                Settings settings = offspring.settings;
                writer.write("# -hephaestus " + "offspring_" + cnt + "\n");
                writer.write("#  dob-date:" + date + "-time:" + time + "\n");
                String fileList = "";
                for (int i = 0; i < offspring.filesUsed.size(); i++){
                    fileList = fileList.concat("#             parent{" + i + "} =" + offspring.filesUsed.get(i) + "\n");
                }
                writer.write(fileList);
                writer.write("#                moved " + " x:" + (offspring.moved[0])
                        + " y:" + (offspring.moved[1])
                        + " z:" + (offspring.moved[2]) + "\n");
                writer.write("#              rotated " + " x:" + (offspring.moved[3])
                        + " y:" + (offspring.moved[4])
                        + " z:" + (offspring.moved[5]) + "\n");
                writer.write("#                                               \n");
                writer.write("#    " + settings.removeUsedVertices + "\n");
                writer.write("#             nearestV " + settings.nearestVertice + "\n");
                writer.write("#             nearestS " + settings.nearestSurface + "\n");
                writer.write("#                ratio " + settings.ratio + "\n");
                writer.write("#        iterate ratio " + settings.iterateRatio + "\n");
                writer.write("#             decimate " + settings.decimate + "\n");
                writer.write("#     standardizeScale " + settings.standardizeScale + "\n");
                writer.write("#        centerObjects " + settings.centerObjects + "\n");
                writer.write("#        vertexNormals " + settings.VertexNormals + "\n");
                writer.write("#   removeUsedVertices " + settings.removeUsedVertices + "\n");
                writer.write("# prioritizeByDistance " + settings.prioritizeByDistance + "\n");
                writer.write("o " + runCnt + "_" + cnt + "__ " + date.substring(5,10) + "_" + date.substring(0,4)+ "_" + time.substring(0, 8) + "\n");

                // MTL file name
                writer.write("mtllib " + offspring.MtlName + "\n");

                for (int i = 0; i < offspring.v.size(); i++) {
                    vs++;
                    writer.write("v" + " " + df.format(offspring.v.get(i)[0])
                            + " " + df.format(offspring.v.get(i)[1])
                            + " " + df.format(offspring.v.get(i)[2])
                            + "\n");
                    if(offspring.v.get(i)[0] == 0 && offspring.v.get(i)[1] == 0){
                        System.out.print("something broken");
                    }
                }

                writer.write("usemtl None\n");
                writer.write("s off\n");
                writer.write("g objects\n");

                // F
                if(offspring.f.size() > 0){
                    for (List<Integer> cv : offspring.f) {
                        String f = "f ";
                        for (int v1 : cv) {
                            f = f.concat((v1 + Vcnt + 1) + " ");
                        }
                        f = f.concat("\n");
                        writer.write(f);
                    }
                } else {
                    for (int j = 0; j < offspring.rawf.size(); j++) {
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
                }
                Vcnt += vs;
                cnt++;
                seperate[0] += settings.separationDistanceX;
                retainCorrectMove[0] += settings.separationDistanceX;
                pb1.step();


                String info = runCnt + "_" + cnt + "__ '" + date.substring(5,10) + "_" + date.substring(0,4)+ "_" + time.substring(0, 8) + "\n";
                for (int i = 0; i < offspring.filesUsed.size(); i++){
                    info = info.concat("            parent{" + i + "} =" + offspring.filesUsed.get(i) + "\n");
                }
                info = info.concat("                moved " + " x:" + (offspring.moved[0])
                        + " y:" + (offspring.moved[1])
                        + " z:" + (offspring.moved[2]) + "\n");
                info = info.concat("              rotated " + " x:" + (offspring.moved[3])
                        + " y:" + (offspring.moved[4])
                        + " z:" + (offspring.moved[5]) + "\n");
                info = info.concat("             filepath " + filePath + "\n");
                info = info.concat("                ratio " + offspring.settings.ratio + "\n");
                info = info.concat("   removeUsedVertices " + offspring.settings.removeUsedVertices + "\n");
                info = info.concat("     standardizeScale " + offspring.settings.standardizeScale + "\n");
                info = info.concat("        centerObjects " + offspring.settings.centerObjects + "\n");
                info = info.concat(" prioritizeByDistance " + offspring.settings.prioritizeByDistance + "\n");
                info = info.concat("        VertexNormals " + offspring.settings.VertexNormals + "\n");
                info = info.concat("\n");
                pool.logText.add(info);
            }

            writer.flush();
            writer.close();
            pool.output.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
