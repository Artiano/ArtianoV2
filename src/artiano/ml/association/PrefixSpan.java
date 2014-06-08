package artiano.ml.association;

import java.util.*;

import artiano.ml.association.structure.*;

public class PrefixSpan<T extends Comparable<T>> {
	private List<Sequence<T>> sequenceList;		//最初的序列模式
	private int minSupport;			//最小支持度		
	private List<Sequence<T>> frequentSequence = 
		new ArrayList<Sequence<T>>();  //得到的频繁序列模式
	private Map<T, List<Sequence<T>>> sequencesForItem;
	
	/**
	 * 构造函数
	 * @param sequenceList 最初的序列模式
	 * @param minSupport 最小支持度
	 */
	public PrefixSpan(List<Sequence<T>> sequenceList, int minSupport) {
		super();
		this.sequenceList = Collections.unmodifiableList(sequenceList);
		this.minSupport = minSupport;
	}

	/**
	 * 找出序列模式
	 * @return 序列模式
	 */
	public List<Sequence<T>> getSeuqencePattern() {
		//找出频繁单项
		List<T> frequentSingleItem = getFrequentSingleItem();
		//深度复制序列列表
		List<Sequence<T>> cloneOfSequenceList = cloneSequenceList(sequenceList);
		//移除所有序列中的非频繁单项
		removeNotFrequentItem(cloneOfSequenceList, frequentSingleItem);
		//分别获取包含每一个频繁项的序列列表
		sequencesForItem =
			getRelativeSequencesForItem(cloneOfSequenceList, frequentSingleItem);
		//深度复制每个频繁单项对应的序列Map		
		Map<T, List<Sequence<T>>> cloneOfSequencesForItem = 
			cloneSequencesForItem(sequencesForItem);
		//为频繁单项生成投影数据库
		generateProjectedDBForFrequentSingleItems(cloneOfSequencesForItem);
		
		//分别寻找以一频繁单项为开始的所有频繁序列
		Set<T> itemset = sequencesForItem.keySet();
		for(T item : itemset) {
			Sequence<T> sequence = constructSequenceWithItem(item);			
			List<Sequence<T>> sequencesOfItem = new ArrayList<Sequence<T>>();			
			sequencesOfItem.add(sequence);
			//寻找以频繁单项item为开始的所有频繁序列
			getFreqSequencesForFreqItem(sequence, 0, cloneOfSequencesForItem.get(item), sequencesOfItem);
			for(Sequence<T> seq: sequencesOfItem) {
				if(!frequentSequence.contains(seq)) {
					frequentSequence.add(seq);
				}
			}
		}		
		return this.frequentSequence;
	}
	
	/**
	 * 构造单项构成的序列 
	 * @param item 单项
	 * @return 单项构成的序列序列
	 */
	private Sequence<T> constructSequenceWithItem(T item) {
		List<Element<T>> elementList = new ArrayList<Element<T>>();
		Element<T> ele = new Element<T>();
		ele.addItem(item);
		elementList.add(ele);
		Sequence<T> sequence = new Sequence<T>(elementList);
		return sequence;
	}
	
	/**
	 * 找出所有已某一频繁单项作为开始的频繁序列(频繁模式) 
	 * @param sequence 序列
	 * @param length 当前频发序列的长度
	 * @param projectedDB 投影数据库
	 * @param frequentSequencesOfItem 频繁序列
	 * @return 所有的频繁序列
	 */
	private List<Sequence<T>> getFreqSequencesForFreqItem(Sequence<T> sequence, int length,
			List<Sequence<T>> projectedDB, List<Sequence<T>> frequentSequencesOfItem) {
		int sizeOfProjectedDB = projectedDB.size();
		if(sizeOfProjectedDB == 0) {       //已经找到所有的频繁模式
			return frequentSequencesOfItem;
		}
		
		//频繁单项
		T singleFrequentItem = sequence.getElement(0).getFirstItem(); 
		List<Sequence<T>> sequenceList = sequencesForItem.get(singleFrequentItem);
				
		for(int i=0; i<sizeOfProjectedDB; i++) {
			Sequence<T> currentSequence = projectedDB.get(i);
			Element<T> element = currentSequence.getElement(0);
			T firstItem = element.removeFirstItem();
			if(element.getSize() == 0) {
				currentSequence.removeElement(element);				
			}
			
			/* 将firstItem添加到sequence的最后一个元素中并为序列模式,
			 * 判断该模式是不是频繁模式 */ 
			addFrequentSeqWithItemAdded(sequence, length, projectedDB,
					frequentSequencesOfItem, firstItem);
			/* 将firstItem作为sequence的最后一个元素并为序列模式,
			 * 判断该模式是不是频繁模式 */
			addFrequentSeqWithElementAdded(sequence, length, projectedDB,
					frequentSequencesOfItem, sequenceList, firstItem);
		}
		return frequentSequencesOfItem;
	}

