/**
 * MatrixOpt.java
 */
package artiano.core.operation;

import artiano.core.structure.Matrix;

/**
 * <p>Operation on matrix.</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-25
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class MatrixOpt {
	
	/**
	 * Normalize the matrices by minimum element and maximum element of the matrix on the matrices.
	 * <p>Given matrices A=Ai, i=0,1,2,...,N, the step of the method is:
	 * <li>step 1. Find the minimum and maximum element of A, we get two new matrix so-called
	 * min and max. min(i,j)=minimum[A1(i,j),A2(i,j),...,AN(i,j)], max(i,j)=maximum[A1(i,j),A2(i,j),...,AN(i,j)], 
	 * i means the row index of A, and j means the column index of A.</li>
	 * <li>step 2. Normalize A. Ai(j,k)=2*[(Ai(j,k)-min(j,k))/(max(j,k)-min(j,k))]-1.</li></p>
	 * @param src A set of matrix want to be normalized.
	 * @param reserve Indicate replace the original matrix whether or not, if parameter <code>reserve</code> is false, 
	 * the program will replace the original matrix with the new matrix after normalized.
	 * @return Matrices after normalized.
	 */
	public static Matrix[] normalizeByMinMax(Matrix[] src, boolean reserve){
		Matrix[] dst = reserve ? new Matrix[src.length]: src;
		Matrix min = Matrix.ones(src[0].rows(), src[0].columns(), Double.MAX_VALUE);
		Matrix max = Matrix.ones(src[0].rows(), src[0].columns(), Double.MIN_VALUE);
		//find the min and max
		for (int i = 0; i < src[0].rows(); i++){
			for (int j = 0; j < src[0].columns(); j++){
				for (int k = 0; k < src.length; k++){
					if (min.at(i, j) > src[k].at(i, j))
						min.set(i, j, src[k].at(i, j));
					if (max.at(i, j) < src[k].at(i, j))
						max.set(i, j, src[k].at(i, j));
				}
			}
		}
		//normalize
		for (int k = 0; k < src.length; k++)
			for (int i = 0; i < min.rows(); i++)
				for (int j = 0; j < min.columns(); j++){
					double r = 2 * (src[k].at(i, j) - min.at(i, j))/(max.at(i, j) - min.at(i, j)) - 1;
					src[k].set(i, j, r);
				}
		return dst;
	}
	
	/**
	 * Calculate the mean matrix of a set of matrices.
	 * @param matrices A set of matrices to compute.
	 * @param start Start index of the matrices.
	 * @param length Length of the matrices to compute
	 * @return Mean matrix
	 */
	public static Matrix computeMean(Matrix[] matrices, int start, int length){
		if (start < 0 || start + length > matrices.length)
			throw new IllegalArgumentException("MatrixOpt computMean, out of range.");
		Matrix mean = new Matrix(matrices[0].rows(), matrices[0].columns());
		for (int i = start; i < length; i++)
			mean.plus(matrices[i]);
		mean.divide(matrices.length);
		return mean;
	}
	
	/**
	 * Calculate the mean matrix of a set of matrices.
	 * @param matrices A set of matrix to compute.
	 * @return Mean matrix.
	 */
	public static Matrix computeMean(Matrix[] matrices){
		return computeMean(matrices, 0, matrices.length);
	}
	
	/**
	 * <p>Compute the covariance of the vectors consist of a set of row vector by row, this is an economical way to 
	 * save storage space while the dimension of the vectors is very large because the method will not open up 
	 * new storage space any more.<p>
	 * <p>Assume V=[v1,v2,...,vn], the covariance will calculate as: 
	 * <br>Cov=(V-M)*transpose(V-M), that M is the mean vector of V, and transpose means transpose of vector, 
	 * Cov is the covariance matrix.</br></p>
	 * @param vectors A set of vectors to compute the covariance.
	 * @param mean Mean matrix.
	 * @param start Start index of vectors to compute.
	 * @param length Length to compute.
	 * @param scale Scale, the covariance after computed will multiply the scale.
	 * @return - Covariance matrix of the matrices
	 */
	public static Matrix computeCovarianceByRow(Matrix[] vectors, Matrix mean, int start, int length, double scale){
		if (vectors[0].rows() != 1)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByRow, accept row vectors only.");
		if (start < 0 || start + length > vectors.length)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByRow, out of range.");
		Matrix cov = new Matrix(length, length);
		for (int i = start; i < length; i++){
			Matrix t = vectors[i].minus(mean, true);
			for (int j = 0; j < length; j++){
				Matrix r = vectors[j].minus(mean, true).t();
				cov.set(i, j, t.multiply(r).data()[0]*scale);
			}
		}
		return cov;
	}
	
	/**
	 * <p>Compute the covariance of the vectors consist of a set of row vector by row, this is an economical way to 
	 * save storage space while the dimension of the vectors is very large because the method will not open up 
	 * new storage space any more.<p>
	 * <p>Assume V=transpose[v1,v2,...,vn], the covariance will calculate as: 
	 * <br>Cov=(V-M)*transpose(V-M), that M is the mean vector of V, and transpose means transpose of vector, 
	 * Cov is the covariance matrix.</br></p>
	 * @param vectors A set of vectors.
	 * @param mean Mean matrix.
	 * @param scale The covariance matrix after computed will multiply the scale.
	 * @return Covariance matrix of the matrices
	 */
	public static Matrix computeCovarianceByRow(Matrix[] vectors, Matrix mean, double scale){
		return computeCovarianceByRow(vectors, mean, 0, vectors.length, scale);
	}
	
	/**
	 * compute the covariance of the vectors consist of a set of row vectors by column, this is an economical way
	 * to save storage space while the dimension of the vectors is very large because the method will not open up
	 * new storage space any more.
	 * <p>Assume V=[v1,v2,...,vn], the covariance matrix will calculate as:
	 * <br>Cov=transpose(V-M)*(V-M), that M is the mean vector of V, and transpose means transpose of vector,
	 * Cov is the covariance matrix.</br></p>
	 * @param vectors Row vectors.
	 * @param mean Mean vector.
	 * @param scale The covariance matrix after computed will multiply the scale.
	 * @return Covariance matrix.
	 */
	public static Matrix computeCovarianceByCol(Matrix[] vectors, Matrix mean, double scale){
		if (vectors[0].rows() != 1)
			throw new IllegalArgumentException("MatrixOpt computeCovarianceByCol, accept row vectors only.");
		Matrix cov = new Matrix(vectors[0].columns(), vectors[0].columns());
		Matrix t = new Matrix(1, vectors.length);
		Matrix r = new Matrix(vectors.length, 1);
		for (int i = 0; i < vectors[0].columns(); i++){
			for (int k = 0; k < vectors.length; k++)
				t.set(0, k, vectors[k].at(0, i) - mean.at(0, i));
			for (int j = 0; j < vectors[0].columns(); j++){
				for (int h = 0; h < vectors.length; h++)
					r.set(h, 0, vectors[h].at(0, j) - mean.at(0, j));
				cov.set(i, j, t.multiply(r).data()[0]*scale);
			}
		}
		return cov;
	}
	
	/**
	 * Calculate the 2-dimensional covariance of the matrices.
	 * <p>Assume A=Ai, Ai is 2-dimensional matrix. The covariance will compute as:
	 * <br>Cov=sum(transpose(Ai-mean)*(Ai-mean)).</br></p>
	 * @param matrices A set of matrix.
	 * @param mean Mean matrix
	 * @param scale The covariance after computed will multiply the scale.
	 * @return Covariance matrix
	 */
	public static Matrix compute2DCovariance(Matrix[] matrices, Matrix mean, double scale){
		Matrix cov = new Matrix(mean.columns(), mean.columns());
		for (int i = 0; i < matrices.length; i++){
			Matrix t = matrices[i].minus(mean, true);
			cov.plus(t.t().multiply(t));
		}
		cov.multiply(scale);
		return cov;
	}
	
	/**
	 * calculate the 2-dimensional covariance of the matrices
	 * @param matrices
	 * @param mean mean matrix
	 * @param scale
	 * @return covariance matrix
	 */
	public static Matrix compute2DCovariance(Matrix[] matrices, Matrix mean){
		return compute2DCovariance(matrices, mean, 1.);
	}
}
