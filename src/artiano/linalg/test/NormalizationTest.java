package artiano.linalg.test;

import artiano.core.structure.Matrix;
import artiano.linalg.basic.Normalization;

public class NormalizationTest {

	public static void main(String[] args) {
		double[] dataArr = new double[]{
			5, 4, 6, 2,
			2, 3, 7, 4,
			3, 6, 4, 2,
			4, 5, 6, 7
		};
		Matrix dataset = new Matrix(4, 4, dataArr);
		System.out.println("Before norm:");
		dataset.print();
		
		Normalization norm = new Normalization();
		
		Matrix norm_1 = norm.colNorm0_1(dataset);
		System.out.println("Column norm to [0,1]:");
		norm_1.print();
		
		Matrix norm_2 = norm.colNormNeg1_1(dataset);
		System.out.println("Column norm to [-1,1]:");
		norm_2.print();
		
		Matrix norm_3 = norm.rowNorm0_1(dataset);
		System.out.println("Row norm to [0,1]:");
		norm_3.print();
		
		Matrix norm_4 = norm.rowNormNeg1_1(dataset);
		System.out.println("Row norm to [-1,1]:");
		norm_4.print();
		
	}

}
