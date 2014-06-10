/**
 * EuclideanDistance.java
 */
package artiano.statistics.distance;

import artiano.core.structure.Matrix;

/**
 * <p>计算两个给定向量的欧几里得距离。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-16
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class EuclideanDistance implements Distance {

	/* (non-Javadoc)
	 * @see artiano.statistics.distance.Distance#calculate(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double calculate(Matrix a, Matrix b) {
		if (!a.isVector() || !b.isVector())
			throw new UnsupportedOperationException("Distance calculate, accept vector only.");
		if (!a.sameType(b))
			throw new UnsupportedOperationException("Distance calculate, type not same.");
		double d = 0.;
		Matrix x = a.minus(b, true);
		if (x.isRowVector())
			d = x.multiply(x.t()).sqrt().data()[0];
		else
			d = x.t().multiply(x).sqrt().data()[0];
		return d;
	}

}
