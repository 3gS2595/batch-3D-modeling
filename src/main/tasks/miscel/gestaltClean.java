package main.tasks.miscel;

import main.tasks.Task;
import java.io.File; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import javafx.util.Pair;
import java.nio.file.*;

public class gestaltClean implements Task {
	String src = "/home/pin/test/in/";
	String dst = "/home/pin/test/ins/";
	boolean delete = false;
	boolean update = false;
	
	public gestaltClean() {}

	@Override
	public void run() {
		HashMap<String, ArrayList<Pair<String, File>>> oldSongs = new HashMap<String, ArrayList<Pair<String, File>>>();
		oldSongs = searchOld(src, oldSongs);
		HashMap<String, ArrayList<Pair<String, File>>> newSongs = new HashMap<String, ArrayList<Pair<String, File>>>();
		newSongs = searchOld(dst, newSongs);
		optionSelection();
		System.out.println(oldSongs.size());
		System.out.println(newSongs.size());
		System.out.println(update);
		System.out.println(delete);
		gestaltClean m = new gestaltClean();
		if(update) m.copyMissing(oldSongs, newSongs);
		if(delete) m.deleteMissing(oldSongs, newSongs);
	}
	 void optionSelection() {
		System.out.println("[1] dst delete if exist in src");
		System.out.println("[2] src copy if missing in existing dst artist \n inputs:");
		Scanner keyboard = new Scanner(System.in);
		String selection = keyboard.nextLine();
		if(selection.contains("1")) this.delete = true;
		if(selection.contains("2")) this.update = true;
	}


	 HashMap<String, ArrayList<Pair<String, File>>> searchOld(String inPath, HashMap<String, ArrayList<Pair<String, File>>> songs) {
		File folder = new File(inPath);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				String path = file.getPath().replace('\\', '/');
				if (file.isDirectory()) {
					songs = (new gestaltClean().searchOld(path+"/", songs));
				} else {
					if (file.toString().substring(file.toString().length() - 4).toLowerCase().contains(".mp3")) {
						String[] split = file.toString().split("-");

						String artist = split[0].replace(inPath, "");
						while(artist.charAt(artist.length()-1) == ' ') artist = artist.substring(0, artist.length()-1);

						String title = "";
						if(split.length > 1){
		 					for(int i = 1; i <= split.length -1; i++){
								title = title.concat(split[i]);
							}
						} else title = split[0];
						while(title.charAt(0) == ' ') title = title.substring(1);
						title = title.substring(0, title.length()-4);
						
						ArrayList<Pair<String, File>> templ = songs.get(artist);
						if(templ == null){
							templ = new ArrayList<Pair<String, File>>();
							templ.add(new Pair<>(title, file));
						} else {
							templ.add(new Pair<>(title, file));
						}
						songs.put(artist, templ);
					}
				}
			}
		}
		return songs;
	}


	void copyMissing(HashMap<String, ArrayList<Pair<String, File>>> oldSongs, HashMap<String, ArrayList<Pair<String, File>>> newSongs)  {
		for (Map.Entry<String,ArrayList<Pair<String, File>>> e : newSongs.entrySet()) {
			if(oldSongs.containsKey(e.getKey())){
				for(Pair<String, File> n:oldSongs.get(e.getKey())){
					boolean missing = true;
					for(Pair<String, File> i:e.getValue()){
						if(n.getKey().equals(i.getKey()))
							missing = false;
					}

					if(missing == true){
						System.out.println("MISSING" + (n.getValue().getAbsolutePath()));
						try {
							Path copSrc = Paths.get(n.getValue().getAbsolutePath());
							Path copDst = Paths.get(dst.concat(n.getValue().getAbsolutePath().replace(src, "")));
							Files.copy(copSrc, copDst, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException p){
							System.out.println(p);
						}
					} else {
						System.out.println("EXISTS" + (n.getValue().getAbsolutePath()));
					}
				}	
			}
		}
	}
	void deleteMissing(HashMap<String, ArrayList<Pair<String, File>>> oldSongs, HashMap<String, ArrayList<Pair<String, File>>> newSongs)  {
		for (Map.Entry<String,ArrayList<Pair<String, File>>> e : newSongs.entrySet()) {
			if(oldSongs.containsKey(e.getKey())){
				for(Pair<String, File> n:oldSongs.get(e.getKey())){
					boolean missing = true;
					for(Pair<String, File> i:e.getValue()){
						if(n.getKey().equals(i.getKey()))
							missing = false;
					}

					if(missing == true){
						System.out.println("MISSING" + (n.getValue().getAbsolutePath()));
						
					} else {
						System.out.print("EXISTS" + (n.getValue().getAbsolutePath()));
						if (n.getValue().delete()) { 
							System.out.println("Deleted the file: ");
						} else {
							System.out.println("Failed to delete the file.");
						} 
					}
				}	
			}
		}
	}

}
