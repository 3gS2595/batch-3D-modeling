package main.tools;

import main.form.Form;
import main.Settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SetUp {
    public static List<Form[]> loadWells(Settings settings){
        List<String> files = (new Searcher(Paths.get(settings.inputFolder).toString())).search();
        List<Form> load = new ArrayList<>();
        if (files.size() % 2 != 0) files.remove(files.size() - 1);
        System.out.println("intaking-" + files.size());
        for (String fpath : files) {
            fpath = fpath.replace("\\", "/");
            load.add(new Form(fpath, settings));
        }


        List<Form[]> wellsprings = new ArrayList<>();
        if(settings.singleOut){
            for(Form form : load){
                wellsprings.add(new Form[]{form});
            }
        } else for(int i = 0; i < load.size(); i++) {
            for (int j = i + 1; j < load.size(); j++) {
                if(!files.get(i).equals(files.get(j))) {
                    Form file0 = load.get(i);
                    Form file1 = load.get(j);

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
                            (settings.maxDistance[0] * 2) / settings.iterations,
                            (settings.maxDistance[1] * 2) / settings.iterations,
                            (settings.maxDistance[2] * 2) / settings.iterations};
                    wellSpring[0].settings.tempRotate = new double[]{
                            (settings.rotation[0]) / settings.iterations,
                            (settings.rotation[1]) / settings.iterations,
                            (settings.rotation[2]) / settings.iterations};
                    wellsprings.add(wellSpring);
                }
            }
        }
        return wellsprings;
    }
    public static class Searcher {

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
