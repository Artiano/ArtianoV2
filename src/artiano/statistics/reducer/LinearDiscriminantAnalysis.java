/**
 * LinearDiscriminantAnalysis.java
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
public class LinearDiscriminantAnalysis extends Reducer 
	implements SupervisedReducer {
	
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.SupervisedReducer#train(artiano.core.structure.Matrix[], artiano.core.structure.Matrix)
	 */
	@Override
	public void train(Matrix samples, Matrix labels) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.Reducer#extract(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reduce(Matrix sample) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.Reducer#reconstruct(artiano.core.structure.Matrix)
	 */
	@Override
	public Matrix reconstruct(Matrix feature) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.reducer.Reducer#getModel()
	 */
	@Override
	public Matrix getModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
