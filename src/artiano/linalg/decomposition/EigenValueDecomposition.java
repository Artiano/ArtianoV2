/**
 * EigenValueDecomposition.java
 */
package artiano.linalg.decomposition;

import javax.management.RuntimeErrorException;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-9-7
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class EigenValueDecomposition {
	
	protected Matrix a = null;
	protected Matrix v= null;
	protected Matrix d = null;
	protected int numberOfIterations = 0;
	
	/**
	 * constructor
	 * @param sym - symmetric matrix want to be decomposed
	 */
	public EigenValueDecomposition(Matrix sym){
		this(sym, false);
	}
	
	/**
	 * constructor
	 * @param sym - symmetric matrix want to b decomposed
	 * @param reserve - to indicate reserve the matrix whether or not
	 */
	public EigenValueDecomposition(Matrix sym, boolean reserve){
		if (sym.rows() != sym.columns())
			throw new IllegalArgumentException("Eigen value decomposition, accept square matrix only.");
		this.a = reserve? sym.clone(): sym;
		d = new Matrix(1, sym.rows());
		v = Matrix.unit(sym.rows());
		decompose();
	}
	
	/**
	 * get eigen vectors
	 * @return - eigen vectors that been decomposed
	 */
	public Matrix V(){
		return this.v;
	}
	
	/**
	 * get eigen values
	 * @return - eigen values that been decomposed
	 */
	public Matrix W(){
		return this.d;
	}
	
	/**
	 * rotate the matrix
	 * @param a - the matrix want to be rotated
	 * @param s - value to be copied
	 * @param tau
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 */
	protected void rotate(Matrix a, double s, double tau, int i, int j, int k, int l){
		double g,h;
		g = a.at(i, j);
		h = a.at(k, l);
		a.set(i, j, g-s*(h+g*tau));
		a.set(k, l, h+s*(g-h*tau));
	}
	
	/**
	 * decompose the matrix
	 */
	protected void decompose(){
		int i,j,ip,iq;
		double tresh, theta, tau, t, sm, s, h, g, c;
		
		int n = d.columns();
		double[] b = new double[n];
		double[] z = new double[n];
		for (ip=0; ip<n; ip++){
			b[ip] = a.at(ip, ip);
			d.set(ip, a.at(ip, ip));
			z[ip] = 0.;
		}
		//begin iterations
		for (i=1; i<=50; i++){
			sm=0.;
			for (ip=0; ip<n-1; ip++){
				for (iq=ip+1; iq<n; iq++)
					sm+=Math.abs(a.at(ip, iq));
			}
			if (sm==0.)
				return;
			if (i<4)
				tresh=0.2*sm/(n*n);
			else
				tresh=0.;
			for (ip=0; ip<n-1; ip++){
				for (iq=ip+1; iq<n; iq++){
					g=100.*Math.abs(a.at(ip, iq));
					//rotate
					if (i>4 && (Math.abs(d.at(ip))+g) == Math.abs(d.at(ip)) &&
							(Math.abs(d.at(iq))+g) == Math.abs(d.at(iq)))
						a.set(ip, iq, 0.);
					else if (Math.abs(a.at(ip, iq)) > tresh){
						h=d.at(iq)-d.at(ip);
						if ((Math.abs(h)+g) == Math.abs(h))
							t=a.at(ip, iq)/h;
						else{
							theta=0.5*h/a.at(ip, iq);
							t=1./(Math.abs(theta)+Math.sqrt(1.+theta*theta));
							if (theta<0.) t = -t;
						}
						c=1./Math.sqrt(1+t*t);
						s=t*c;
						tau=s/(1.+c);
						h=t*a.at(ip, iq);
						z[ip] -= h;
						z[iq] += h;
						d.minus(0, ip, h);
						d.plus(0, iq, h);
						a.set(ip, iq, 0.);
						for (j=0; j<ip; j++)
							rotate(a, s, tau, j, ip, j, iq);
						for (j=ip+1; j<iq; j++)
							rotate(a, s, tau, ip, j, j, iq);
						for (j=iq+1; j<n; j++)
							rotate(a, s, tau, ip, j, iq, j);
						for (j=0; j<n; j++)
							rotate(v, s, tau, j, ip, j, iq);
						numberOfIterations++;
					}
				}
			}
			for (ip=0; ip<n; ip++){
				b[ip] += z[ip];
				d.set(ip, b[ip]);
				z[ip] = 0.;
			}
		}
		throw new RuntimeErrorException(new Error(), "Eigen value decomposition, no convergence in 50 iterations.");
	}
	
	/**
	 * sort the matrix
	 */
	public void sort(){
		double p;
		
		int n=d.columns();
		for (int i=0; i<n-1; i++){
			int k=i;
			p=d.at(k);
			for (int j=i; j<n; j++){
				if (d.at(j) >= p){
					k=j;
					p=d.at(k);
				}
			}
			if (k!=i){
				d.set(k, d.at(i));
				d.set(i, p);
				for (int j=0; j<n; j++){
					p=v.at(j, i);
					v.set(j, i, v.at(j, k));
					v.set(j, k, p);
				}
			}
		}
	}
	
}
