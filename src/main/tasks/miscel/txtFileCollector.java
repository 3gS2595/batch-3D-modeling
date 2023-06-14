package main.tasks.miscel;

import main.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class txtFileCollector implements Task {
	String src = "C:\\Users\\lucoius\\Documents\\3c9f3\\hepheastus\\documentation_";
	String dstFileName = "TXT.txt";

	public txtFileCollector() {
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
				if (file.getAbsoluteFile().toString().contains(".txt")) {
					System.out.println(file.getName());

					//print file info header
					String breaker = "-------------------------------------------------------------------------------------\n";
					text.add(breaker);
					int cnt = 85 - file.getName().length();
					String name = file.getName();
					while (cnt > 0) {
						name = name.concat("-");
						cnt--;
					}
					name = name.concat("\n");
					text.add(
							breaker.concat(
							name.concat(
							breaker.concat(
							breaker
						))));

					//.txt file reader

					// Open the file
					FileInputStream fstream = new FileInputStream(file.getPath());
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
					String line;
					while ((line = br.readLine()) != null) {
						text.add(line + "\n");
					}
					fstream.close();
					text.add(" \n\n\n");
				}
			}

			//creates TXT.txt
			File file = new File(src + "\\" + dstFileName);
			FileWriter writer = new FileWriter(file);

			//output
			for (String line : text) {
				writer.write(line);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}
// if in need of recursive search this is here
//	private class Searcher {
//		private final String root;
//
//		public Searcher(String root) {
//			this.root = root;
//		}
//
//		public List<File> search() {
//			List<File> fs = new ArrayList<>();
//			File folder = new File(root);
//			File[] listOfFiles = folder.listFiles();
//			if (listOfFiles != null) {
//				for (File file : listOfFiles) {
//					String path = file.getPath().replace('\\', '/');
//					if (file.isDirectory()) {
//						fs.addAll(new Searcher(path + "/").search());
//					} else {
//						if (file.toString().substring(file.getName().length() - 4).toLowerCase().contains(".txt")) {
//							fs.add(file);
//							System.out.println(file.getName());
//						}
//					}
//				}
//			}
//			return fs;
//		}
//	}
}
