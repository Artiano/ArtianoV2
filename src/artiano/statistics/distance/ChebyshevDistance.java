/**
 * ChebyshevDistance.java
 */
package artiano.statistics.distance;

import artiano.core.structure.Matrix;

/**
 * <p>计算给定两个向量之间的切比雪夫距离。给定两个向量a、b，它们之间的切比雪夫距离计算为：
 * <pre><code>
 * d(a,b) = max(|a(i)-b(i)|)
 * </code></pre></p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-16
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class ChebyshevDistance implements Distance{

	/* (non-Javadoc)
	 * @see artiano.statistics.distance.Distance#calculate(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double calculate(Matrix a, Matrix b) {
		if (!a.isVector() || !b.isVector())
			throw new UnsupportedOperationException("Distance calculate, accept vector only.");
		if (!a.sameType(b))
			throw new UnsupportedOperationException("Distance calculate, type not same.");
		double d = Math.abs(a.at(0)-b.at(0));
		for (int i=0; i<a.size();i++){
			double t = Math.abs(a.at(i)-b.at(i));
			if (d<t)
				d = t;
		}
		return d;
	}

}
