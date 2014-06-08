package artiano.ml.association.structure;

import java.util.*;

public class Element<T extends Comparable<T>> {
	private List<T> itemset;    //项集

	public Element() {
		this.itemset = new ArrayList<T>();
	}

	public Element(T[] items) {
		this.itemset = new ArrayList<T>();
		int length = items.length;
		for (int i = 0; i < length; i++) {
			itemset.add(items[i]);
		}
	}

	public Element(List<T> itemset) {
		this.itemset = itemset;
	}

	public void addItem(T item) {
		this.itemset.add(item);
	}

	/**
	 * 在元素的指定下标处添加一项(如果指定下标越界，则不添加)
	 * 
	 * @param index
	 * @param item
	 */
	public void addItem(int index, T item) {
		if (index < 0 || index > getSize()) {
			return;
		} else {
			this.itemset.add(index, item);
		}
	}

	public List<T> getItemset() {
		return itemset;
	}

	/**
	 * @return 返回元素中的第一项(如果元素为空，返回null)
	 */
	public T getFirstItem() {
		if (getSize() == 0) {
			return null;
		} else {
			return itemset.get(0);
		}
	}

	/**
	 * @return 返回元素中的最后一项(如果元素为空，返回null)
	 */
	public T getLastItem() {
		if (getSize() == 0) {
			return null;
		} else {
			return itemset.get(getSize() - 1);
		}
	}

	/**
	 * 移除元素的第一项(如果元素为空，返回null)
	 */
	public T removeFirstItem() {
		if (getSize() == 0) {
			return null;
		} else {
			return this.itemset.remove(0);
		}
	}

	/**
	 * 移除元素的最后一项(如果元素为空，返回null)
	 */
	public T removeLastItem() {
		if (getSize() == 0) {
			return null;
		} else {
			return this.itemset.remove(getSize() - 1);
		}
	}

	/**
	 * 移除元素的特定下标项(如果元素为空或下标越界，返回null)
	 */
	public T removeItem(int index) {
		if (getSize() == 0 || index < 0 || index >= getSize()) {
			return null;
		} else {
			return this.itemset.remove(index);
		}
	}

	// 获取去除第一项的元素
	public Element<T> getWithoutFirstItem() {
		Element<T> element = new Element<T>();
		int size = getSize();
		for (int i = 1; i < size; i++) {
			element.addItem(itemset.get(i));
		}
		return element;
	}

	// 获取去除最后一项的元素
	public Element<T> getWithoutLastItem() {
		Element<T> element = new Element<T>();
		int size = getSize();
		for (int i = 0; i < size - 1; i++) {
			element.addItem(itemset.get(i));
		}
		return element;
	}

	public boolean contains(T item) {
		if (this.itemset.contains(item)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断本元素是不是包含于元素e中
	 * 
	 * @param e 元素
	 * @return true--是 false--否
	 */
	public boolean isContainIn(Element<T> e) {
		if (this.itemset.size() > e.itemset.size()) {// 如果两个元素大小不同，则为不相等
			return false;
		}

		int i = 0, j = 0;
		while (j < e.getSize() && i < this.itemset.size()) {
			if (this.itemset.get(i).equals(e.itemset.get(j))) {
				i++;
				j++;
			} else {
				j++;
			}
		}

		if (i == this.itemset.size()) {
			return true;
		} else {
			return false;
		}
	}

	public int getSize() {
		return itemset.size();
	}

	// 深度复制元素
	public Element<T> clone() {
		Element<T> element = new Element<T>();
		int size = getSize();
		for (int i = 0; i < size; i++) {
			element.addItem(itemset.get(i));
		}
		return element;
	}

	/**
     * 判断两个元素是否相同
     * @param o          
     * @return  true--相同 false--不同
     */
    public boolean equals(Object o){
       if( !(o instanceof Element) ) {
    	   return false;
       }
       
       boolean equal=true;
       @SuppressWarnings("unchecked")
       Element<T> e=(Element<T>)o;
       if(this.itemset.size()!=e.itemset.size()){//如果两个元素大小不同，则为不相等
           equal=false;
       }

       for(int i=0; equal && i<this.itemset.size(); i++){
           if(!this.itemset.get(i).equals(e.itemset.get(i))){
               equal=false;
               break;
           }
       }
       return equal;
   }
	
	public String toString() {
		int size = getSize();
		if (size == 0) {
			return "";
		} else if (size == 1) {
			return itemset.get(0).toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			for (int i = 0; i < size; i++) {
				if (i < size - 1) {
					sb.append(itemset.get(i) + ", ");
				} else {
					sb.append(itemset.get(i));
				}
			}
			sb.append(")");
			return sb.toString();
		}
	}
}
