/**
 * Attribute.java
 */
package artiano.core.structure;

import java.io.Serializable;

/**
 * <p>
 * 基本数据结构，表示任何属性的超类。
 * </p>
 * 
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-28
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public abstract class Attribute implements Serializable {
	private static final long serialVersionUID = 9111172224858073660L;
	
	/** 属性缺失取值 */
	public static final double MISSING_VALUE = Double.NaN;
	/** 属性类型 */
	protected String type = "";
	/** 属性名称 */
	protected String name = null;
	/** 存放属性值的向量 */
	protected IncrementVector vector = new IncrementVector();

	/**
	 * 构造一个属性
	 */
	public Attribute() {
	}

	/**
	 * 使用声明的名称构造一个属性
	 * 
	 * @param name
	 *            属性名称
	 */
	public Attribute(String name) {
		this.name = name;
	}

	/**
	 * 判断表中是否有缺失值
	 * 
	 * @return
	 */
	public boolean hasMissing() {
		for (int i = 0; i < vector.size(); i++)
			if (vector.at(i).equals(MISSING_VALUE))
				return true;
		return false;
	}

	/**
	 * 判断属性值向量在i处是否为缺失值
	 * 
	 * @param i
	 *            下标
	 * @return
	 */
	public boolean isMissing(int i) {
		return vector.at(i).equals(MISSING_VALUE);
	}

	/**
	 * 获取非缺失值个数
	 * 
	 * @return
	 */
	public int countNoneMissing() {
		int size = 0;
		for (int i = 0; i < vector.size(); i++)
			if (!isMissing(i))
				size++;
		return size;
	}

	/**
	 * 获取值向量的大小
	 * 
	 * @return
	 */
	public int size() {
		return vector.size();
	}

	/**
	 * 获取缺失值个数
	 * 
	 * @return
	 */
	public int countMissing() {
		int count = 0;
		for (int i = 0; i < vector.size(); i++)
			if (isMissing(i))
				count++;
		return count;
	}

	/**
	 * 使用声明的名称和属性值向量构造一个属性
	 * 
	 * @param name
	 *            名称
	 * @param vector
	 *            属性值向量
	 */
	public Attribute(String name, IncrementVector vector) {
		this.name = name;
		this.vector = vector;
	}

	/**
	 * 获取属性名称
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取属性类型
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置属性名称
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取属性值向量
	 * 
	 * @return the vector
	 */
	public IncrementVector getVector() {
		return vector;
	}

	/**
	 * 设置属性值向量
	 * 
	 * @param vector
	 */
	public void setVector(IncrementVector vector) {
		this.vector = vector;
	}

	/**
	 * 移除在i处的值
	 * 
	 * @param i
	 */
	public void remove(int i) {
		this.vector.remove(i);
	}

	/**
	 * 在向量后添加值
	 * 
	 * @param value
	 */
	public void push(Object value) {
		vector.push(value);
	}

	/**
	 * 设置向量的值
	 * 
	 * @param i
	 * @param value
	 */
	public void set(int i, Object value) {
		vector.set(i, value);
	}

	/**
	 * 判断向量值中的值是否兼容，如果表中拥有两种及两种（String，Numeric）以上的值类型，
	 * 则认为是不兼容的
	 * 
	 * @return
	 */
	public boolean valueCompatible() {
		if (size() == 0)
			return true;
		Class<?> t = get(0).getClass();
		for (int i = 1; i < size(); i++)
			if (!isMissing(i) && !t.equals(get(i).getClass()))
				return false;
		return true;
	}

	/**
	 * 获取属性值向量在i处的值
	 * 
	 * @param i
	 *            指定下标
	 * @return
	 */
	public abstract Object get(int i);

	/**
	 * 将属性向量转换为数组
	 * 
	 * @return
	 */
	public abstract Object toArray();

	/**
	 * 替换缺失值
	 */
	public abstract void replaceMissing();
}
