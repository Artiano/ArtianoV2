package artiano.probability.splitAttrSelect;

import java.util.*;

import artiano.core.structure.Table;

/**
 * 信息增益率计算类
 */
public class InformationGainRatio {
	/**
	 * 计算数据集在给定特征上的信息增益率
	 * @param dataset 数据集
	 * @param featIndex 特征
	 * @return 信息增益率
	 */
	public double calcInfoGainRatio(Table dataset, int featIndex) {
		InformationGain IG = new InformationGain();
		// 计算信息增益
		double informationGain = IG.calcInformationGain(dataset, featIndex);
		
		// 统计属性的所有可能值的出现次数
		Map<Object, Integer> featValueCounts = 
				countFeatValues(dataset, featIndex);		
		Set<Object> featValues = featValueCounts.keySet();
		
		double numEntries = dataset.rows(); 
		double IV = 0.0;
		for(Object featValue : featValues) {
			double prob = featValueCounts.get(featValue) / numEntries;
			IV -= prob * Math.log10(prob) / Math.log10(2);
		}
		System.out.println("IV: " + IV);
		return informationGain/IV;
	}
	
	/**
	 * 统计属性的所有可能值的出现次数
	 * @param dataset 数据集
	 * @return 类标属性值 - 相应类标属性值出现次数组成的键值对
	 */
	private Map<Object, Integer> countFeatValues(Table dataset, int featIndex) {
		int numEntries = dataset.rows();
		Map<Object, Integer> featValueCounts = new HashMap<Object, Integer>();
		for(int i=0; i<numEntries; i++) {
			Object featValue = dataset.at(i, featIndex);
			if(!featValueCounts.containsKey(featValue)) {
				featValueCounts.put(featValue, 1);
			} else {
				featValueCounts.put(featValue, featValueCounts.get(featValue)+1);
			}
		}
		return featValueCounts;
	}
	
}
