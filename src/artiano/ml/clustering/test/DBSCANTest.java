package artiano.ml.clustering.test;

import java.util.List;

import org.junit.Test;

import artiano.core.structure.Matrix;
import artiano.ml.clustering.DBSCAN;

public class DBSCANTest {

	@Test
	public void testCluster() {
		int dimension = 2;
		double[] dataPointsArr = new double[]{
			-1, 0,   1, 2,   2, 0,   2, 2,
			2,  3,   3, 1,   5, 3,   6, 1,
			6, 2 ,   6, 5,   7, 1,   7, 3,    
			7, 5,    8, 3,   9, 3,   11, 1
		}; 
		Matrix dataPoints = 
			new Matrix(dataPointsArr.length/dimension, dimension, dataPointsArr);
		double eps = 2.0;
		int minNeighborsNum = 4;
		DBSCAN dbscan = new DBSCAN(eps, minNeighborsNum, dataPoints);
		List<Matrix> clusterList = dbscan.cluster();
		System.out.println("所有的簇如下所示:");
		int i = 0;
		for(Matrix cluster: clusterList) {
			System.out.println("簇 " + (i+1));
			cluster.print();
			i++;
		}
		
		List<Matrix> noisePointList = dbscan.getNoisePoints();
		System.out.println("被划分为噪声点的点如下:");
		for(Matrix noisePoint: noisePointList) {
			noisePoint.printAll();
		}
	}

}
