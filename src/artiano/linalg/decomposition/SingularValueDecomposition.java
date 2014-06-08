/**
 * SingularValueDecomposition.java
 */
package artiano.linalg.decomposition;

import javax.management.RuntimeErrorException;

import artiano.core.structure.Matrix;

/**
 * <p>Description: This class for singular value decomposition.</p>
 * <p>As we know, a M*N dimension matrix A can decompose into U*W*V', U is M*N dimensional, W is N*N dimensional, 
 *  and V is N*N dimension. And, U*U' = I, V*V' = I, I is unit matrix. To get singular vectors, you may write 
 * code like this:</p>
 * <code><br>double[][] A = {{...},{...},...,};</br>
 * <br>SingularValueDecomposition svd = new SingularValueDecomposition(A);</br>
 * <br>svd.sort(); //or you can sort it as descending order</br>
 * <br>double[][] u = svd.U();</br>
 * <br>double[][] w = svd.W();</br>
 * <br>double[][] v = svd.V();</br>
 * <br>...<br></code>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-19
 * @author (latest modification by Nano.Michael)
 * @function 
 * @since 1.0.0
 */
public class SingularValueDecomposition {
	//a=u*w*v'
	protected Matrix u = null;
	protected Matrix w = null;
	protected Matrix v = null;
	//indicate compute right singular vectors whether or not
	protected boolean computeRight = false;
	//pseudo-inversion of A (store it to speed up compute)
	protected Matrix pseudoInversion = null;
	
	/**
	 * constructor
	 * @param a - input matrix A
	 * @param reserve - to indicate reserve matrix a whether or not, if not reserve, a will be replaced with u.
	 * @param computeRight - to indicate compute right singular vectors whether or not. In most cases, we don't 
	 * need to compute it, that will speed up the compute.
	 */
	public SingularValueDecomposition(Matrix a,  boolean reserve, boolean computeRight){
		if (reserve)
			this.u = a.clone();
		else
			this.u = a;
		w = new Matrix(1, a.columns());
		this.computeRight = computeRight;
		if (computeRight)
			v = new Matrix(a.columns(), a.columns());
		decompose();
	}
	
	/*(non-Javadoc)
	 * @see artiano.math.linearalgebra.SingularValueDecomposition#SingularValueDecomposition(double[][], boolean, boolean)
	 */
	public SingularValueDecomposition(Matrix a, boolean computeRight){
		this(a, false, computeRight);
	}
	
	/*(non-Javadoc)
	 * @see artiano.math.linearalgebra.SingularValueDecomposition#SingularValueDecomposition(double[][], boolean, boolean)
	 */
	public SingularValueDecomposition(Matrix a){
		this(a, true);
	}
	
	/**
	 * get the left singular vectors
	 * @return - left singular vectors
	 */
	public Matrix U(){
		return this.u;
	}
	
	/**
	 * get the right singular vectors, if supported.
	 * @return - right singular vectors
	 */
	public Matrix V(){
		if (!computeRight)
			throw new UnsupportedOperationException("Singular value decompsition, right singular vectors not computed.");
		return this.v;
	}
	
	/**
	 * get the singular value vector, the diagonal matrix saved as a vector like w[w1,w2....wn]
	 * @return - singular value vector
	 */
	public Matrix W(){
		return this.w;
	}
	
	/**
	 * calculate the (pseudo-)inversion of matrix A
	 * @return - pseudo-inversion
	 */
	public Matrix pseudoInverse(){
		//if pseudo-inversion has been computed previously
		if (pseudoInversion != null)
			return pseudoInversion;
		//if not supported
		if (!computeRight)
			throw new UnsupportedOperationException("Pseudo-inverse, right singular vectors not computed.");
		Matrix t = new Matrix(u.columns(), u.rows());
		//X=(W^)*(U')
		for (int i = 0; i < u.columns(); i++){
			for (int j = 0; j < u.rows(); j++)
				t.set(i, j, (w.at(0, i) == 0? 0: 1./w.at(0, i)*u.at(j, i)));
		}
		//X=V*X
		pseudoInversion = v.multiply(t);
		return pseudoInversion;
	}
	
	/**
	 * solve the matrix equation like A*x=b
	 * @param b - constant matrix
	 * @return - solution matrix
	 */
	public Matrix solve(Matrix b){
		if (u.rows() != b.rows())
			throw new IllegalArgumentException("Singular value decomposition solve, size not match.");
		if (pseudoInversion == null)
			pseudoInverse();
		return pseudoInversion.multiply(b);
	}
	
