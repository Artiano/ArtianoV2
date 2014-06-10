package artiano.ml.regression.test;

import artiano.core.structure.Matrix;
import artiano.ml.regression.PolynomialRegression;

public class PolynomialRegressionTest {
	public static void main(String[] args) {
		PolynomialRegression regression = new PolynomialRegression();
		
		double[] xArr = new double[]{
			37.0, 37.5, 38.0, 38.5, 39.0, 39.5,
			40.0, 40.5, 41.0, 41.5, 42.0, 42.5, 43.0
		};
		Matrix x = new Matrix(xArr.length, 1, xArr);
		
		double[] yArr = new double[]{
			3.40, 3.00, 3.00, 2.27, 2.10, 1.83,
			1.57, 1.70, 1.80, 1.90, 2.35, 2.54, 2.90
		};
		Matrix y = new Matrix(yArr.length, 1, yArr);
		
		Matrix coeif = regression.fit(x, y);
		coeif.print();
	}
}
