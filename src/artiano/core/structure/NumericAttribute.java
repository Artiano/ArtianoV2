/**
 * NumericAttribute.java
 */
package artiano.core.structure;

import java.util.Random;

/**
 * <p>
 * 表示数值属性
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class NumericAttribute extends Attribute {
	/** 属性值缺失替换 */
	public static final double MISSING_VALUE_REPLACE = 0.;
	/** 归一化到[-1,1] */
	public static final int NORMALIZE_MINMAX_N11 = 0;
	/** 归一化到[0,1] */
	public static final int NORMALIZE_MINMAX_01 = 1;
	/** 使用z-score归一 */
	public static final int NORMALIZE_ZSCORE = 2;

	/**
	 * 构造一个数值属性
	 */
	public NumericAttribute() {
		this.type = "Numeric";
	}

	/**
	 * 使用声明的名称构造一个数值属性
	 * 
	 * @param name
	 */
	public NumericAttribute(String name) {
		super(name);
		this.type = "Numeric";
	}

	public NumericAttribute(String name, IncrementVector vector) {
		super(name, vector);
		this.type = "Numeric";
	}

	/**
	 * 归一化数据
	 * 
	 * @param method
	 *            使用的归一化方法
	 */
	public void normalize(int method) {
		if (method == NORMALIZE_MINMAX_N11)
			normalizeByMinMaxN11();
		else if (method == NORMALIZE_MINMAX_01)
			normalizeByMinMax01();
		else if (method == NORMALIZE_ZSCORE)
			normalizeByZScore();
	}

	/**
	 * 使用属性值最大值最小值方法将属性值向量归一化到(-1,1)
	 */
	public void normalizeByMinMaxN11() {
		double min = min();
		double max = max();
		for (int i = 0; i < vector.size(); i++) {
			double r = 2 * (get(i) - min) / (max - min) - 1;
			vector.set(i, r);
		}
	}

	/**
	 * 使用属性值最大值最小值方法将属性值向量归一到(0,1)
	 */
	public void normalizeByMinMax01() {
		double min = min();
		double max = max();
		for (int i = 0; i < vector.size(); i++) {
			double r = (get(i) - min) / (max - min);
			vector.set(i, r);
		}
	}

	/**
	 * 使用z-score方法将属性值向量归一化
	 */
	public void normalizeByZScore() {
		double mean = mean();
		double stdDev = standardDeviation();
		for (int i = 0; i < vector.size(); i++) {
			double r = (get(i) - mean) / stdDev;
			vector.set(i, r);
		}
	}

	/**
	 * 求取平均值
	 * 
	 * @return
	 */
	public double mean() {
		double avg = 0;
		for (int i = 0; i < this.vector.size(); i++)
			if (!get(i).equals(MISSING_VALUE))
				avg += get(i);
		avg /= countNoneMissing();
		return avg;
	}

	/**
	 * 求取最大值
	 * 
	 * @return
	 */
	public double max() {
		double m = get(0);
		for (int i = 1; i < this.vector.size(); i++)
			if (m < get(i))
				m = get(i);
		return m;
	}

	/**
	 * 求取最小值
	 * 
	 * @return
	 */
	public double min() {
		double m = get(0);
		for (int i = 1; i < this.vector.size(); i++)
			if (m > get(i))
				m = get(i);
		return m;
	}

	/**
	 * 标准差
	 * 
	 * @return
	 */
	public double standardDeviation() {
		double stdDev = 0.;
		double mean = this.mean();
		for (int i = 0; i < this.vector.size(); i++) {
			if (!isMissing(i)) {
				double t = get(i) - mean;
				stdDev += t * t;
			}
		}
		stdDev /= countNoneMissing() - 1;
		stdDev = Math.sqrt(stdDev);
		return stdDev;
	}

	/**
	 * 转换为符号属性
	 * 
	 * @return
	 */
	public NominalAttribute toNominal() {
		NominalAttribute att = new NominalAttribute(this.getName());
		for (int i = 0; i < this.vector.size(); i++) {
			if (!isMissing(i))
				att.addNominal(this.vector.at(i));
			att.vector.push(this.vector.at(i));
		}
		return att;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see artiano.core.structure.Attribute#replaceMissing()
	 */
	@Override
	public void replaceMissing() {
		for (int i = 0; i < vector.size(); i++)
			if (isMissing(i))
				vector.set(i, MISSING_VALUE_REPLACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see artiano.core.structure.Attribute#get(int)
	 */
	@Override
	public Double get(int i) {
		return (Double) this.vector.at(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see artiano.core.structure.Attribute#toArray()
	 */
	@Override
	public double[] toArray() {
		double[] array = new double[this.vector.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = ((Double) this.vector.at(i)).doubleValue();
		return array;
	}

	public static void main(String[] args) {
		NumericAttribute att = new NumericAttribute();
		Random r = new Random();
		for (int i = 0; i < 10; i++)
			att.getVector().push(r.nextInt(10));
		System.out.println("after push:");
		att.getVector().print();
		att.getVector().push(MISSING_VALUE);
		System.out.println("after push ?: ");
		att.getVector().print();
		System.out.println("mean: " + att.mean());
		System.out.println("max: " + att.max());
		System.out.println("min: " + att.min());
		//standard deviation
		System.out.println("standard deviation: "+att.standardDeviation());
	}

}