	/**
	 * sort the matrix in descending order with w as the basis
	 */
	public void sort(){
		for (int i = 0; i < w.columns() - 1; i++){
			double maxW = w.at(0, i);
			int maxIdx = i;
			for (int j = i + 1; j < w.columns(); j++){
				if (maxW < w.at(0, j)){
					maxW = w.at(0, j);
					maxIdx = j;
				}
			}
			//sort w
			w.set(0, maxIdx, w.at(0, i));
			w.set(0, i, maxW);
			//sort v if needed
			if (computeRight){
				for (int k = 0; k < v.columns(); k++){
					double t = v.at(k, maxIdx);
					v.set(k,maxIdx, v.at(k, i));
					v.set(k, i, t);
				}
			}
			//sort u
			for (int k = 0; k < u.columns(); k++){
				double t = u.at(k, maxIdx);
				u.set(k, maxIdx, u.at(k, i));
				u.set(k, i, t);
			}
		}
	}
	
	/**
	 * sign function of a with b
	 * @param a
	 * @param b
	 * @return sign function value
	 */
	protected double sign(final double a, final double b){
		return b >= 0 ? (a >= 0 ? a: -a): (a >= 0 ? -a: a);
	}
	
	/**
	 * compute (a^2 + b^2)^0.5 = abs(a)*(1 + (b/a)^2)^0.5, in this form, will not destroy overflow or underflow
	 * @param a
	 * @param b
	 * @return - the function value
	 */
	protected double pythag(final double a, final double b){
		double absa, absb;
		absa = Math.abs(a);
		absb = Math.abs(b);
		if (absa > absb) return absa*Math.sqrt(1.+(absb/absa)*(absb/absa));
		else return (absb==0.?0.:absb*Math.sqrt(1.+(absa/absb)*(absa/absb)));
	}
	
