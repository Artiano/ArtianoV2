package artiano.ml.association.structure;

import java.util.*;

public class Sequence<T extends Comparable<T>> {
	private int support; // 该序列在数据库中的支持计数
	private List<Element<T>> sequence; // 存放元素序列

	public Sequence() {
		this.support = 0;
		this.sequence = new ArrayList<Element<T>>();
	}

	public Sequence(Element<T>[] sequenceArr) {
		this.support = 0;
		this.sequence = new ArrayList<Element<T>>();
		int size = sequenceArr.length;
		for (int i = 0; i < size; i++) {
			sequence.add(sequenceArr[i]);
		}
	}

	public Sequence(List<Element<T>> sequence) {
		this.support = 0;
		this.sequence = new ArrayList<Element<T>>();
		int size = sequence.size();
		for (int i = 0; i < size; i++) {
			this.sequence.add(sequence.get(i));
		}
	}

	// 拷贝参数序列对象s中的所有元素到本对象的sequence属性中
	public Sequence(Sequence<T> s) {
		this.sequence = new ArrayList<Element<T>>();
		this.support = 0;
		// 拷贝s中的所有元素
		for (int i = 0; i < s.getSize(); i++) {
			this.sequence.add(s.sequence.get(i).clone());
		}
	}
	
	//构造只含有一项的序列
	public Sequence(T item) {
		this.support = 0;
		this.sequence = new ArrayList<Element<T>>();
		Element<T> element = new Element<T>();
		element.addItem(item);		
		this.sequence.add(element);
	}

	// 向序列中添加新的元素
	public void addElement(Element<T> element) {
		this.sequence.add(element);
	}

	/**
	 * 
	 * @param index 下标
	 * @return 返回指定下标处的元素(如果下表越界，返回空的Element对象)
	 */
	public Element<T> getElement(int index) {
		if (index < 0 || index >= getSize()) {
			return new Element<T>();
		} else {
			return this.sequence.get(index);
		}
	}

	public List<Element<T>> getElements() {
		return sequence;
	}

	// 增加支持计数
	public void incrementSupport() {
		this.support++;
	}

	// 获取支持计数
	public int getSupport() {
		return this.support;
	}

	public int getSize() {
		return sequence.size();
	}

	// 判断本序列是不是在序列集candidatePattern中出现过
	public boolean notInSeqs(List<Sequence<T>> candidatePattern) {
		for (int i = 0; i < candidatePattern.size(); i++) {
			Sequence<T> s = candidatePattern.get(i);
			if (this.isSubsequenceOf(s) && s.isSubsequenceOf(this)) {
				return false;
			}
		}
		return true;
	}

	// 判断本序列是不是另外一个序列的子序列
	public boolean isSubsequenceOf(Sequence<T> s) {
		int i = 0, j = 0;
		while (j < s.getSize() && i < this.sequence.size()) {
			if (this.getElement(i).isContainIn(s.getElement(j))) {
				i++;
				j++;
				if (i == this.sequence.size()) {
					return true;
				}
			} else {
				j++;
			}
		}
		return false;
	}
	
	//判断序列中是否含有某一单项
	public boolean containsItem(T item) {
		for(Element<T> element : sequence) {
			if(element.contains(item)) {
				return true;
			} 
		}
		return false;
	}

	//移除序列中特定下标元素(如果序列为空或下标越界，则不做任何操作)
	public void removeElement(int index) {
		if (getSize() == 0 || index < 0 || index >= getSize()) {
			return;
		} else {
			this.sequence.remove(index);
		}
	}
	
	//移除序列中特定下标元素(如果序列为空或要删除的元素不在列表中，则不做任何操作)
	public void removeElement(Element<T> element) {
		if(this.sequence.contains(element)) {
			sequence.remove(element);
		}
	}

	/**
	 * 在序列的指定下标处插入一个元素(如果下标越界或要插入的元素为空，则不做任何操作)
	 * 
	 * @param index
	 * @param element
	 */
	public void insertElement(int index, Element<T> element) {
		if (index < 0 || index > getSize() || element == null) {
			return;
		} else {
			this.sequence.add(index, element);
		}
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Sequence) {
			@SuppressWarnings("unchecked")
			Sequence<T> sequence = (Sequence<T>) obj;
			if(this.getSize() != sequence.getSize()) {
				return false;
			}
			List<Element<T>> elements = sequence.getElements();
			for(Element<T> element: elements) {
				if(!this.sequence.contains(element)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		int size = getSize();
		if (size == 0) {
			return "< >";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("<");
			for (int i = 0; i < size; i++) {
				String elementStr = getElement(i).toString();
				if (i < size - 1) {
					sb.append(elementStr + " ");
				} else {
					sb.append(elementStr);
				}
			}
			sb.append(">");
			return sb.toString();
		}
	}
}
