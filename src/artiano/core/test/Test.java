/**
 * Test.java
 */
package artiano.core.test;

import artiano.core.operation.MatrixOpt;
import artiano.core.structure.Matrix;
import artiano.core.structure.Range;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-23
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Test {
	static double[] A =
	   {0,1,2,3,4,
		5,6,7,8,9,
		10,11,12,13,14,
		15,16,17,18,19
	   };
	
	static double[] B =
		{0,1,2,3,4,
		5,6,7,8,9,
		10,11,12,13,14,
		15,16,17,18,19
	   };
	
	static void printMatrix(Matrix m){
		System.out.println("-----------------------------");
		for (int i = 0; i < m.rows(); i++){
			for (int j = 0; j < m.columns(); j++)
				System.out.print(m.at(i, j) + " ");
			System.out.println();
		}
		System.out.println("-----------------------------");
	}
	
	static void testMatrix(){
		Matrix m = new Matrix(4, 5, A);
		printMatrix(m);
		Matrix q = m.at(new Range(1, 3), new Range(1, 4));
		printMatrix(q);
		Matrix z = q.at(Range.all(), new Range(0, 2));
		printMatrix(z);
		Matrix y = new Matrix(2, 2);
		z.copyTo(y);
		printMatrix(z);
		Matrix x = m.at(Range.all(), new Range(0, 3));
		printMatrix(x);
		printMatrix(m.row(2));
		printMatrix(m.column(2));
		m.minus(0, 0, 3);
		printMatrix(m);
		Matrix n = m.at(new Range(1, 3), new Range(2, 4));
		printMatrix(n);
		Matrix e = new Matrix(2, 2);
		for (int i = 0; i < n.rows(); i++)
			for (int j = 0; j < n.columns(); j++)
				n.set(i, j, 10 + i);
		printMatrix(n);
		for (int i = 0; i < e.rows(); i++)
			for (int j = 0; j < e.columns(); j++)
				e.set(i, j, i + 1 + j);
		printMatrix(e);
		printMatrix(e);
		double d[] = {1,2,3,4,5,6,7,8,9,0};
		Matrix x1 = new Matrix(5, 2, d);
		double dd[] = {-1,-2};
		x1.setRow(4, new Matrix(1, 2, dd));
		printMatrix(x1);
		Matrix x2 = new Matrix(5, 1, d);
		System.out.println(x2.at(0));
		x2.set(0, 100);
		System.out.println(x2.at(0));
	}
	
	public static void testMerge(){
		Matrix a = new Matrix(4, 4, A);
		Matrix b = new Matrix(4, 4, B);
		a.print();
		b.print();
		a.mergeAfterRow(b);
		a.print();
		Matrix y = a.at(Range.all(), new Range(0, 3));
		y.print();
	}
	
	public static void testMatrixOpt(){
		double[] d1 = {1,2,3,4};
		double[] d2 = {5,6,7,8};
		double[] d3 = {9,10,11,12};
		double[] d4 = {13,14,15,16};
		Matrix[] m = new Matrix[4];
		m[0] = new Matrix(1, 4, d1);
		m[1] = new Matrix(1, 4, d2);
		m[2] = new Matrix(1, 4, d3);
		m[3] = new Matrix(1, 4, d4);
		Matrix mean = MatrixOpt.computeMean(m);
		printMatrix(mean);
		Matrix cov = MatrixOpt.computeCovarianceByRow(m, mean, 1.);
		printMatrix(cov);
		Matrix cav = MatrixOpt.computeCovarianceByCol(m, mean, 1.);
		printMatrix(cav);
		/*Matrix cov = MatrixOpt.computeGeneralizedCovariance(m, mean, 1.);
		printMatrix(cov);*/
	}
	
	public static void main(String[] argStrings){
		//testMatrix();
		//testMatrixOpt();
		testMerge();
	}
}
