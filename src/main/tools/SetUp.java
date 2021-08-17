package main.tools;

import main.form.Form;
import main.Settings;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SetUp {
    public List<Form> singles;
    public List<String> files;

    // initial fil intake
    public SetUp(Settings settings){

        // finds .obj files
        List<String> files = (new Searcher(Paths.get(settings.inputFolder).toString())).search();
        List<Form> loadedFiles = new ArrayList<>();
//        if (files.size() % 2 != 0 && !settings.decimate) files.remove(files.size() - 1);
        System.out.println("intaking-" + files.size());

        // loads files from found paths
        for (String fpath : files) {
            fpath = fpath.replace("\\", "/");
            loadedFiles.add(new Form(fpath, settings));
        }
        singles = loadedFiles;
        this.files = files;
    }

    public SetUp() {

    }

    // produces a list containing grouped forms
    public List<Form[]> group(Settings setting){
        HashMap<String, String> entryCheck = new HashMap<>();
        // builds run sets in either singles or groups
        List<Form[]> wellsprings = new ArrayList<>();
        for(int i = 0; i < setting.wellsprings.size(); i++) {
            for (int j = 0; j < setting.wellsprings.size(); j++) {
                String check = setting.wellsprings.get(i).id.concat(setting.wellsprings.get(j).id);
                if(!setting.wellsprings.get(i).id.equals(setting.wellsprings.get(j).id) && !entryCheck.containsKey(check)) {
                    String check0 = setting.wellsprings.get(i).id.concat(setting.wellsprings.get(j).id);
                    String check1 = setting.wellsprings.get(j).id.concat(setting.wellsprings.get(i).id);
                    entryCheck.put(check0,"0");
                    entryCheck.put(check1,"1");
                    Form file0 = setting.wellsprings.get(i);
                    Form file1 = setting.wellsprings.get(j);
                    file0.filesUsed = new ArrayList<>();
                    file1.filesUsed = new ArrayList<>();
                    Form[] wellSpring = new Form[]{null, null};

                    if(setting.manualParentSelection){
                        System.out.println("\nselect wellsprings mother");
                        File f0 = new File(file0.ObjName);
                        File f1 = new File(file1.ObjName);
                        System.out.println("[1] " + file0.id + " " + f0.length());
                        System.out.println("[2] " + file1.id + " " + f1.length());
                        Scanner keyboard = new Scanner(System.in);
                        String selection = keyboard.nextLine();
                        if(selection.contains("1")) {
                            file0.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.settings = setting;
                            wellSpring[0] = file0;
                            file1.filesUsed.add(setting.wellsprings.get(j).id);
                            file1.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.settings = setting;
                            wellSpring[1] = file1;
                        }
                        if(selection.contains("2")) {
                            file1.filesUsed.add(setting.wellsprings.get(i).id);
                            file1.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.settings = setting;
                            wellSpring[0] = file1;
                            file0.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.settings = setting;
                            wellSpring[1] = file0;
                        }
                    } else {
                        // [0] = mother , donates polygons
                        if (file0.v.size() > file1.v.size()) {
                            file0.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.settings = setting;
                            wellSpring[0] = file0;
                            file1.filesUsed.add(setting.wellsprings.get(j).id);
                            file1.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.settings = setting;
                            wellSpring[1] = file1;

                        } else {
                            file1.filesUsed.add(setting.wellsprings.get(i).id);
                            file1.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.settings = setting;
                            wellSpring[0] = file1;
                            file0.filesUsed.add(setting.wellsprings.get(i).id);
                            file0.filesUsed.add(setting.wellsprings.get(j).id);
                            file0.settings = setting;
                            wellSpring[1] = file0;
                        }
                    }

                    double[] XyzIterationStepInitiator = new double[]{
                            -setting.maxDistance[0],
                            -setting.maxDistance[1],
                            -setting.maxDistance[2]};
                    wellSpring[0].moved[0] += -setting.maxDistance[0];
                    wellSpring[0].moved[1] += -setting.maxDistance[1];
                    wellSpring[0].moved[2] += -setting.maxDistance[2];
                    wellSpring[0].translate(XyzIterationStepInitiator);
                    wellSpring[0].settings.moveStep = new double[]{
                            (setting.maxDistance[0] * 2) / setting.iterationCnt,
                            (setting.maxDistance[1] * 2) / setting.iterationCnt,
                            (setting.maxDistance[2] * 2) / setting.iterationCnt,
                            (setting.ratio/setting.iterationCnt)};
//                    System.out.println(wellSpring[0].id);
//                    System.out.println(setting.ratio);
//                    System.out.println(setting.ratio/setting.iterationCnt);
//                    System.out.println(wellSpring[0].settings.moveStep[3]);
                    wellSpring[0].settings.tempRotate = new double[]{
                            (setting.rotation[0]) / setting.iterationCnt,
                            (setting.rotation[1]) / setting.iterationCnt,
                            (setting.rotation[2]) / setting.iterationCnt};
                    wellsprings.add(wellSpring);
                }
            }
        }
        if(setting.iterateRatio) setting.ratio = 0;
// use to debug matching
//        for (Form[] g :wellsprings){
//            System.out.println(g.length + " " + wellsprings.size());
//            System.out.println(g[0].id + ", " + g[1].id);
//            System.out.println(g[0].filesUsed);
//            System.out.println(g[1].filesUsed);
//            System.out.println();
//        }
        return wellsprings;
    }

    // finds .obj files recursively starting at the input folder
    private static class Searcher {
        private final String root;

        public Searcher(String root) {
            this.root = root;
        }

        public List<String> search() {
            List<String> fs = new ArrayList<>();
            File folder = new File(root);
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    String path = file.getPath().replace('\\', '/');
                    if (file.isDirectory()) {
                        fs.addAll(new Searcher(path + "/").search());
                    } else {
                        if (file.toString().substring(file.toString().length() - 4).toLowerCase().contains(".obj")) {
                            fs.add(file.getAbsolutePath());
                            System.out.println(path);
                        }
                    }
                }
            }
            return fs;
        }
    }
}
