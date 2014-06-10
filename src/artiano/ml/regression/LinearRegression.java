/**
 * LinearRegression.java
 */
package artiano.ml.regression;

import artiano.core.structure.Matrix;
import artiano.linalg.decomposition.CholeskyDecomposition;

/**
 * <p>使用最小二乘方法生成一个线性模型。
 * <ul>
 * <li>由x数据和y数据拟合出参数模型 'w0', 'w1', 'w2', ..., 'wk'。
 * 对线性方程组 y = w0 + w1 * x1 + w2 * x2 + ... + wk * xk,
 * 线性回归试图根据已有数据生成一个参数模型matrix。
 * 类由方法{@link #fit(Matrix, Matrix)}拟合出参数矩阵matrix，有：
 * matrix.at(0,0) --> w0, matrix.at(0, 1) --> w1, .., matrix.at(0,k) --> wk.</li>
 * </ul></p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-21
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class LinearRegression extends Regression {
	
	/* (non-Javadoc)
	 * @see artiano.ml.regression.Regression#fit(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix fit(Matrix XData, Matrix YData) {
		Matrix coefficients = generateCoefficientMatrix(XData);
    	/* Solve AW=b to coefficients of the linear regression Polynomial */
    	Matrix A = genearteLeftHandSide(coefficients);
    	Matrix b = generateRightHandSide(YData, coefficients);
    	CholeskyDecomposition decomposition = new CholeskyDecomposition(A);
    	return decomposition.solve(b);
	}
	
	private static Matrix generateRightHandSide(Matrix right_hand, 
			Matrix coefficients) {
		int cols = coefficients.columns() - 1;
		Matrix b = new Matrix(cols + 1, 1);
		b = coefficients.t().multiply(right_hand);
		return b;
	}

	private static Matrix genearteLeftHandSide(Matrix coefficients) {
		int size = coefficients.columns();
		Matrix a = new Matrix(size, size);
		a = coefficients.t().multiply(coefficients);
		return a;
	}

	private static Matrix generateCoefficientMatrix(Matrix left_hand) {
		Matrix coefficients = 
			new Matrix(left_hand.rows(), left_hand.columns() + 1);
    	for(int i=0; i<coefficients.rows(); i++) {
    		for(int j=0; j<coefficients.columns(); j++) {
    			if(j == 0) {
    				coefficients.set(i, j, 1);
    			} else {
    				coefficients.set(i, j, left_hand.at(i, j - 1));
    			}
    		}
    	}
		return coefficients;
	}
	
}
