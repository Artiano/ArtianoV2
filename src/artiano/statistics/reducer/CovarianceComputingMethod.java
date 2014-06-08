/**
 * CovarianceMethod.java
 */
package artiano.statistics.reducer;

import artiano.core.structure.Matrix;

/**
 * <p>协方差的计算方法</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-29
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class CovarianceComputingMethod {
	private Matrix mean = null;
	private Matrix[] means = null;
	private Matrix sb = null;
	private Matrix sw = null;
	/**
	 * 计算给定矩阵的协方差矩阵
	 * @param samples
	 * @param mean
	 * @return 协方差矩阵
	 */
	public abstract Matrix compute(Matrix samples, Matrix mean);
}
