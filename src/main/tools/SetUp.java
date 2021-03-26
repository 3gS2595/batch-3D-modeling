package main.tools;

import main.form.Form;
import main.Settings;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SetUp {
    public List<Form> singles;
    public List<String> files;

    // initial fil intake
    public SetUp(Settings settings){
        if (settings.outputFolder.charAt(settings.outputFolder.length()-1) != '\\')
            settings.outputFolder = settings.outputFolder.concat("\\");

        // finds .obj files
        List<String> files = (new Searcher(Paths.get(settings.inputFolder).toString())).search();
        List<Form> loadedFiles = new ArrayList<>();
        if (files.size() % 2 != 0 && !settings.decimate) files.remove(files.size() - 1);
        System.out.println("intaking-" + files.size());

        // loads files from found paths
        for (String fpath : files) {
            fpath = fpath.replace("\\", "/");
            loadedFiles.add(new Form(fpath, settings));
        }
        singles = loadedFiles;
        this.files = files;
    }

    public SetUp() { }

    // produces a list containing grouped forms
    public List<Form[]> group(Settings setting){
        // builds run sets in either singles or groups
        List<Form[]> wellsprings = new ArrayList<>();
        for(int i = 0; i < setting.files.size(); i++) {
            for (int j = i + 1; j < setting.files.size(); j++) {
                if(!setting.files.get(i).equals(setting.files.get(j))) {
                    Form file0 = setting.forms.get(i);
                    Form file1 = setting.forms.get(j);
                    file0.filesUsed.clear();
                    file1.filesUsed.clear();

                    // [0] = mother , donates polygons
                    Form[] wellSpring = {null, null};
                    if (file0.v.size() > file1.v.size()) {
                        wellSpring[0] = file1;
                        wellSpring[0].filesUsed.add(setting.forms.get(i).id);
                        wellSpring[0].filesUsed.add(setting.forms.get(j).id);
                        wellSpring[1] = file0;
                        wellSpring[1].filesUsed.add(setting.forms.get(i).id);
                        wellSpring[1].filesUsed.add(setting.forms.get(j).id);
                    } else {
                        wellSpring[0] = file0;
                        wellSpring[0].filesUsed.add(setting.forms.get(j).id);
                        wellSpring[0].filesUsed.add(setting.forms.get(i).id);
                        wellSpring[1] = file1;
                        wellSpring[1].filesUsed.add(setting.forms.get(j).id);
                        wellSpring[1].filesUsed.add(setting.forms.get(i).id);
                    }

                    double[] XyzIterationStepInitiator = new double[]{
                            -setting.maxDistance[0],
                            -setting.maxDistance[1],
                            -setting.maxDistance[2]};
                    wellSpring[0].translate(XyzIterationStepInitiator);
                    wellSpring[0].settings.moveStep = new double[]{
                            (setting.maxDistance[0] * 2) / setting.iterationCnt,
                            (setting.maxDistance[1] * 2) / setting.iterationCnt,
                            (setting.maxDistance[2] * 2) / setting.iterationCnt};
                    wellSpring[0].settings.tempRotate = new double[]{
                            (setting.rotation[0]) / setting.iterationCnt,
                            (setting.rotation[1]) / setting.iterationCnt,
                            (setting.rotation[2]) / setting.iterationCnt};
                    wellsprings.add(wellSpring);
                }
            }
        }
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
