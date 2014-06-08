/**
 * LUDecomposition.java
 */
package artiano.linalg.decomposition;

import artiano.core.structure.Matrix;

/**
 * <p>Description: solve matrix equation like A*x=b.</p>
 * <p>This class for solve the matrix equation like A*x = B, x is the solution. You can write the code
 * like this:</p>
 * <code><br>//A*x = B</br>
 * <br>Matrix A = {{...},{...},...};</br>
 * <br>Matrix b = {{...},{...},...};</br>
 * <br>LUDecomposition decomposition = new LUDecompostion(A, false); //assume don't reserve</br>
 * <br>Matrix solution = decomposition.solve(b); //solve the equation, assume don't reserve</br>
 * <br>Matrix inversion = decompostion.inverse(); //get the inversion of A</br>
 * ...</code>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-18
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0
 */
public class LUDecomposition {
	//coefficient matrix
	private Matrix a = null;
	//index
	private int[] indx = null;
	//to record the number of swapping times
	private double d = 0.;
	
	/**
	 * constructor
	 * @param a - the coefficient matrix
	 * @param reserve - to indicate reserve the A whether or not, if reserve is not true,
	 * the matrix A will be replaced with L*U (L*U = A).
	 */
	public LUDecomposition(Matrix a, boolean reserve){
		if (a.rows() != a.columns())
			throw new IllegalArgumentException("The class accept the square matrix only.");
		if (reserve)
			this.a = a.clone();
		else 
			this.a = a;
		indx = new int[a.rows()];
		decompose();
	}
	
	/* (non-Javadoc)
	 * @see artiano.math.linearalgebra.LUDecomposition#LUDecomposition(Matrix, boolean)
	 */
	public LUDecomposition(Matrix a){
		this(a, false);
	}
	
	/**
	 * solve the equation A*x=I, I is unit matrix and x is the inversion of A
	 * @return - the inversion of A
	 */
	public Matrix inverse(){
		//unit matrix;
		Matrix I = Matrix.unit(a.rows(), 1.);
		return solve(I, false);
	}
	
	/**
	 * calculate the determinant of the matrix
	 * @return - determinant of the matrix
	 */
	public double det(){
		double detvalue = 1.;
		for (int i = 0; i < a.rows(); i++)
			detvalue *= a.at(i, i);
		return detvalue;
	}
	
	/**
	 * decompose A the form like this: L*U = A, the L and U stored in a
	 */
	protected void decompose(){
		final double TINY = 1.e-20;
		//variables for count
		int i, imax = 0, j, k;
		//useful variable
		double big, dum,sum,temp;
		
		int n = a.rows();
		//vv contents of each row for storing the scale factor
		double[] vv = new double[n];
		d = 1.;
		
		for (i = 0; i < n; i++){
			big = 0.;
			for (j = 0; j < n; j++)
				if ((temp = Math.abs(a.at(i, j))) > big) big = temp;
			if (big == 0.)
				throw new IllegalArgumentException("LU decomposition, singular matrix.");
			//store the scale factor
			vv[i] = 1. / big;
		}
		
		for (j = 0; j < n; j++){
			for (i = 0; i < j; i++){
				sum = a.at(i, j);
				for (k = 0; k < i; k++) sum -= a.at(i, k) * a.at(k, j);
				a.set(i, j, sum);
			}
			//initialize the biggest element
			big = 0.;
			for (i = j; i < n; i++){
				sum = a.at(i, j);
				for (k = 0; k < j; k++) sum -= a.at(i, k) *a.at(k, j);
				a.set(i, j, sum);
				if ((dum = vv[i] * Math.abs(sum)) >= big){
					big = dum;
					imax = i;
				}
			}
			if (j != imax){
				//swap
				for (k = 0; k < n; k++){
					dum = a.at(imax, k);
					a.set(imax, k, a.at(j, k));
					a.set(j, k, dum);
				}
				//change the parity
				d = -d;
				vv[imax] = vv[j];
			}
			indx[j] = imax;
			//if matrix is singular, a[j][j] replaced with tiny
			if (a.at(j, j) == 0.) a.set(j, j, TINY);
			if (j != n - 1){
				dum = 1. / a.at(j, j);
				for (i = j + 1; i < n; i++) a.multiply(i, j, dum);
			}
		}
	}
	
	/**
	 * solve the equation like A*x=b, x is the solution matrix
	 * @param b - input matrix
	 * @return - solution
	 */
	public Matrix solve(Matrix b){
		return solve(b,false);
	}
	
	/**
	 * solve the equation like A*v = b, v is the solution matrix
	 * @param c - specified constant matrix
	 * @return - solution
	 */
	public Matrix solve(Matrix c, boolean reserve){
		if (c.rows() != a.rows())
			throw new IllegalArgumentException("LU solve, size not match.");
		Matrix b = reserve ? c.clone(): c;
		//variables for count
		int i, ii = 0, ip, j;
		double sum = 0;
		
		int n = a.rows();
		//walk through all column-vectors
		for (int k = 0; k < b.columns(); k++){
			for (i = 0; i < n; i++){
				ip = indx[i];
				sum = b.at(ip, k);
				b.set(ip, k, b.at(i, k));
				if (ii != 0)
					for (j = ii - 1; j < i; j++) sum -= a.at(i, j) * b.at(j, k);
				else if (sum != 0.)
					ii = i + 1;
				b.set(i, k, sum);
			}
			
			for (i = n - 1; i >= 0; i--){
				sum = b.at(i, k);
				for (j = i + 1; j < n; j++) sum -= a.at(i, j) * b.at(j, k);
				b.set(i, k, sum / a.at(i, i));
			}
		}
		return b;
	}
}