	/* 将firstItem作为sequence的最后一个元素并为序列模式,
	 * 判断该模式是不是频繁模式 */
	@SuppressWarnings("unchecked")
	private void addFrequentSeqWithElementAdded(Sequence<T> sequence,
			int length, List<Sequence<T>> projectedDB,
			List<Sequence<T>> frequentSequencesOfItem,
			List<Sequence<T>> sequenceList, T firstItem) {		
		Sequence<T> copy2_Of_Sequence = new Sequence<T>(sequence);
		copy2_Of_Sequence.addElement(new Element<T>((T[])new Comparable[]{firstItem}));
		for(Sequence<T> current_sequence: sequenceList) {
			if(copy2_Of_Sequence.isSubsequenceOf(current_sequence)) {
				copy2_Of_Sequence.incrementSupport();
			}
		}
		if(copy2_Of_Sequence.getSupport() >= minSupport) {  //频繁模式
			if(!frequentSequencesOfItem.contains(copy2_Of_Sequence)) {
				frequentSequencesOfItem.add(copy2_Of_Sequence);
			}
			//获得对应的投影数据库
			List<Sequence<T>> newProjectedDB = cloneSequenceList(projectedDB);
			generateProjectedDB(firstItem, newProjectedDB);
			//递归调用
			getFreqSequencesForFreqItem(copy2_Of_Sequence, length+1, newProjectedDB, frequentSequencesOfItem);				
		}
	}

	/* 将firstItem添加到sequence的最后一个元素中并为序列模式,
	 * 判断该模式是不是频繁模式 */ 
	private void addFrequentSeqWithItemAdded(Sequence<T> sequence,
			int length, List<Sequence<T>> projectedDB,
			List<Sequence<T>> frequentSequencesOfItem, T firstItem) {		
		//频繁单项
		T singleFrequentItem = sequence.getElement(0).getFirstItem(); 
		List<Sequence<T>> sequenceList = sequencesForItem.get(singleFrequentItem);
		//深度复制sequence
		Sequence<T> copy1_Of_Sequence = new Sequence<T>(sequence);
		copy1_Of_Sequence.getElement(sequence.getSize()-1).addItem(firstItem);			
		for(Sequence<T> current_sequence: sequenceList) {
			if(copy1_Of_Sequence.isSubsequenceOf(current_sequence)) {
				copy1_Of_Sequence.incrementSupport();
			}
		}
		if(copy1_Of_Sequence.getSupport() >= minSupport) {  //频繁模式
			if(!frequentSequencesOfItem.contains(copy1_Of_Sequence)) {
				frequentSequencesOfItem.add(copy1_Of_Sequence);
			}
			//获得对应的投影数据库
			List<Sequence<T>> newProjectedDB = cloneSequenceList(projectedDB);
			generateProjectedDB(firstItem, newProjectedDB);
			//递归调用
			getFreqSequencesForFreqItem(copy1_Of_Sequence, length+1, newProjectedDB, frequentSequencesOfItem);
		}
	}
	
	//找出频繁单项
	private List<T> getFrequentSingleItem() {
		Map<T, Integer> singleItemCountMap = countEachItem();  //Count each item		
		List<T> frequentSingleItem = getFrequentSingleItem(singleItemCountMap);
		return frequentSingleItem;
	}

	private List<T> getFrequentSingleItem(Map<T, Integer> singleItemCountMap) {
		List<T> frequentSingleItem = new ArrayList<T>();
		Set<T> itemset = singleItemCountMap.keySet();
		for(T item : itemset) {
			if(singleItemCountMap.get(item) >= minSupport) {
				frequentSingleItem.add(item);
			}
		}
		return frequentSingleItem;
	}

	private Map<T, Integer> countEachItem() {
		Map<T, Integer> singleItemCountMap = new HashMap<T, Integer>();
		for(Sequence<T> sequence: sequenceList) {			
			List<Element<T>> elementList = sequence.getElements();
			for(Element<T> element: elementList) {
				List<T> itemset = element.getItemset();
				for(T item : itemset) {
					if(!singleItemCountMap.containsKey(item)) {
						singleItemCountMap.put(item, 1);
					} else {
						singleItemCountMap.put(item, singleItemCountMap.get(item)+1);
					}
				}
			}
		}
		return singleItemCountMap;
	}

