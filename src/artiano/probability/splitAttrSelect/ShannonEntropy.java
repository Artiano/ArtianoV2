package artiano.probability.splitAttrSelect;

import java.util.*;

import artiano.core.structure.Table;

/**
 * 香农熵计算类
 */
public class ShannonEntropy {
	/**
	 * 计算给定数据集的香农熵
	 * @param dataset 待计算香农熵数据集
	 * @return 数据集的香农熵
	 */
	public double calcShannonEntropy(Table dataset) {
		//为所有可能分类创建字典
		Map<Object, Integer> labelCounts = countLabels(dataset);
		
		double shannonEnt = 0.0;
		double numEntries = dataset.rows();
		Set<Object> labelSet = labelCounts.keySet();
		for(Object label : labelSet) {
			double prob = labelCounts.get(label) / numEntries;
			shannonEnt -= prob * Math.log10(prob) / Math.log10(2);
		}
		return shannonEnt;
	}

	/**
	 * 统计类标属性的所有可能值的出现次数
	 * @param dataset 数据集
	 * @return 类标属性值 - 相应类标属性值出现次数组成的键值对
	 */
	private Map<Object, Integer> countLabels(Table dataset) {
		int numEntries = dataset.rows();
		int classIndex = dataset.classIndex();		
		Map<Object, Integer> labelCounts = new HashMap<Object, Integer>();
		for(int i=0; i<numEntries; i++) {
			Object label = dataset.at(i, classIndex);
			if(!labelCounts.containsKey(label)) {
				labelCounts.put(label, 1);
			} else {
				labelCounts.put(label, labelCounts.get(label)+1);
			}
		}
		return labelCounts;
	}
}
