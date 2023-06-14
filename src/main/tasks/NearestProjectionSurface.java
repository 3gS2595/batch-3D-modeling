package main.tasks;

import main.form.Form;
import main.pool.ThreadPool;
import main.tools.Ray;
import org.tinspin.index.qthypercube2.QEntry;
import org.tinspin.index.qthypercube2.QEntryDist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NearestProjectionSurface implements Task {
	private final ThreadPool pool;
	private final Form[] pairSpring;
	private final int i;
	private final int offIndex;

	private double[] bestPnt = null;
	private double bstDist = Double.MAX_VALUE;
	private List<double[]> surfPnts = null;


	public NearestProjectionSurface(int i, int offIndex, ThreadPool pool) {
		this.pool = pool;
		this.pairSpring = pool.pairSpring;
		this.i = i;
		this.offIndex = offIndex;
	}

	@Override
	public void run() {
		// retrieves nearest neighbor (nn
		Collection<QEntryDist<Object>> nearestNeighbor = pairSpring[1].KdTree.knnQuery(pairSpring[0].v.get(i), 1);
		double[] nearestV = ((QEntry)nearestNeighbor.toArray()[0]).point();

		// uses ray to probe surface
		Ray r = new Ray(i, pairSpring);
		if(r.findNearestTrisProjection(nearestV)) bestPnt = r.bestPnt;

		if(bestPnt != null) {
			// produces midpoint
			double ratio = (pairSpring[0].settings.ratio * bstDist) / bstDist;
			double[] midpoint = new double[pairSpring[0].v.get(i).length];
			for (int a = 0; a < pairSpring[0].v.get(i).length; a++) {
				midpoint[a] = (pairSpring[0].v.get(i)[a] + (ratio * (this.bestPnt[a] - pairSpring[0].v.get(i)[a])));
			}

			// inserts refreshed point
			pool.output.get(offIndex).newPoints.put(i, midpoint);
		} else { System.out.println("best point was null?");}
		// completion check
		if (pool.output.get(offIndex).newPoints.size() == pairSpring[0].v.size()) {
			List<double[]> newV = new ArrayList<>();
			for (int i = 0; i < pairSpring[0].v.size(); i++) {
				newV.add(pool.output.get(offIndex).newPoints.get(i));
			}
			pool.output.get(offIndex).v = new ArrayList<>(newV);
		}
	}
}
