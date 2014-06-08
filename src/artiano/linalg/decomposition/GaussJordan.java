/**
 * GaussJordan.java
 */
package artiano.linalg.decomposition;

import artiano.core.structure.Matrix;

/**
 * <p>Description: Solve the matrix equation like A*x = B.</p>
 * <p>This class for solve the matrix equation like A*x = b, x is the solution. You can write the code
 * like this:</p>
 * <code><br>//A*x = B</br>
 * <br>double[][] A = {{...},{...},...};</br>
 * <br>double[][] b = {{...},{...},...};</br>
 * <br>GaussJordan g = new GaussJordan(A, B, false); //assume don't reserve</br>
 * <br>double[][] inversion = g.getInversion(); //get the inversion of A</br>
 * <br>double[][] solution = g.getSolution(); //get the solution</br>
 * ...</code>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-18
 * @author (latest modification by Nano.Michael)
 * @since 1.0
 */
public class GaussJordan {
	//matrix 
	private Matrix a;
	private Matrix b;
	
	/**
	 * constructor
	 * @param a - coefficient matrix
	 * @param b - constant matrix
	 * @param reserve - to indicate reserve the A and b whether or not, if reserve is not true,
	 * the matrix A will be replaced with the inversion of A, and the matrix B will replaced with
	 * the solution matrix
	 */
	public GaussJordan(Matrix a, Matrix b, boolean reserve){
		if (a.rows() != a.columns())
			throw new IllegalArgumentException("GaussJordan, the class accept the square matrix only.");
		if (a.rows() != b.rows())
			throw new IllegalArgumentException("GaussJordan, size of A and B not match.");
		if (reserve){
			this.a = a.clone();
			this.b = b.clone();
		}
		else {
			this.a = a;
			this.b = b;
		}
		solve();
	}
	
	/* (non-Javadoc)
	 * @see artiano.math.linearalgebra.GaussJordan#GaussJordan(double[][], double[][], boolean)
	 */
	public GaussJordan(Matrix a, Matrix b){
		this(a, b, false);
	}
	/**
	 * get the inversion of A
	 * @return the inversion of A
	 */
	public Matrix getInversion(){
		return this.a;
	}
	
	/**
	 * get the solution of the equation
	 * @return the solution
	 */
	public Matrix getSolution(){
		return this.b;
	}
	
	/**
	 * solve the equation like A*x = B
	 */
	protected void solve(){
		double dum; //temporary
		double big; //the biggest element
		double pivinv; //pivot element
		
		int n = a.rows();
		int m = b.columns();
		//for record the main element
		int[] colIdx = new int[n];
		int[] rowIdx = new int[n];
		int[] ipiv  = new int[n];
		//variables for count
		int i, icol = 0, irow = 0, j, k, l, ll;
		//initialize
		for (j = 0; j < n; j++)
			ipiv[j] = 0;
		
		//reduce columns
		for (i = 0; i <n; i++){
			big = 0.;
			//find the biggest element
			for (j = 0; j < n; j++)
				if (ipiv[j] != 1)
					for (k = 0; k < n; k++){
						if (ipiv[k] == 0){
							if (Math.abs(a.at(j, k)) >= big){
								big = Math.abs(a.at(j, k));
								irow = j;
								icol = k;
							}
						}
					}
			++(ipiv[icol]);
			//swap rows and columns
			if (irow != icol){
				for (l = 0; l < n; l++){
					double t = a.at(irow, l);
					a.set(irow, l, a.at(icol, l));
					a.set(icol, l, t);
				}
				for (l = 0; l < m; l++){
					double t = b.at(irow, l);
					b.set(irow, l, b.at(icol, l));
					b.set(icol, l, t);	
				}
			}
			rowIdx[i] = irow;
			colIdx[i] = icol;
			//if a is singular matrix
			if (a.at(icol, icol) == 0.0)
				throw new IllegalArgumentException("Gauss-Jordan, singular matrix.");
			pivinv = 1./a.at(icol, icol);
			a.set(icol, icol, 1.);
			for (l = 0; l < n; l++)
				a.set(icol, l, a.at(icol, l) * pivinv);
			for (l = 0; l < m; l++)
				b.set(icol, l, b.at(icol, l) * pivinv);	
			//reduce
			for (ll = 0; ll < n; ll++)
				if (ll != icol){
					dum = a.at(ll, icol);
					a.set(ll, icol, 0.);
					for (l = 0; l < n; l++)
						a.minus(ll, l, a.at(icol, l) * dum);
					for (l = 0; l < m; l++)
						b.minus(ll, l, b.at(icol, l) * dum);
				}
		}
		
		//swap, sort the matrix
		for (l = n - 1; l >= 0; l--){
			if (rowIdx[l] != colIdx[l])
				for (k = 0; k < n; k++){
					double t = a.at(k, rowIdx[l]);
					a.set(k, rowIdx[l], a.at(k, colIdx[l]));
					a.set(k, colIdx[l], t);
				}
		}
	}
}
