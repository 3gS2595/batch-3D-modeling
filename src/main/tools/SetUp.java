package main.tools;

import main.form.Form;
import main.Settings;
import main.form.weaving.translate;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SetUp {
    public List<Form> singles;

    // initial fil intake
    public SetUp(Settings settings){

        // finds .obj files
        List<String> files = (new Searcher(Paths.get(settings.inputFolder).toString())).search();
        List<Form> loadedFiles = new ArrayList<>();
        System.out.println("intaking-" + files.size());

        // loads files from found paths
        for (String fpath : files) {
            fpath = fpath.replace("\\", "/");
            loadedFiles.add(new Form(fpath, settings));
        }
        singles = loadedFiles;
    }

    public SetUp() {

    }

    // produces a list containing grouped forms
    public List<Form[]> group(Settings setting){
        HashMap<String, String> entryCheck = new HashMap<>();
        List<Form[]> wellsprings = new ArrayList<>();
        for(int i = 0; i < setting.wellsprings.size(); i++) {
            for (int j = 0; j < setting.wellsprings.size(); j++) {
                String check = setting.wellsprings.get(i).id.concat(setting.wellsprings.get(j).id);
                if(!setting.wellsprings.get(i).id.equals(setting.wellsprings.get(j).id) && !entryCheck.containsKey(check)) {
                    boolean breakFlag = false;
                    String checkij = setting.wellsprings.get(i).id.concat(setting.wellsprings.get(j).id);
                    String checkji = setting.wellsprings.get(j).id.concat(setting.wellsprings.get(i).id);
                    entryCheck.put(checkij,"");
                    entryCheck.put(checkji,"");
                    Form file0 = new Form(setting.wellsprings.get(i));
                    Form file1 = new Form(setting.wellsprings.get(j));
                    Form[] wellSpring = new Form[]{null, null};

                    if(setting.manualParentSelection){
                        System.out.println("\nselect wellsprings mother");
                        //opens files to get file size
                        File f0 = new File(file0.ObjName); File f1 = new File(file1.ObjName);
                        System.out.println("[1] " + file0.id + " " + f0.length());
                        System.out.println("[2] " + file1.id + " " + f1.length());
                        Scanner keyboard = new Scanner(System.in);
                        String selection = keyboard.nextLine();

                        //switch
                        if(selection.contains("1")) {
                            wellSpring[0] = logFilesUsed(file0, i, j, setting);
                            wellSpring[1] = logFilesUsed(file1, i, j, setting);
                        }
                        else if(selection.contains("2")) {
                            wellSpring[0] = logFilesUsed(file1, j, i, setting);
                            wellSpring[1] = logFilesUsed(file0, j, i, setting);
                        } else {
                            breakFlag = true;
                        }
                    } else {
                        //automatic
                        if (file0.v.size() > file1.v.size()) {
                            wellSpring[0] = logFilesUsed(file0, i, j, setting);
                            wellSpring[1] = logFilesUsed(file1, i, j, setting);
                        } else {
                            wellSpring[0] = logFilesUsed(file1, j, i, setting);
                            wellSpring[1] = logFilesUsed(file0, j, i, setting);
                        }
                    }

                    // iteration step calculation -> final placement
                    if(!breakFlag) {
                        double[] XyzIterationStepInitiator = new double[]{
                                -setting.maxDistance[0],
                                -setting.maxDistance[1],
                                -setting.maxDistance[2]};
                        wellSpring[0].moved[0] += -setting.maxDistance[0];
                        wellSpring[0].moved[1] += -setting.maxDistance[1];
                        wellSpring[0].moved[2] += -setting.maxDistance[2];
                        translate.translate(wellSpring[0], XyzIterationStepInitiator);
                        wellSpring[0].settings.moveStep = new double[]{
                                (setting.maxDistance[0] * 2) / setting.iterationCnt,
                                (setting.maxDistance[1] * 2) / setting.iterationCnt,
                                (setting.maxDistance[2] * 2) / setting.iterationCnt,
                                ((setting.ratio - setting.minRatio) / setting.iterationCnt)};
                        wellSpring[0].settings.tempRotate = new double[]{
                                (setting.rotation[0]) / setting.iterationCnt,
                                (setting.rotation[1]) / setting.iterationCnt,
                                (setting.rotation[2]) / setting.iterationCnt};
                        wellsprings.add(wellSpring);
                        System.out.println(wellSpring[0].settings.moveStep[0]+"final last");
                    }
                }
            }
        }
        if(setting.iterateRatio) setting.ratio = setting.minRatio;
        return wellsprings;
    }

    public Form logFilesUsed(Form form, int a, int b, Settings setting){
        double[] m0 = new double[7];
        System.arraycopy(setting.wellsprings.get(a).moved, 0, m0, 0,setting.wellsprings.get(a).moved.length);
        form.parentInfo.put(setting.wellsprings.get(a).ObjName, m0);
        double[] m1 = new double[7];
        System.arraycopy(setting.wellsprings.get(b).moved, 0, m1, 0,setting.wellsprings.get(b).moved.length);
        form.parentInfo.put(setting.wellsprings.get(b).ObjName, m1);
        form.settings = new Settings(setting);
        return form;
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
