package main.tasks.miscel;

import main.pool.ThreadPool;
import main.tasks.Task;
import main.tools.SetUp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ImageCollect implements Task {
	 String src = "D:\\c71238796\\26e72a9\\forms\\form";
	 String dst = "D:\\c71238796\\26e72a9\\mappings\\form\\0_PHONE DIRECTORY\\";

	public ImageCollect() {
	}

	@Override
	public void run() {
		try {
			Searcher search = new Searcher(src);
			List<File> files = search.search();
			for (File file : files) {
				Files.copy(file.toPath(),
						(new File(dst + file.getName())).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class Searcher {
		private final String root;
		public Searcher(String root) {
			this.root = root;
		}

		public List<File> search() {
			List<File> fs = new ArrayList<>();
			File folder = new File(root);
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					String path = file.getPath().replace('\\', '/');
					if (file.isDirectory()) {
						fs.addAll(new Searcher(path + "/").search());
					} else {
						if (file.toString().substring(file.toString().length() - 4).toLowerCase().contains(".png")) {
							fs.add(file);
							System.out.println(path);
						}
					}
				}
			}
			return fs;
		}
	}
}
