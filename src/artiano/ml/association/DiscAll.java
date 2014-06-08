package artiano.ml.association;

import java.util.*;
import artiano.ml.association.structure.*;

/* Not implemented yet. */
@Deprecated
public class DiscAll<T extends Comparable<T>> {
	private List<Sequence<T>> sequenceList;		//最初的序列模式
	private int minSupport;			//最小支持度		
	private List<Sequence<T>> frequentSequence = 
		new ArrayList<Sequence<T>>();  //得到的频繁序列模式
	private Map<T, List<Sequence<T>>> sequencesForItem;
	
	public DiscAll(List<Sequence<T>> sequenceList, int minSupport) {
		super();
		this.sequenceList = Collections.unmodifiableList(sequenceList);
		this.minSupport = minSupport;
	}
	
	//获取序列模式
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
		
		//获取2-序列模式
		Map<Sequence<T>, List<Sequence<T>>> freqSequencesMap = 
			new HashMap<Sequence<T>, List<Sequence<T>>>();
		for(T freItem: frequentSingleItem) {
			Sequence<T> seq = new Sequence<T>(freItem);
			for(T item: frequentSingleItem) {
				//判断<(seq, item)>是不是2-序列模式
				addFrequentSeqWithItemAdded(freqSequencesMap, seq, item);	
				//判断<seq, item>是不是2-序列模式
				addFrequentSeqWithElementAdded(freqSequencesMap, seq, item);
			}
		}				
		
		return this.frequentSequence;
	}

	//判断<seq, item>是不是2-序列模式
	@SuppressWarnings("unchecked")
	private void addFrequentSeqWithElementAdded(
			Map<Sequence<T>, List<Sequence<T>>> freqSequencesMap,
			Sequence<T> seq, T item) {		
		Sequence<T> sequence_2 = new Sequence<T>(seq);
		sequence_2.addElement(new Element<T>((T[])(new Comparable[]{item})));
		
		//频繁单项
		T singleFrequentItem = seq.getElement(0).getFirstItem(); 
		//包含频繁单项singleFrequentItem的序列列表
		List<Sequence<T>> sequenceList = sequencesForItem.get(singleFrequentItem);
		
		for(Sequence<T> current_sequence: sequenceList) {
			if(sequence_2.isSubsequenceOf(current_sequence)) {
				sequence_2.incrementSupport();
				if(!freqSequencesMap.containsKey(sequence_2)) {
					List<Sequence<T>> seqList = new ArrayList<Sequence<T>>();
					seqList.add(current_sequence);
					freqSequencesMap.put(sequence_2, seqList);							
				} else {
					freqSequencesMap.get(sequence_2).add(current_sequence);
				}
			}
		}
		if(sequence_2.getSupport() < minSupport) {
			//不是2-序列模式，从freqSequencesMap中移除
			freqSequencesMap.remove(sequence_2);
		}
	}

	//判断<(seq, item)>是不是2-序列模式
	private void addFrequentSeqWithItemAdded(
			Map<Sequence<T>, List<Sequence<T>>> freqSequencesMap,
			Sequence<T> seq, T item) {
		Sequence<T> sequence = new Sequence<T>(seq);
		sequence.getElement(sequence.getSize()-1).addItem(item);
		for(Sequence<T> current_sequence: sequenceList) {
			if(sequence.isSubsequenceOf(current_sequence)) {
				sequence.incrementSupport();
				if(!freqSequencesMap.containsKey(sequence)) {
					List<Sequence<T>> seqList = new ArrayList<Sequence<T>>();
					seqList.add(current_sequence);
					freqSequencesMap.put(sequence, seqList);							
				} else {
					freqSequencesMap.get(sequence).add(current_sequence);
				}
			}
		}
		if(sequence.getSupport() < minSupport) { 
			//不是2-序列模式，从freqSequencesMap中移除
			freqSequencesMap.remove(sequence);
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
	
	//深度复制序列列表
	private List<Sequence<T>> cloneSequenceList(List<Sequence<T>> sequenceList) {
		List<Sequence<T>> cloneOfSequenceList = new ArrayList<Sequence<T>>();
		for(Sequence<T> sequence: sequenceList) {
			Sequence<T> copyOfSequence = new Sequence<>(sequence);
			cloneOfSequenceList.add(copyOfSequence);
		}
		return cloneOfSequenceList;
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

}
