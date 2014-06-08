/**
 * QRDecomposition.java
 */
package artiano.linalg.decomposition;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-9-7
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class QRDecomposition {
	
	protected Matrix a = null;
	protected Matrix c = null;
	protected Matrix d = null;
	boolean singular = false;
	
	/**
	 * constructor
	 * @param a - matrix to decompose
	 */
	public QRDecomposition(Matrix a){
		this(a, false);
	}
	
	/**
	 * constructor
	 * @param a - matrix to decompose
	 * @param reserve - indicate reserve a whether or not
	 */
	public QRDecomposition(Matrix a, boolean reserve){
		if (a.rows() != a.columns())
			throw new IllegalArgumentException("QRDecomposition, accept square matrix only.");
		this.a = reserve? a.clone(): a;
		c = new Matrix(a.rows(), 1);
		d = new Matrix(a.rows(), 1);
		decompose();
	}
	
	/**
	 * decompose the matrix
	 */
	protected void decompose(){
		//for record index
		int i, j, k;
		double scale, sigma, sum, tau;
		
		int n = a.rows();
		for (k = 0; k < n - 1; k++){
			scale = 0.;
			for (i = k; i < n; i++) scale = Math.max(scale, Math.abs(a.at(i, k)));
			if (scale == 0.){
				singular = true;
				c.set(k, 0.);
				d.set(k, 0.);
			} else {
				for (i = k; i < n; i++) a.divide(i, k, scale);
				for (sum = 0., i = k; i < n; i++) sum += Math.pow(a.at(i, k), 2);
				sigma = sign(Math.sqrt(sum), a.at(k, k));
				a.plus(k, k, sigma);
				c.set(k, sigma*a.at(k, k));
				d.set(k, -scale*sigma);
				for (j = k+1; j<n; j++){
					for (sum = 0., i = k; i<n; i++) sum += a.at(i, k)*a.at(i, j);
					tau = sum/c.at(k);
					for (i=k; i<n; i++) a.minus(i, j, tau*a.at(i, k));
				}
			}
		}
		d.set(n-1, a.at(n-1, n-1));
		if (d.at(n-1) == 0.) singular = true;	
	}
	
	/**
	 * sign function of a with b
	 * @param a
	 * @param b
	 * @return sign function value
	 */
	private double sign(final double a, final double b){
		return b >= 0 ? (a >= 0 ? a: -a): (a >= 0 ? -a: a);
	}
	
	/**
	 * inversion of the matrix
	 * @return - inversion
	 */
	public Matrix inverse(){
		Matrix I = Matrix.unit(a.rows());
		return solve(I);
	}
	
	/**
	 * solve the matrix equation like A*x=B.
	 * @param B - constant matrix
	 * @return - result
	 */
	public Matrix solve(Matrix B){
		return solve(B, false);
	}
	
	/**
	 * solve the matrix equation like A*x=B.
	 * @param B - constant matrix
	 * @param reserve - indicate reserve B whether or not
	 * @return
	 */
	public Matrix solve(Matrix B, boolean reserve){
		if (B.rows() != a.rows())
			throw new IllegalArgumentException("QRDecomposition solve, size not match.");
		Matrix b = reserve? B.clone(): B;
		double sum, tau;
		int n = a.rows();
		for (int k=0; k<b.columns(); k++){
			for (int j = 0; j<n-1; j++){
				sum = 0.;
				for (int i = j; i<n; i++) sum += a.at(i, j)*b.at(i, k);
				tau = sum/c.at(j);
				for (int i = j; i<n; i++) b.minus(i, k, tau*a.at(i, j));
			}
		}
		return solveR(b);
	}
	
	/**
	 * solve the matrix equation like R*x=B, that R is the decomposition of a
	 * @param B - constant matrix
	 * @return - result
	 */
	public Matrix solveR(Matrix B){
		return solveR(B, false);
	}
	
	/**
	 * solve the matrix equation like R*x=B, that R is the decomposition of a
	 * @param B - constant matrix
	 * @param reserve - indicate reserve B whether or not
	 * @return - result
	 */
	public Matrix solveR(Matrix B, boolean reserve){
		if (a.rows() != B.rows())
			throw new IllegalArgumentException("QRDecomposition solveR, size not match.");
		Matrix b = reserve? B.clone(): B;
		double sum;
		int n = a.rows();
		
		for (int k = 0; k < b.columns(); k++){
			b.divide(n-1, k, d.at(n-1));
			for (int i = n-2; i>=0; i--){
				sum = 0.;
				for (int j = i+1; j<n; j++) sum += a.at(i, j)*b.at(j, k);
				b.set(i, k, (b.at(i, k)-sum)/d.at(i));
			}
		}
		return b;
	}
	
}
