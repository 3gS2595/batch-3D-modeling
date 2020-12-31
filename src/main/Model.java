package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Model {

	int id;
	String OBJname;
	String MTLname;
	public List<double[]>   verts = new ArrayList<>();
	public List<double[][]> polys = new ArrayList<>();
	public List<Material>    mats = new ArrayList<>();
	public List<String>  rawPolys = new ArrayList<>();

	public HashMap<Double, String> matUsed = new HashMap<>();
	public static List<double[][]> comp = new ArrayList<>();
	public static HashMap<double[], ArrayList<double[][]>> map = new HashMap<>();

	public Model(String filepath, int id){
		this.OBJname = "./input/" + filepath;
		this.id = id;
		String inputHack = "model 0.0 0.0 0.0 0 1.0 0 0 0 0 " + this.OBJname;
		Transform.trans(inputHack, (this.id + ""));
		intake();
	}

	// Handles .obj first and then .mtl
	public void intake(){
		try {
			BufferedReader reader;
			String line;

		// OBJ
			if (this.OBJname != null) {

				//adds oen to offset from 0 as to keep indices correct
				this.verts.add(new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE});

				// a dirty fucking way to add the id tag inside of the
				// full path just before the actual obj filename appears
				// jesus fucking christ
				String[] temp = this.OBJname.split("/");

				// ADD ID HERE TO GET THE TRANSFORM TO WORK
				temp[2] = this.id + "_" + temp[2];
//				temp[2] = temp[2];
				String path = "/" + temp[1] + "/" + temp[2];
				String dirPath = new File(".").getCanonicalPath() + path;

				System.out.println(dirPath);
				reader = new BufferedReader(new FileReader(dirPath));
				double[] matID = new double[]{0.0, 0.0};
				while ((line = reader.readLine()) != null) {
					String[] raw = line.split(" ");
					double[] num = new double[raw.length];

					// Skips comments
					if (!raw[0].equals("#")) {

						// Converts raw string into doubles
						for (int i = 1; i < raw.length; i++) {
							try { num[i] = Double.parseDouble(raw[i]);
							} catch (Exception ignored) { }
						}

						switch (raw[0]) {
							case "mtllib":
								this.MTLname = raw[1];
								break;

							case "v":
								this.verts.add(new double[]{num[1], num[2], num[3], matID[0]});
								map.put(new double[]{num[1], num[2], num[3], matID[0]}, null);
								break;

							case "f":
								rawPolys.add(line);
								int[] one = new int[]{
										Integer.parseInt(raw[1].split("//")[0]),
										Integer.parseInt(raw[2].split("//")[0]),
										Integer.parseInt(raw[3].split("//")[0])
								};
								double[][] temps = new double[][]{
										this.verts.get(one[0]).clone(),
										this.verts.get(one[1]).clone(),
										this.verts.get(one[2]).clone(),
										matID.clone()
								};
								comp.add(temps);
								this.polys.add(temps);
								break;

							case "usemtl":
								matID[0] = matID[0] + 1;
								this.matUsed.put(matID[0], raw[1]);
								break;
						}
					}
				}
			}

		// MTL
			if(this.MTLname != null) {
				String[] temp = OBJname.split("/");
				String MTLFilePath = temp[0] + "/" + temp[1] + "/" + MTLname;
				reader = new BufferedReader(new FileReader(MTLFilePath));
				Material m = new Material();
				while ((line = reader.readLine()) != null) {
					String[] raw = line.split(" ");
					double[] num = new double[raw.length];

					// Skips comments
					if (!raw[0].equals("#")) {

						// Converts raw string into doubles
						for (int i = 1; i < raw.length; i++) {
							try { num[i] = Double.parseDouble(raw[i]);
							} catch (Exception ignored) {}
						}

						// Intake
						switch (raw[0]) {
							case "newmtl":
								// .mtl data intake entrance
								m = new Material();
								m.name = raw[1];
								break;

							case "Ns": m.alpha = num[1];break;
							case "Ka": m.ka = new double[]{num[1], num[2], num[3]};break;
							case "Kd": m.kd = new double[]{num[1], num[2], num[3]};break;
							case "Ks": m.ks = new double[]{num[1], num[2], num[3]};break;
							case "Tr": m.tr = new double[]{num[1], num[2], num[3]};break;
							case "Ni": m.ni = num[1];break;

							case "illum":
								if (num[1] == 3){ m.kr = m.ks;}
								else{ m.kr = new double[]{0.0, 0.0, 0.0};}

								// LOAD COMPLETE CHECK
								if(m.name != null && m.ka != null && m.kd != null && m.ks != null) {
									this.mats.add(m);
								} else System.out.println("MAT LOAD FAILURE");
								break;
						}
					}
				}
			}
		} catch (IOException e) { e.printStackTrace(); }

//		clean up
		// a dirty fucking way to add the id tag inside of the
		// full path just before the actual obj filename appears
		// jesus fucking christ
		String[] temp = this.OBJname.split("/");
		temp[2] = this.id + "_" + temp[2];
		String path = "/" + temp[1] + "/" + temp[2];
		String dirPath = null;
		try {
			dirPath = new File(".").getCanonicalPath() + path;
		} catch (IOException e) {
			e.printStackTrace();
		}

//		File file = new File(dirPath);
//		if( !(file.delete())){
//			System.out.println("FAILED TO DELETE OBJ TRANSFORM FILE");
//		}

		System.out.println(this.id + ": vertices: " + verts.size());

	}

}