package artiano.statistics.distance.test;

import artiano.core.structure.Matrix;
import artiano.statistics.distance.EuclideanDistance;

public class EuclideanDistanceTest {

	public static void main(String[] args) {
		double[] arr_1 = new double[]{3, 5};
		Matrix mat_a = new Matrix(2, 1, arr_1);
		
		double[] arr_2 = new double[]{2, 4};
		Matrix mat_b = new Matrix(2, 1, arr_2);
		
		System.out.println("distance: " + 
				new EuclideanDistance().calculate(mat_a, mat_b));
	}

}
