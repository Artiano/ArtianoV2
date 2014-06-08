/**
 * CholeskyDecomposition.java
 */
package artiano.linalg.decomposition;

import artiano.core.structure.Matrix;


/**
 * <p>Description: Cholesky decomposition, this class for find the inversion of a matrix or solve the linear equation 
 * system like <b>A*x=b</b> while the matrix is symmetric and positive-definite, this method if very efficient, and it is
 * twice as fast as LU-decomposition. </p>
 * <li>Any symmetric and positive-definite matrix <b>A</b> can be decomposed as: <b>A=L*LT</b>, <b>L</b> is lower triangular
 * matrix. So, we can get the inversion of <b>A</b> from <b>L</b> conveniently.
 * <li>To use the class, you should write code like:
 * <blockquote>
 * <pre>
 * double[] data={4,3,3,4};
 * Matrix A = new Matrix(2,2,data);
 * CholeskyDecomposition cd = new CholeskyDecomposition(A); //assume don't reserve A
 * if (cd.isDefinite()){
 *     Matrix inv = cd.inverse(); //get inversion of A
 *     double[] d = {7,7};
 *     Matrix B = new Matrix(2,1,d);
 *     Matrix sol = cd.solve(B); //assume don't reserve A
 * }
 * </pre>
 * </blockquote>
 * <li><i><b>NOTICE:</b></i> This program refer to the book Numerical Recipes, you can read the program at this book, there are detailed
 * description about Cholesky decomposition.</li>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-18
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class CholeskyDecomposition {
	
	protected Matrix a = null;
	protected double[] p = null;
	protected boolean isDef = true;
	
	/**
	 * constructor
	 * @param a - coefficient matrix
	 * @param reserve - to indicate reserve A whether or not
	 */
	public CholeskyDecomposition(Matrix a, boolean reserve){
		if (a.rows() != a.columns())
			throw new IllegalArgumentException("Accept square matrix only.");
		if (reserve)
			this.a = a.clone();
		else
			this.a = a;
		p = new double[a.rows()];
		decompose();
	}
	
	/* (non-Javadoc)
	 * @see artiano.math.linearalgebra.CholeskyDecomposition#CholeskyDecomposition(double[][], boolean)
	 */
	public CholeskyDecomposition(Matrix a){
		this(a, false);
	}
	
	protected void clearJagg(Matrix x){
		for (int i = 0; i < x.rows() - 1; i++)
			for (int j = i + 1; j < x.columns(); j++)
				x.set(i, j, 0);
	}
	
	/**
	 * inversion of L of a been decomposed
	 * @param reserveA - indicate reserve a whether or not
	 * @return - inversion of L
	 */
	public Matrix inverseOfL(boolean reserveA){
		Matrix inv = reserveA? a.clone(): a;
		double sum = 0.;
		for (int i = 0; i < a.rows(); i++){
			inv.set(i, i, 1./p[i]);
			for (int j = i+1; j < a.rows(); j++){
				sum = 0.;
				for (int k = i; k<j; k++)
					sum -= inv.at(j,k)*inv.at(k,i);
				inv.set(j, i, sum/p[j]);
			}
		}
		clearJagg(inv);
		return inv;
	}
	
	/**
	 * inversion of L of a been decomposed, will reserve a
	 * @return - inversion of L
	 */
	public Matrix inverseOfL(){
		return inverseOfL(true);
	}
	
	/**
	 * get inversion of the coefficient matrix A
	 * @return - inversion matrix
	 */
	public Matrix inverse(){
		//unit matrix;
		Matrix I = Matrix.unit(a.rows(), 1);
		return solve(I);
	}
	
	/**
	 * judge if the matrix A is positive-definite
	 * @return - true if is positive-definite or false otherwise
	 */
	public boolean isDefinite(){
		return isDef;
	}
	
	/**
	 * decompose the coefficient matrix to the form like L*L' = A
	 */
	protected void decompose(){
		double sum = 0.;
		int i, j, k;
		
		int n = a.rows();
		for (i = 0; i < n; i++){
			for (j = i; j < n; j++){
				for (sum = a.at(i,j), k = i - 1; k >= 0; k--) sum -= a.at(i,k) * a.at(j, k);
				if (i == j){
					//non-positive-definite
					if (sum <= 0.){
						isDef = false;
						return;
					}
					p[i] = Math.sqrt(sum);
				}else 
					a.set(j, i, sum / p[i]);
			}
		}
	}
	
	/**
	 * solve the matrix equation like A*x = B, B is row vector
	 * @param b - constant matrix
	 * @return - solution matrix
	 */
	public Matrix solve(Matrix b){
		if (b.rows() != a.rows())
			throw new IllegalArgumentException("Cholesky decomposition solve, Size not match.");
		if (!isDef)
			throw new UnsupportedOperationException("Cholesky decompositon, matrix is not positive-definite.");
		
		int i, k;
		double sum = 0.;
		Matrix x = new Matrix(b.rows(), b.columns());
		
		int n = a.rows();
		int m = b.columns();
		for (int j = 0; j < m; j++){
			for (i = 0; i < n; i++){
				for (sum = b.at(i, j), k = i - 1; k >= 0; k--) sum -= a.at(i,k) * x.at(k, j);
				x.set(i, j, sum / p[i]);
			}
			for (i = n - 1; i >= 0; i--){
				for (sum = x.at(i, j), k = i + 1; k < n; k++) sum -= a.at(k, i) * x.at(k, j);
				x.set(i, j, sum / p[i]);
			}
		}
		return x;
	}
}
