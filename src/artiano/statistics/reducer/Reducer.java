/**
 * Reducer.java
 */
package artiano.statistics.reducer;

import artiano.core.operation.Preservable;
import artiano.core.structure.Matrix;

/**
 * <p>所有特征提取器的父类。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-8-22
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Reducer extends Preservable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 从样本中提取特征。
	 * @param sample 输入样本。
	 * @return 样本特征矩阵。
	 */
	public abstract Matrix reduce(Matrix sample);
	
	/**
	 * 根据提取得到的特征重建样本。
	 * @param feature 提取器提取得到的特征。
	 * @return 重建后的样本。
	 */
	public abstract Matrix reconstruct(Matrix feature);
	
	/**
	 * 得到提取器形成的模型。
	 * @return 提取器形成的模型。
	 */
	public abstract Matrix getModel();
}
