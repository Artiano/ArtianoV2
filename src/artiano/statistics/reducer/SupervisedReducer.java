/**
 * SupervisedReducer.java
 */
package artiano.statistics.reducer;

import artiano.core.structure.Matrix;

/**
 * <p>受监督的降维器（特征提取器）</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-26
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface SupervisedReducer {
	/**
	 * 使用特定的标签训练一个降维器
	 * @param samples 待训练的样本
	 * @param labels 样本的标签
	 */
	public void train(Matrix samples, Matrix labels);
}