	//移除所有序列中的非频繁单项
	private void removeNotFrequentItem(List<Sequence<T>> sequenceList,
			List<T> frequentSingleItem) {
		for(Sequence<T> sequence: sequenceList) {
			List<Element<T>> elements = sequence.getElements();
			List<Element<T>> emptyElement = new ArrayList<Element<T>>();
			for(Element<T> element: elements) {
				List<T> itemset = element.getItemset();
				Iterator<T> e = itemset.iterator();    
			    while(e.hasNext()){    
				    T item = e.next();  				    
				    if(!frequentSingleItem.contains(item)){
				    	//不是频繁单项，删除
				    	e.remove();  
				    }  
			    }  			  
				if(itemset.size() == 0) { 
					emptyElement.add(element);
				}
			}
			//删除空的元素
			for(Element<T> ele: emptyElement) {
				elements.remove(ele);
			}			
		}		
	}
	
	//深度复制序列列表
	private List<Sequence<T>> cloneSequenceList(List<Sequence<T>> sequenceList) {
		List<Sequence<T>> cloneOfSequenceList = new ArrayList<Sequence<T>>();
		for(Sequence<T> sequence: sequenceList) {
			Sequence<T> copyOfSequence = new Sequence<>(sequence);
			cloneOfSequenceList.add(copyOfSequence);
		}
		return cloneOfSequenceList;
	}

	//深度复制每个频繁单项对应的序列Map
	private Map<T, List<Sequence<T>>> cloneSequencesForItem(Map<T, List<Sequence<T>>> sequencesForItem) {
		Map<T, List<Sequence<T>>> cloneOfSequencesForItem = 
			new HashMap<T,List<Sequence<T>>>();
		Set<T> itemset = sequencesForItem.keySet();
		for(T item : itemset) {
			List<Sequence<T>> sequenceList = sequencesForItem.get(item);
			cloneOfSequencesForItem.put(item, cloneSequenceList(sequenceList));
		}
		return cloneOfSequencesForItem;
	}
	
	//获取每个频繁单项对应的序列列表
	private Map<T, List<Sequence<T>>> getRelativeSequencesForItem(
		List<Sequence<T>> sequenceList, List<T> frequentSingleItem) {
		Map<T, List<Sequence<T>>> itemSequenceMap = 
			new HashMap<T, List<Sequence<T>>>();
		for(Sequence<T> sequence: sequenceList) {
			for(T item : frequentSingleItem) {
				if(sequence.containsItem(item)) {
					Sequence<T> copyOfSequence = new Sequence<T>(sequence);
					if(!itemSequenceMap.containsKey(item)) {
						List<Sequence<T>> sequences = new ArrayList<Sequence<T>>();
						sequences.add(copyOfSequence);
						itemSequenceMap.put(item, sequences);
					} else {
						itemSequenceMap.get(item).add(copyOfSequence);
					}
				}
			}
		} 
		return itemSequenceMap;
	}


	//为频繁单项生成投影数据库
	private void generateProjectedDBForFrequentSingleItems(Map<T, List<Sequence<T>>> sequencesForItem) {
		Set<T> itemset = sequencesForItem.keySet();
		for(T item: itemset) {
			List<Sequence<T>> sequenceList = sequencesForItem.get(item);
			generateProjectedDB(item, sequenceList);
		}
	}

	//为某一项产生投影数据库
	private void generateProjectedDB(T item, List<Sequence<T>> sequenceList) {
		List<Sequence<T>> emptySequence = new ArrayList<Sequence<T>>();
		for(Sequence<T> sequence : sequenceList) {
			List<Element<T>> elements = sequence.getElements();
			Iterator<Element<T>> iter = elements.iterator();
			List<Element<T>> emptyElement = new ArrayList<Element<T>>(); 
			while(iter.hasNext()) {
				Element<T> element = iter.next();
				boolean complete = false;
				List<T> items = element.getItemset();
				while(items.size()>0 && !complete) {
					T currentItem = items.remove(0);    //
					if(currentItem.equals(item)) {
						complete = true;
						break;
					}
				}	
				
				if(element.getSize() == 0) {
					//sequence.removeElement(element);
					emptyElement.add(element);					
				}
				if(complete) {
					break;
				}
			}
			for(Element<T> ele: emptyElement) {
				elements.remove(ele);
			}
			if(sequence.getElement(0).getSize() == 0) { 				
				emptySequence.add(sequence);  //序列已经为空
			}
		}
		
		for(Sequence<T> seq: emptySequence) {
			sequenceList.remove(seq);
		}
	}
}