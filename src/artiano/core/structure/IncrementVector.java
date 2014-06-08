/**
 * IncrementVector.java
 */
package artiano.core.structure;

import java.io.Serializable;
import java.util.Iterator;

/**
 * <p>基础数据结构，自增长类型向量，用来存放所有表示属性的值。</p>
 * @author Nano.Michael
 * @version 1.0.0
 * @date 2013-10-30
 * @author (latest modification by Nano.Michael)
 * @since 1.0.0
 */
public class IncrementVector implements Serializable, Iterable<Object> {
	private static final long serialVersionUID = -4168269174447845594L;
	
	/** 属性向量中值的个数  */
	protected int size = 0;
	/** 向量容量的增长因子  */
	protected int incrementFactor = 2;
	/** 存储属性值的数组 */
	protected Object[] data = null;
	/** 默认容量 */
	public static final int DEFAULT_CAPACITY = 50;
	
	public class VectorIterator implements Iterator<Object> {
		/** 计数器 */
		private int counter = 0;
		/** 需迭代的向量 */
		private IncrementVector v = null;
		
		/**
		 * 构造一个向量迭代器
		 * @param v 需迭代的向量
		 */
		public VectorIterator(IncrementVector v) {
			this.v = v;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return (counter < v.size());
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Object next() {
			return v.at(counter++);
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * 构造一个属性值向量，此时向量的容量为1
	 */
	public IncrementVector(){
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * 构造一个属性值向量
	 * @param capacity 属性向量容量
	 */
	public IncrementVector(int capacity){
		this.data = new Object[capacity];
	}
	/**
	 * 拷贝一份（深层拷贝）
	 * @return
	 */
	public IncrementVector copy(){
		IncrementVector vector = new IncrementVector(this.capacity());
		vector.size = this.size;
		System.arraycopy(this.data, 0, vector.data, 0, size);
		return vector;
	}
	
	/**
	 * 设置属性向量的容量
	 * @param capacity 待设置的容量
	 */
	public void setCapacity(int capacity){
		Object[] newObjects = new Object[capacity];
	    System.arraycopy(data, 0, newObjects, 0, Math.min(capacity, size));
	    this.data = newObjects;
	    if (data.length < size)
	      size = data.length;
	}
	
	/**
	 * 获取属性值向量的容量
	 * @return 属性值向量容量
	 */
	public int capacity(){
		return this.data.length;
	}
	
	/**
	 * 判断向量是否为空
	 * @return
	 */
	public boolean isEmpty(){
		return (size==0);
	}
	
	/**
	 * 获取属性向量的大小
	 * @return
	 */
	public int size(){
		return this.size;
	}
	
	/**
	 * 获取向量尾元素
	 * @return 向量尾元素
	 */
	public Object back(){
		return this.data[size-1];
	}
	
	/**
	 * 获取属性在下标i处的值
	 * @param i 下标
	 * @return 在下标i处的值
	 */
	public Object at(int i) {
		return this.data[i];
	}
	
	/**
	 * 在属性向量下标i处设置值
	 * <ul>
	 * <li>
	 * 必须在使用 {@link #push(Object)}，{@link #insert(int, Object)},{@link #append(IncrementVector)}
	 * 方法后才能使用此方法，因为此方法设置值后不会导致数据长度发生改变
	 * </li>
	 * </ul>
	 * @param i 声明的下标
	 * @param value 待设置的值
	 */
	public void set(int i, Object value) {
		this.data[i] = value;
	}
	
	/**
	 * 在属性的值向量下标i处插入一个值
	 * @param i 声明的下标
	 * @param value 待插入的值
	 */
	public void insert(int i, Object value) {
		if (size < data.length) {
			System.arraycopy(data, i, data, i + 1, size - i);
			data[i] = (Object) value;
		} else {
			Object[] newData = new Object[incrementFactor * (data.length + 1)];
			System.arraycopy(data, 0, newData, 0, i);
			newData[i] = value;
			System.arraycopy(data, i, newData, i + 1, size - i);
			this.data = newData;
		}
		size++;
	}
	
	/**
	 * 移除属性在下标i处的值
	 * @param i 声明的下标
	 */
	public void remove(int i) {
		System.arraycopy(data, i + 1, data, i, size - i - 1);
		// clear the last reference
		data[size - 1] = null;
		size--;
	}
	
	/**
	 * 清空属性值向量
	 */
	public void clear(){
		this.data = new Object[data.length];
		size = 0;
	}
	
	/**
	 * 在属性向量的尾部插入一个值
	 * @param value 待插入的值
	 */
	public void push(Object value) {
		if (size == data.length) {
			Object[] newData = new Object[incrementFactor * (data.length + 1)];
			System.arraycopy(data, 0, newData, 0, size);
			this.data = newData;
		}
		if (value instanceof Number)
			data[size] = ((Number)value).doubleValue();
		else
			data[size] = value;
		size++;
	}
	
	/**
	 * 在属性值向量的尾部附加一个属性值向量
	 * @param vector 待附加的属性值向量
	 */
	public void append(IncrementVector vector){
		if (this.data.length < (data.length+vector.size))
			setCapacity(size+vector.size);
		System.arraycopy(vector.data, 0, data, size, vector.size);
		size = vector.size+size;
	}
	
	/**
	 * 弹出属性向量尾部的值
	 */
	public void pop(){
		this.data[size-1] = null;
		size--;
	}
	
	/**
	 * 获取值所在下标，若值不在向量中，则返回-1
	 * @param value 待寻找的值
	 * @return
	 */
	public int indexOf(Object value){
		for (int i=0; i<size; i++)
			if (data[i].equals(value))
				return i;
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Object> iterator() {
		return new VectorIterator(this);
	}
	
	/**
	 * 修剪向量以使得向量容量等于向量长度
	 */
	public void trim() {
		Object[] objects = new Object[size()];
		System.arraycopy(data, 0, objects, 0, size());
		this.data = objects;
	}
	
	/**
	 * 辅助方法，输出属性值向量中的所有元素到控制台
	 */
	public void print(){
		for (int i = 0; i < size; i++)
			System.out.print(at(i)+" ");
		System.out.println();
	}
	
	public static void main(String[] args){
		IncrementVector vector = new IncrementVector();
		vector.setCapacity(3);
		System.out.print("push element to vector:");
		//push
		vector.push(1);
		vector.push(2);
		vector.push(3);
		vector.print();
		//append
		IncrementVector vector2 = new IncrementVector(5);
		vector2.push(2);
		vector2.push(3);
		vector2.push(4);
		vector2.set(2, 10);
		System.out.println("size:"+vector2.size());
		//remove
		vector2.remove(0);
		System.out.println("vector2:");
		vector2.print();
		System.out.println("after append:");
		vector2.append(vector);
		vector2.print();
		System.out.print("capacity of vector2:");
		System.out.println(vector2.capacity());
		//reset capacity
		vector2.setCapacity(4);
		System.out.print("set capacity to 3:");
		vector2.print();
		//clear
		vector2.clear();
		System.out.println("after clear:");
		vector2.print();
		//copy
		IncrementVector vector3 = vector.copy();
		System.out.println("copy vector to vector3:");
		vector3.print();
		//remove
		vector3.remove(1);
		System.out.println("after remove:");
		vector3.print();
	}
	
}




