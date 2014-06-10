/**
 * Distance.java
 */
package artiano.statistics.distance;

import artiano.core.structure.Matrix;

/**
 * <p>表示距离的接口。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-15
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public interface Distance {
	/**
	 * 计算给定两个向量a、b之间的距离
	 * @param a 
	 * @param b
	 * @return 距离
	 */
	public double calculate(Matrix a, Matrix b);
}
