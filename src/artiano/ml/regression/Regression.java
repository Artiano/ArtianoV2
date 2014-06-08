/**
 * Regression.java
 */
package artiano.ml.regression;

import artiano.core.structure.Matrix;

/**
 * <p>所有回归方法的超类。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-20
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Regression {
	/**
	 * 从输入数据x和y中拟合出一个回归模型。
	 * <ul>
	 * <li><b><i>NOTICE:</b></i></li>所有x和y都以行向量表示，生成的模型将以列向量表示。
	 * </ul>
	 * @param x x数据
	 * @param y y数据
	 * @return 生成的模型
	 */
	public abstract Matrix fit(Matrix x, Matrix y);
}
