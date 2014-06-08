/**
 * UnsupervisedReducer.java
 */
package artiano.statistics.reducer;

import artiano.core.structure.Matrix;

/**
 * <p>Description:</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-26
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface UnsupervisedReducer {
	/**
	 * train an extractor with no labels
	 * @param samples - samples to train
	 */
	public void train(Matrix samples);
}
