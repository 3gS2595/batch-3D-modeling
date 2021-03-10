package main.tools;

import main.form.Form;
import main.Settings;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SetUp {
    public static List<Form[]> loadWells(Settings settings){
        if (settings.outputFolder.charAt(settings.outputFolder.length()-1) != '\\')
            settings.outputFolder = settings.outputFolder.concat("\\");

        // finds .obj files
        List<String> files = (new Searcher(Paths.get(settings.inputFolder).toString())).search();
        List<Form> loadedFiles = new ArrayList<>();
        if (files.size() % 2 != 0 && !settings.singleOut) files.remove(files.size() - 1);
        System.out.println("intaking-" + files.size());

        // loads files from found paths
        for (String fpath : files) {
            fpath = fpath.replace("\\", "/");
            loadedFiles.add(new Form(fpath, settings));
        }

        // builds run sets in either singles or groups
        List<Form[]> wellsprings = new ArrayList<>();
        if(settings.singleOut){
            for(Form form : loadedFiles){
                wellsprings.add(new Form[]{form});
            }
        } else for(int i = 0; i < loadedFiles.size(); i++) {
            for (int j = i + 1; j < loadedFiles.size(); j++) {
                if(!files.get(i).equals(files.get(j))) {
                    Form file0 = loadedFiles.get(i);
                    Form file1 = loadedFiles.get(j);

                    // [0] = mother , donates polygons
                    Form[] wellSpring = {null, null};
                    if (file0.v.size() > file1.v.size()) {
                        wellSpring[0] = file1;
                        wellSpring[0].settings.file0 = files.get(i);
                        wellSpring[0].settings.file1 = files.get(j);
                        wellSpring[1] = file0;
                        wellSpring[0].settings.file0 = files.get(i);
                        wellSpring[0].settings.file1 = files.get(j);
                    } else {
                        wellSpring[0] = file0;
                        wellSpring[0].settings.file0 = files.get(j);
                        wellSpring[0].settings.file1 = files.get(i);
                        wellSpring[1] = file1;
                        wellSpring[0].settings.file0 = files.get(j);
                        wellSpring[0].settings.file1 = files.get(i);
                    }

                    double[] XyzIterationStepInitiator  = new double[]{
                            -settings.maxDistance[0],
                            -settings.maxDistance[1],
                            -settings.maxDistance[2]};
                    wellSpring[0].translate(XyzIterationStepInitiator);
                    wellSpring[0].settings.moveStep = new double[]{
                            (settings.maxDistance[0] * 2) / settings.iterationCnt,
                            (settings.maxDistance[1] * 2) / settings.iterationCnt,
                            (settings.maxDistance[2] * 2) / settings.iterationCnt};
                    wellSpring[0].settings.tempRotate = new double[]{
                            (settings.rotation[0]) / settings.iterationCnt,
                            (settings.rotation[1]) / settings.iterationCnt,
                            (settings.rotation[2]) / settings.iterationCnt};
                    wellsprings.add(wellSpring);
                }
            }
        }
        return wellsprings;
    }
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
