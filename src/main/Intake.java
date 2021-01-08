package main;

import me.tongfei.progressbar.ProgressBar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Intake {
    // Handles .obj first and then .mtl
    public static Model intake(Model model) {
        try (ProgressBar pb0 = new ProgressBar(model.OBJname, 1)) {
            try {
                BufferedReader reader;
                String line;

                // OBJ

                if (model.OBJname != null) {
                    //adds oen to offset from 0 as to keep indices correct
                    model.v.add(new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE});

                    //System.out.println(dirPath);
                    int linecnt = 0;
                    reader = new BufferedReader(new FileReader(model.OBJname));
                    double[] matID = new double[]{0.0, 0.0};
                    while ((line = reader.readLine()) != null) {
                        pb0.setExtraMessage("" + linecnt);
                        linecnt++;
                        String[] raw = line.split(" ");
                        double[] num = new double[raw.length];

                        // Skips comments
                        if (!raw[0].equals("#")) {

                            // Converts raw string into doubles
                            for (int i = 1; i < raw.length; i++) {
                                try {
                                    num[i] = Double.parseDouble(raw[i]);
                                } catch (Exception ignored) {
                                }
                            }

                            switch (raw[0]) {
                                case "mtllib":
                                    model.MTLname = raw[1];
                                    break;

                                case "v":
                                    model.v.add(new double[]{num[1], num[2], num[3], matID[0]});
                                    break;

                                case "f":
                                    model.rawPolys.add(line);
                                    int[] one;
                                    if (raw[1].contains("//")) {
                                        one = new int[]{Integer.parseInt(raw[1].split("//")[0]),
                                                Integer.parseInt(raw[2].split("//")[0]),
                                                Integer.parseInt(raw[3].split("//")[0])};
                                    } else {
                                        one = new int[]{Integer.parseInt(raw[1].split("/")[0]),
                                                Integer.parseInt(raw[2].split("/")[0]),
                                                Integer.parseInt(raw[3].split("/")[0])};
                                    }
                                    if (one[0] < 100000000000.0) {
                                        double[][] temps = new double[][]{model.v.get(one[0]).clone(),
                                                model.v.get(one[1]).clone(),
                                                model.v.get(one[2]).clone(),
                                                matID.clone()};
                                        model.f.add(temps);
                                    } else {
                                        System.out.println("WTF massive / infinite vertex" + one[0]);
                                    }

                                    if(model.settings.avgVertexNormals) {
                                        if (!model.siblingPoints.containsKey(one[0])) {
                                            List<Integer> t = new ArrayList<>();
                                            t.add(one[1]);
                                            t.add(one[2]);
                                            model.siblingPoints.put(one[0], t);
                                        } else if (model.siblingPoints.containsKey(one[0])) {
                                            List<Integer> t = model.siblingPoints.get(one[0]);
                                            t.add(one[1]);
                                            t.add(one[2]);
                                            model.siblingPoints.put(one[2], t);
                                        }

                                        if (!model.siblingPoints.containsKey(one[1])) {
                                            List<Integer> t = new ArrayList<>();
                                            t.add(one[0]);
                                            t.add(one[2]);
                                            model.siblingPoints.put(one[1], t);
                                        } else if (model.siblingPoints.containsKey(one[1])) {
                                            List<Integer> t = model.siblingPoints.get(one[1]);
                                            t.add(one[0]);
                                            t.add(one[2]);
                                            model.siblingPoints.put(one[2], t);
                                        }

                                        if (!model.siblingPoints.containsKey(one[2])) {
                                            List<Integer> t = new ArrayList<>();
                                            t.add(one[0]);
                                            t.add(one[1]);
                                            model.siblingPoints.put(one[2], t);
                                        } else if (model.siblingPoints.containsKey(one[2])) {
                                            List<Integer> t = model.siblingPoints.get(one[2]);
                                            t.add(one[0]);
                                            t.add(one[1]);
                                            model.siblingPoints.put(one[2], t);
                                        }
                                    }
                                    break;

                                case "usemtl":
                                    matID[0] = matID[0] + 1;
                                    model.matUsed.put(matID[0], raw[1]);
                                    break;
                            }
                        }
                    }
                }

                // MTL
                if (model.MTLname != null) {
                    String[] temp = model.OBJname.split("/");
                    String MTLFilePath = temp[0] + "/" + temp[1] + "/" + model.MTLname;
                    reader = new BufferedReader(new FileReader(MTLFilePath));
                    Material m = new Material();
                    while ((line = reader.readLine()) != null) {
                        String[] raw = line.split(" ");
                        double[] num = new double[raw.length];

                        // Skips comments
                        if (!raw[0].equals("#")) {

                            // Converts raw string into doubles
                            for (int i = 1; i < raw.length; i++) {
                                try {
                                    num[i] = Double.parseDouble(raw[i]);
                                } catch (Exception ignored) {
                                }
                            }

                            // Intake
                            switch (raw[0]) {
                                case "newmtl":
                                    // .mtl data intake entrance
                                    m = new Material();
                                    m.name = raw[1];
                                    break;

                                case "Ns":
                                    m.alpha = num[1];
                                    break;
                                case "Ka":
                                    m.ka = new double[]{num[1], num[2], num[3]};
                                    break;
                                case "Kd":
                                    m.kd = new double[]{num[1], num[2], num[3]};
                                    break;
                                case "Ks":
                                    m.ks = new double[]{num[1], num[2], num[3]};
                                    break;
                                case "Tr":
                                    m.tr = new double[]{num[1], num[2], num[3]};
                                    break;
                                case "Ni":
                                    m.ni = num[1];
                                    break;

                                case "illum":
                                    if (num[1] == 3) {
                                        m.kr = m.ks;
                                    } else {
                                        m.kr = new double[]{0.0, 0.0, 0.0};
                                    }

                                    // LOAD COMPLETE CHECK
                                    if (m.name != null && m.ka != null && m.kd != null && m.ks != null) {
                                        model.mats.add(m);
                                    } else System.out.println("MAT LOAD FAILURE");
                                    break;
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("vertices: " + model.v.size());
            System.out.println();

            pb0.step();
            return model;
        }
    }
}
