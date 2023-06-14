package main.tasks.miscel;

import main.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class csv implements Task {
	String src = "C:\\Users\\lucoius\\Documents\\spotify_playlists";
	String dstFileName = "CSV.csv";

	public csv() {
	}

	@Override
	public void run() {
		try {
			//deletes previous collection
			File tempFile = new File(src + "\\" + dstFileName);
			boolean exists = tempFile.exists();
			if(exists)
				Files.deleteIfExists(Paths.get(src + "\\" + dstFileName));

			ArrayList<String> text = new ArrayList<>();
			File dir = new File(src);
			for (File file : dir.listFiles()) {
				if (file.getAbsoluteFile().toString().contains(".csv")) {
					System.out.println(file.getName());

					// Open the file
					FileInputStream fstream = new FileInputStream(file.getPath());
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
					String line;
					String indexData = br.readLine();
					String[] indexArray= indexData.split(",");
					int trackID = -1;
					int artistID = -1;
					for (int i =0; i<indexArray.length; i++){
						if(indexArray[i].contains("Track Name")){
							trackID = i;
						}
						if(indexArray[i].contains("Artist Name(s)")){
							artistID = i;
						}
					}
					while ((line = br.readLine()) != null) {
						String[] array = line.split(",");
						if(trackID == -1 || artistID == -1){
							System.out.println("CSVFUCK");
							System.exit(1);
						}
						text.add(array[artistID] + "," + array[trackID]  + "\n");

					}
					fstream.close();
				}
			}

			//creates TXT.txt
			File file = new File(src + "\\" + dstFileName);
			FileWriter writer = new FileWriter(file);
			writer.write("\"Artist Name\"," + "\"Track Name\"" + "\n");

			//output
			for (String line : text) {
				writer.write(line);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
