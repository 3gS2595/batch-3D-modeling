package main.tools;

import main.form.Form;
import main.Settings;
import main.form.weaving.centerMesh;
import main.form.weaving.standardizeScale;
import main.form.weaving.translate;
import main.pool.ThreadPool;
import me.tongfei.progressbar.ProgressBar;

import java.io.*;
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
            // if (pool.output.get(0).settings.VertexNormals) pool.output.get(0).calculateNormals();
            // names file based on time-date
            String time = (java.time.LocalTime.now() + "").replace(':', '_');
            String date = (java.time.LocalDate.now() + "").replace('-', '_');
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

            pb1.setExtraMessage(runCnt + ",_" + date.substring(5,10) + "_" + date.substring(0,4)+ "__" + time.substring(0, 8));
            for (Form offspring : pool.output) {
                // Prints .obj
                int vs = 0;
                standardizeScale.standardizeScale(offspring);
                centerMesh.centerMesh(offspring);
                translate.translate(offspring, pool.output.get(0).settings.groupStepBy);
                translate.translate(offspring, seperate);

                Settings settings = offspring.settings;
                writer.write("# -hephaestus " + "offspring_" + cnt + "\n");
                writer.write("#  dob-date:" + date + "-time:" + time + "\n");
                String fileList = "";
                int keyCnt = 0;
                for(String key : offspring.parentInfo.keySet()){
                    String[] fileName = key.split("/");
                    fileList = fileList.concat("#             parent{" + keyCnt + "} =" + fileName[fileName.length-1] + "\n");
                    fileList = fileList.concat("#                        " + key + "\n");
                    fileList = fileList.concat("#                      " + " x:" + (offspring.parentInfo.get(key)[0])
                            + " y:" + (offspring.parentInfo.get(key)[1])
                            + " z:" + (offspring.parentInfo.get(key)[2]) + "\n");
                    fileList = fileList.concat("#                      " + " x:" + offspring.parentInfo.get(key)[3]
                            + " y:" + (offspring.parentInfo.get(key)[4])
                            + " z:" + (offspring.parentInfo.get(key)[5]) + "\n");
                    keyCnt++;
                }
                writer.write(fileList);
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

                // infoFile.txt entry
                String info = runCnt + "_" + cnt + "__ '" + date.substring(5,10) + "_" + date.substring(0,4)+ "_" + time.substring(0, 8) + "\n";
                info = info.concat("                inside =" + filePath + "\n");
                info = info.concat("              siblings =" + pool.output.size() + "\n");
                info = info.concat("                 ratio " + offspring.settings.ratio + "\n");
                info = info.concat("    removeUsedVertices " + offspring.settings.removeUsedVertices + "\n");
                info = info.concat("      standardizeScale " + offspring.settings.standardizeScale + "\n");
                info = info.concat("         centerObjects " + offspring.settings.centerObjects + "\n");
                info = info.concat("  prioritizeByDistance " + offspring.settings.prioritizeByDistance + "\n");
                info = info.concat("         VertexNormals " + offspring.settings.VertexNormals + "\n");
                keyCnt = 0;
                for(String key : offspring.parentInfo.keySet()){
//                    System.out.println();
//                    for( int x = 0; x <  offspring.parentInfo.get(key).length; x++) {
//                        System.out.println(key + " " + offspring.parentInfo.get(key)[x]);
//                    }
                    info = info.concat("              parent{" + keyCnt + "} =" + key + "\n");
                    info = info.concat("                      "
                            + " x:" + offspring.parentInfo.get(key)[0]
                            + " y:" + offspring.parentInfo.get(key)[1]
                            + " z:" + offspring.parentInfo.get(key)[2] + "\n");
                    info = info.concat("                      "
                            + " rx:" + offspring.parentInfo.get(key)[3]
                            + " ry:" + offspring.parentInfo.get(key)[4]
                            + " rz:" + offspring.parentInfo.get(key)[5] + "\n");
                    keyCnt++;
                }
                info = info.concat("\n\n");
                pool.logText.add(info);

                // end of output book keeping
                Vcnt += vs;
                cnt++;
                seperate[0] += settings.separateDistX;
                pb1.step();
            }

            writer.flush();
            writer.close();
            pool.output.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
