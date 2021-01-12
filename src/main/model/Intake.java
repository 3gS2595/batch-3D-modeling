package main.model;

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

                // OBJ
                double[] matID = new double[]{0.0, 0.0};
                BufferedReader reader = new BufferedReader(new FileReader(model.OBJname));

                String line;
                int lineCnt = 0;
                while ((line = reader.readLine()) != null) {
                    lineCnt++;
                    pb0.setExtraMessage("" + lineCnt);
                    String[] raw = line.split(" ");

                    // Converts raw string into doubles
                    double[] num = new double[raw.length];
                    for (int i = 1; i < raw.length; i++)
                        try { num[i] = Double.parseDouble(raw[i]); }
                        catch (Exception ignored) {}

                    // Intakes .obj's line text
                    switch (raw[0]) {
                        case "mtllib":
                            model.MTLname = raw[1];
                            break;

                        case "usemtl":
                            matID[0] = matID[0] + 1;
                            model.matUsed.put(matID[0], raw[1]);
                            break;

                        case "v":
                            model.v.add(new double[]{num[1], num[2], num[3], matID[0]});
                            break;

                        case "vn":
                            model.rawVn.add(line);
                            break;

                        case "f":
                            // decides what obj uses as parser than parses
                            List<Integer> vIndices = new ArrayList<>();
                            double[][] vCoordinates = new double[raw.length][3];
                            String parse = "/";
                            if (line.contains("//")) parse = "//";
                            for (int i = 1; i < raw.length; i++) {
                                vCoordinates[i-1] = model.v.get(Integer.parseInt(raw[i].split(parse)[0]) - 1).clone();
                                vIndices.add(Integer.parseInt(raw[i].split(parse)[0]) - 1);
                            }

                            vCoordinates[raw.length - 1] = matID.clone();
                            model.f.add(vCoordinates);
                            model.rawPolys.add(line);

                            if(model.settings.avgVertexNormals) {
                                for(int vertex : vIndices) {
                                    if (!model.siblingPoints.containsKey(vertex)) {
                                        List<Integer> t = new ArrayList<>();
                                        for(int sibling: vIndices) {
                                            if(sibling != vertex)
                                            t.add(sibling);
                                        }
                                        model.siblingPoints.put(vertex, t);
                                    } else if (model.siblingPoints.containsKey(vertex)) {
                                        List<Integer> t = model.siblingPoints.get(vertex);
                                        for(int sibling: vIndices) {
                                            if(sibling != vertex)
                                                t.add(sibling);
                                        }
                                        model.siblingPoints.put(vertex, t);
                                    }
                                }
                            }
                            break;
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

                        // Converts raw string into doubles
                        for (int i = 1; i < raw.length; i++)
                            try { num[i] = Double.parseDouble(raw[i]); }
                            catch (Exception ignored) {}

                        switch (raw[0]) {
                            case "newmtl":
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
                                } else m.kr = new double[]{0.0, 0.0, 0.0};

                                // checks load completion
                                if (m.name != null && m.ka != null && m.kd != null && m.ks != null) {
                                    model.mats.add(m);
                                } else System.out.println("MAT LOAD FAILURE");
                                break;
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