	/**
	 * decompose the matrix
	 */
	protected void decompose(){
		boolean flag;
		int i, its, j, jj, k, l = 0, nm = 0;
		double anorm, c, f, g, h, s, scale, x, y, z;
		
		int m = u.rows();
		int n = u.columns();
		double[] rv1 = new double[n];
		
		//householder approximate into double diagonal form
		g = scale = anorm = 0.;
		for (i = 0; i < n; i++){
			l = i + 2;
			rv1[i] = scale * g;
			g = s = scale = 0.;
			if (i < m){
				for (k = i; k < m; k++) scale += Math.abs(u.at(k, i));
				if (scale != 0.){
					for (k = i; k < m; k++){
						u.divide(k, i, scale);
						s += u.at(k, i) * u.at(k, i);
					}
					f = u.at(i, i);
					g = -sign(Math.sqrt(s), f);
					h = f * g - s;
					u.set(i, i, f-g);
					for (j = l - 1; j < n; j++){
						for (s = 0., k = i; k < m; k++) s += u.at(k, i) * u.at(k, j);
						f = s / h;
						for (k = i; k < m; k++) u.plus(k, j, f * u.at(k, i));
					}
					for (k = i; k < m; k++) u.multiply(k, i, scale);
				}
			}
			w.set(0, i, scale*g);
			g = s = scale = 0.;
			if (i + 1 <= m && i != n){
				for (k = l - 1; k < n; k++) scale += Math.abs(u.at(i, k));
				if (scale != 0.){
					for (k = l - 1; k < n; k++){
						u.divide(i, k, scale);
						s += u.at(i, k) * u.at(i, k);
					}
					f = u.at(i, l-1);
					g = -sign(Math.sqrt(s), f);
					h = f * g - s;
					u.set(i, l-1, f-g);
					for (k = l - 1; k < n; k++) rv1[k] = u.at(i, k) / h;
					for (j = l - 1; j < m; j++) {
						for (s = 0., k = l - 1; k < n; k++) s += u.at(j, k) * u.at(i, k);
						for (k = l - 1; k < n; k++) u.plus(j,k,s*rv1[k]);
					}
					for (k = l - 1; k < n; k++) u.multiply(i, k, scale);
				}
			}
			anorm = Math.max(anorm, Math.abs(w.at(0, i)) + Math.abs(rv1[i]));
		}
		
		//find accumulate sum of right singular vectors (if need to compute)
		if (computeRight){
			for (i = n - 1; i >= 0; i--){
				if (i < n - 1){
					if (g != 0.){
						for (j = l; j < n; j++)
							v.set(j, i, u.at(i, j)/u.at(i, l)/g);
						for (j = l; j < n; j++){
							for (s = 0., k = l; k < n; k++) s += u.at(i, k) * v.at(k, j);
							for (k = l; k < n; k++) v.plus(k, j, s*v.at(k, i));
						}
					}
					for (j = l; j < n; j++){
						v.set(i, j, 0.);
						v.set(j, i, 0.);
					}
				}
				v.set(i, i, 1.);
				g = rv1[i];
				l = i;
			}
		}
		
		//find accumulate sum of left singular vectors
		for (i = Math.min(m, n) - 1; i >= 0; i--){
			l = i + 1;
			g = w.at(0, i);
			for (j = l; j < n; j++) u.set(i, j, 0.);
			if (g != 0.){
				g = 1. / g;
				for (j = l; j < n; j++){
					for (s = 0., k = l; k < m; k++) s += u.at(k, i) * u.at(k, j);
					f = (s / u.at(i, i)) * g;
					for ( k = i; k < m; k++) u.plus(k,j,f*u.at(k, i));
				}
				for (j = i; j < m; j++) u.multiply(j, i, g);
			}else for (j = i; j < m; j++) u.set(j, i, 0.);
			u.plus(i,i,1.);
		}
		
		//diagonal
		for (k = n - 1; k >= 0; k--){
			for (its = 0; its < 30; its++){
				flag = true;
				for (l = k; l >= 0; l--){
					nm = l - 1;
					if (Math.abs(rv1[l]) + anorm == anorm){
						flag = false;
						break;
					}
					if (Math.abs(w.at(0, nm)) + anorm == anorm) break;
				}
				if (flag){
					c = 0.;
					s = 1.;
					for (i = l - 1; i < k + 1; i++){
						f = s * rv1[i];
						rv1[i] = c * rv1[i];
						if (Math.abs(f) + anorm == anorm) break;
						g = w.at(0, i);
						h = pythag(f, g);
						w.set(0, i, h);
						h = 1. / h;
						c = g * h;
						s = -f * h;
						for (j = 0; j < m; j++){
							y = u.at(j, nm);
							z = u.at(j, i);
							u.set(j, nm, y*c+z*s);
							u.set(j, i, z*c-y*s);
						}
					}
				}
				z = w.at(0, k);
				if (l == k){
					if (z < 0.){
						w.set(0, k, -z);
						//if need to compute right singular vectors
						if (computeRight)
							for (j = 0; j < n; j++) v.set(j, k, -v.at(j, k));
					}
					break;
				}
				if (its == 29)
					throw new RuntimeErrorException(new Error(), "Singular value decompose, no convergence in 30 iterations.");
				x = w.at(0, l);
				nm = k - 1;
				y = w.at(0, nm);
				g = rv1[nm];
				h = rv1[k];
				f = ((y-z)*(y+z)+(g-h)*(g+h)) / (2.*h*y);
				g = pythag(f, 1.);
				f = ((x-z)*(x+z)+h*((y/(f+sign(g, f)))-h))/x;
				c = s = 1.;
				for (j = l; j <= nm; j++){
					i = j + 1;
					g = rv1[i];
					y = w.at(0, i);
					h = s*g;
					g = c*g;
					z = pythag(f, h);
					rv1[j] = z;
					c = f/z;
					s = h/z;
					f = x*c+g*s;
					g = g*c-x*s;
					h = y*s;
					y *= c;
					//if need to compute right singular vectors
					if (computeRight){
						for (jj = 0; jj < n; jj++){
							x = v.at(jj, j);
							z = v.at(jj, i);
							v.set(jj, j, x*c+z*s);
							v.set(jj, i, z*c-x*s);
						}
					}
					z = pythag(f, h);
					w.set(0, j, z);
					if (z != 0){
						z = 1./z;
						c = f*z;
						s = h*z;
					}
					f = c*g+s*y;
					x = c*y-s*g;
					for (jj = 0; jj < m; jj++){
						y = u.at(jj, j);
						z = u.at(jj, i);
						u.set(jj, j, y*c+z*s);
						u.set(jj, i, z*c-y*s);
					}
				}
				rv1[l] = 0.;
				rv1[k] = f;
				w.set(0, k, x);
			}
		}
	}
}
