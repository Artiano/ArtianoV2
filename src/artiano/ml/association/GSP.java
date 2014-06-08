package artiano.ml.association;

import java.util.*;
import artiano.ml.association.structure.*;

public class GSP<T extends Comparable<T>> {
	private List<Sequence<T>> sequenceList;   //最初的序列模式
	private List<Sequence<T>> candidatePattern;		//长度为i的候选序列模式
	private List<Sequence<T>> sequencePattern;		//长度为i的序列模式
	private List<Sequence<T>> result;   //最终的序列模式
	private int minSupport;				//最小支持度
	
	/**
	 * 构造函数
	 * @param sequenceList 最初的序列模式
	 * @param minSupport 最小支持度
	 */
	public GSP(List<Sequence<T>> sequenceList, int minSupport) {
		this.sequenceList = Collections.unmodifiableList(sequenceList);
		this.minSupport = minSupport;
		this.result = new ArrayList<Sequence<T>>();
	}
	
    // 产生序列模式    
	// 核心方法，在该方法中调用连接和剪枝操作，并将最后获得的序列模式放到result中
    public List<Sequence<T>> getSequences() {
    	//获取一项候选模式
    	intializeOneItemSequence();
        for(int i=0; i<sequencePattern.size(); i++) {
        	generateCandidatePattern();      //产生进行连接操作后的候选集
        	if (candidatePattern.size() == 0) {
                break;
            }     
        	pruning();         //剪枝        	
            generateL();      //产生序列模式            
            this.addToResult(sequencePattern);            
        }          
    	return this.sequencePattern;
    }
    
    //获取频繁一项集
    private void intializeOneItemSequence() {
    	Map<T, Integer> itemCountMap = countEachItem();    	    	
    	getOneItemSequence(itemCountMap);    //减掉小于最小支持度的项
    }

	private void getOneItemSequence(Map<T, Integer> itemCountMap) {
		this.sequencePattern = new ArrayList<Sequence<T>>();
    	Set<T> itemset = itemCountMap.keySet();
    	for(T item : itemset) {
    		if(itemCountMap.get(item) >= minSupport) {
    			Element<T> element = new Element<T>();
    			element.addItem(item);
    			Sequence<T> sequence = new Sequence<T>();
    			sequence.addElement(element);
    			sequencePattern.add(sequence);
    		}
    	}
    	//将第一次频繁序列模式加入结果集中
        this.addToResult(sequencePattern);
	}

	//统计各项出现的次数
	private Map<T, Integer> countEachItem() {
		Map<T, Integer> itemCountMap = new HashMap<T, Integer>();
    	for(Sequence<T> sequence : sequenceList) {
    		List<Element<T>> elements = sequence.getElements();
    		for(Element<T> element: elements) {
    			List<T> itemset = element.getItemset();
    			for(T item: itemset) { 
    				if(!itemCountMap.containsKey(item)) {
    					itemCountMap.put(item, 1);
    				} else {
    					itemCountMap.put(item, itemCountMap.get(item)+1);
    				}
    			}
    		}
    	}
		return itemCountMap;
	}

	//对于种子集sequencePattern进行连接操作
	private void generateCandidatePattern() {
		this.candidatePattern = new ArrayList<Sequence<T>>();		
		int size = sequencePattern.size();
		for(int i=0; i<size; i++) {
			for(int j=i; j<size; j++) {
				joint(sequencePattern.get(i), sequencePattern.get(j));
				if(i != j) {
					joint(sequencePattern.get(j), sequencePattern.get(i));
				}
			}
		}
	}
	
	//对种子集进行连接操作
    private void joint(Sequence<T> s1, Sequence<T> s2) {    	
    	//去除第一个元素
        Element<T> ef = s1.getElement(0).getWithoutFirstItem();
        //去除最后一个元素
        Element<T> ee = s2.getElement(s2.getSize() - 1).getWithoutLastItem();
        int i = 0, j = 0;
        if (ef.getSize() == 0) {
            i++;
        }

        for (; i < s1.getSize() && j < s2.getSize(); i++, j++) {
            Element<T> e1, e2; 
            if (i == 0) {
                e1 = ef;
            } else {
                e1 = s1.getElement(i);
            }

            if (j == s2.getSize() - 1) {
                e2 = ee;
            } else {
                e2 = s2.getElement(j);
            }
            if (!e1.equals(e2)) {
                return;
            }
        } //end of for                        
    	
        //将s2的最后一个元素添加到s1中
		Sequence<T> s = new Sequence<T>(s1);
		T item = s2.getElement(s2.getSize()-1).getLastItem();
		s.getElement(s.getSize()-1).addItem(item);				
		//如果候选集没有s，则添加到候选集
		if(s.notInSeqs(candidatePattern)) {
			candidatePattern.add(s);
		} 
		
		Sequence<T> st = new Sequence<T>(s1);
        //将s2的最后一个元素添加到st中
		Element<T> newEle = new Element<T>();
		T item1 = s2.getElement(s2.getSize()-1).getLastItem();
		newEle.addItem(item1);
        st.addElement(newEle);
        if(st.notInSeqs(candidatePattern)) {
        	candidatePattern.add(st);
        }
    }
    
    /*
     * 剪枝操作
     * 看每个候选序列的连续子序列是不是频繁序列
     * 采用逐个取元素，只去其中一个项目，然后看是不是有相应的频繁序列在sequencePattern中。
     * 如果元素只有一个项目，则去除该元素做相应判断。
     */
    private void pruning() {
    	//对于候选序列中的所有元素，判断是不是频繁序列    	
    	for(int i=0; i<candidatePattern.size(); i++) {
    		Sequence<T> sequence = candidatePattern.get(i);
    		//对于一个序列中的所有元素
    		for (int j = 0; j < sequence.getSize(); j++) {
    			Element<T> element = sequence.getElement(j);
    			boolean prune = false;
    			if(element.getSize() == 1) {  //元素只有一项
    				sequence.removeElement(j);
    				//如果子序列不是序列模式，则将它从候选序列模式中删除，否则添加
    				if(sequence.notInSeqs(sequencePattern)) {
    					prune = true;
    				}
    				sequence.insertElement(j, element);
    				
    			} else {
    				for(int k=0; k<element.getSize(); k++) {
    					T item = element.removeItem(k);
    					//如果子序列不是序列模式，则将它从候选序列模式中删除,否则添加
    					if(sequence.notInSeqs(sequencePattern)) {
    						prune = true;
    					}
    					element.addItem(k, item);   //attention: k == getSize(), insert
    				}
    			}
    			
    			if(prune) {   //如果剪枝，则将该序列删除
    				candidatePattern.remove(i);
    				i--;
    				break;
    			}
    		}
    	}
    }
    
    //生成序列模式L,用于已经经过连接和剪枝操作后的后选序列集
    private void generateL() {
    	this.sequencePattern = new ArrayList<Sequence<T>>();
    	for(Sequence<T> sequence: sequenceList) {
    		for(Sequence<T> seq: this.candidatePattern) { 
    			if(seq.isSubsequenceOf(sequence)) {
    				seq.incrementSupport(); 	 //支持度计数加1
    			}
    		}
    	}
    	
    	for (Sequence<T> seq : this.candidatePattern) {
         //大于最小支持度阈值的放到序列模式中
         if (seq.getSupport() >= this.minSupport) {
             this.sequencePattern.add(seq);
         }
     }
    }
        
    // 将该频繁序列模式加入结果中
	private void addToResult(List<Sequence<T>> seq) {
		this.result.clear();
        for (int i = 0; i < seq.size(); i++) {
            this.result.add(seq.get(i));
        }
    }
}