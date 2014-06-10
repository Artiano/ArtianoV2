package artiano.linalg.test;

import artiano.core.structure.Matrix;
import artiano.linalg.basic.BasicLinalgProcession;

public class BasicLinalgProcessionTest {

	public static void main(String[] args) {	
		testDetCalc();
	}

	private static void testDetCalc() {
		double[] a = {
				1, -1, 3, 
				-1, 0, -2, 
				2, 2, 4
		};
		Matrix mat = new Matrix(3, 3, a);
		
		BasicLinalgProcession basicPro = new BasicLinalgProcession();
		System.out.println("det: " + basicPro.det(mat));
	}

}
