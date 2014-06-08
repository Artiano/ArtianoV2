/**
 * Ransac.java
 */
package artiano.ml.regression;

import java.util.Random;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-21
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class Ransac extends Regression {
	
	Matrix model = null;
	
	/* (non-Javadoc)
	 * @see artiano.ml.regression.Regression#fit(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix fit(Matrix x, Matrix y) {
		model = new Matrix(x.columns()+1, 1);
		return null;
	}
	
	protected void estimate(){
		
	}
	
	protected Matrix generateLine(Matrix x, Matrix y){
		Random r = new Random();
		int p1 = r.nextInt(x.rows());
		int p2 = r.nextInt(x.rows());
		return null;
	}
	
	protected boolean isAgree(Matrix point){
		return false;
	}
	
}
