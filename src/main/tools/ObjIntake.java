package main.tools;

import main.form.Form;
import main.form.Material;
import me.tongfei.progressbar.ProgressBar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjIntake {
    // Handles .obj first and then .mtl
    public static Form intake(Form form) {
        int fileLength = 0;
        try {
            fileLength = countLines(form.ObjName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // truncates pb id text length
        int terminalSpacing = 12;
        String loadName;
        if(form.id.length() >= terminalSpacing) {
            loadName = form.id.substring(0, terminalSpacing) ;
        } else {
            loadName = form.id;
            while (loadName.length() != terminalSpacing) {
                loadName = loadName.concat(" ");
            }
        }

        try (ProgressBar pb1 = new ProgressBar(loadName , fileLength)) {

                // OBJ
                double[] matID = new double[]{0.0, 0.0};
                BufferedReader reader = new BufferedReader(new FileReader(form.ObjName));

                String line;
                while ((line = reader.readLine()) != null) {
                    pb1.step();
                    String[] raw = line.split(" ");

                    // Converts raw string into doubles
                    double[] num = new double[raw.length];
                    for (int i = 1; i < raw.length; i++)
                        try { num[i] = Double.parseDouble(raw[i]); }
                        catch (Exception ignored) {}

                    // Intakes .obj's line text
                    switch (raw[0]) {
                        case "mtllib":
                            form.MtlName = raw[1];
                            break;

                        case "usemtl":
                            break;

                        case "v":
                            form.v.add(new double[]{num[1], num[2], num[3], matID[0]});
                            break;

                        case "f":
                            // decides what obj uses as parser than parses
                            List<Integer> vIndices = new ArrayList<>();
                            String parse = "/";
                            if (line.contains("//")) parse = "//";
                            for (int i = 1; i < raw.length; i++) {
                                vIndices.add(Integer.parseInt(raw[i].split(parse)[0]) - 1);
                            }
                            form.f.add(vIndices);
                            form.rawf.add(line);

                            // populates vertex sibling points 'siblingPoints'
                            if(form.settings.nearestSurface || form.settings.nearestSurfaceProj) {
                                for(int vertexIndex : vIndices) {
                                    if (!form.siblingPolys.containsKey(vertexIndex)) {
                                        List<List<Integer>> t = new ArrayList<>();
                                        t.add(vIndices);
                                        if(vertexIndex == -1){
                                            System.out.println("WAIT");
                                            System.exit(1);
                                        }
                                        form.siblingPolys.put(vertexIndex, t);
                                    } else if (form.siblingPolys.containsKey(vertexIndex)) {
                                        List<List<Integer>> t = form.siblingPolys.get(vertexIndex);
                                        t.add(vIndices);
                                        if(vertexIndex == -1){
                                            System.out.println("WAIT");
                                            System.exit(1);
                                        }
                                        form.siblingPolys.put(vertexIndex, t);
                                    }
                                }
                            }

                            // populates vertex sibling points 'siblingPoints'
                            if(form.settings.avgVertexNormals) {
                                for(int vertexIndex : vIndices) {
                                    if (!form.siblingPoints.containsKey(vertexIndex)) {
                                        List<Integer> t = new ArrayList<>();
                                        for(int sibling: vIndices) {
                                            if(sibling != vertexIndex)
                                            t.add(sibling);
                                        }
                                        form.siblingPoints.put(vertexIndex, t);
                                    } else if (form.siblingPoints.containsKey(vertexIndex)) {
                                        List<Integer> t = form.siblingPoints.get(vertexIndex);
                                        for(int sibling: vIndices) {
                                            if(sibling != vertexIndex)
                                                t.add(sibling);
                                        }
                                        form.siblingPoints.put(vertexIndex, t);
                                    }
                                }
                            }
                            break;
                    }
                }

                // MTL
                String[] temp = form.ObjName.split("/");
                String MTLFilePath = temp[0] + "/" + temp[1] + "/" + form.MtlName;
                if (Files.isReadable(Paths.get(MTLFilePath))) {
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
                                    form.mats.add(m);
                                } else System.out.println("MAT LOAD FAILURE");
                                break;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return form;

    }

    public static int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            boolean empty = true;
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }
    }
}
