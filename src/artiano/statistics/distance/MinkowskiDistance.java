/**
 * MinkowskiDistance.java
 */
package artiano.statistics.distance;

import artiano.core.structure.Matrix;

/**
 * <p>闵可夫斯基距离</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-17
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class MinkowskiDistance implements Distance {

	private int power = 2;
	
	/**
	 * 默认构造方法，此时幂次数为2，相当于欧几里得距离。
	 */
	public MinkowskiDistance(){}
	
	public MinkowskiDistance(int power){
		this.power = power;
	}
	
	/**
	 * @return 获得距离幂次。
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @param power 设置距离幂次。
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/* (non-Javadoc)
	 * @see artiano.statistics.distance.Distance#calculate(artiano.core.structure.Matrix, artiano.core.structure.Matrix)
	 */
	@Override
	public double calculate(Matrix a, Matrix b) {
		return 0;
	}
	
}
