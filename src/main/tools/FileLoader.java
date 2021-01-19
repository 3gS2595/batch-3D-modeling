package main.tools;

import main.Main;
import main.form.Form;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {
    public static List<Form[]> loadWells(Main.Settings settings){
        List<String> files = folderIntake(settings.inputFolder);
        List<Form> load = new ArrayList<>();
        try (ProgressBar pb = new ProgressBar(files.size() + " loading", files.size())) {
            if (files.size() % 2 != 0) files.remove(files.size() - 1);
            for(int i = 0; i < files.size(); i++) {
                pb.setExtraMessage(i + "/" + files.size() + files.get(i));
                load.add(new Form(files.get(i), settings));
                pb.step();
            }
        }

        List<Form[]> wellsprings = new ArrayList<>();
        for(int i = 0; i < load.size(); i++) {
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
                    wellSpring[0].settings.translate = new double[]{
                            -settings.maxDistance[0],
                            -settings.maxDistance[1],
                            -settings.maxDistance[2]};
                    wellSpring[0].translate();
                    wellSpring[0].settings.moveStep = new double[]{
                            (settings.maxDistance[0] * 2) / settings.iterations,
                            (settings.maxDistance[1] * 2) / settings.iterations,
                            (settings.maxDistance[2] * 2) / settings.iterations};

                    wellsprings.add(wellSpring);
                }
            }
        }


        return wellsprings;
    }

    private static List<String> folderIntake(String filePath){
        File folder = new File(filePath);
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if(fileEntry.toString().contains(".obj"))files.add(fileEntry.toString());
        }
        return files;
    }
}
