/**
 * NullSpaceSolve.java
 */
package artiano.statistics.reducer;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NullSpaceSolve implements SolvingAlgorithm {

	protected Matrix projection = null;
	protected Matrix eigenValue = null;
	
	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.SolvingAlgorithm#solve(artiano.core.structure.Matrix, artiano.core.structure.Matrix, artiano.core.structure.Matrix[])
	 */
	@Override
	public void solve(Matrix cov1, Matrix cov2, Matrix[] matrices) {
		
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.SolvingAlgorithm#getProjectionMatrix()
	 */
	@Override
	public Matrix getProjectionMatrix() {
		return projection;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.SolvingAlgorithm#getEigenValue()
	 */
	@Override
	public Matrix getEigenValue() {
		return eigenValue;
	}

}
