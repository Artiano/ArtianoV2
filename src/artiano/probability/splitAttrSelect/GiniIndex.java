package artiano.probability.splitAttrSelect;

import java.util.*;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.StringAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;

/**
 * Gini系数，用于特征选择
 */
public class GiniIndex {
	/**
	 * 计算数据集在给定特征上的gini系数
	 * @param dataset 数据集
	 * @param featIndex 特征
	 * @return
	 */
	public double calcGiniIndex(Table dataset, int featIndex) {
		if(featIndex < 0 || featIndex >= dataset.columns()) {
			throw new IndexOutOfBoundsException("featIndex is out of bounds.");
		}
		
		//创建唯一的分类标签列表
		Set<Object> uniqueAttrValues = getUniqueAttrValues(dataset, featIndex);
		double giniIndex = 0.0;
		for(Object featValue : uniqueAttrValues) {
			Table subDataset = splitDataset(dataset, featIndex, featValue);
			double prob = subDataset.rows()/(dataset.rows() * 1.0);
			giniIndex += prob * calcGiniIndexOnFeatureValue(subDataset);
		}
		return giniIndex;
	}
	
	/**
	 * 创建唯一的分类标签列表
	 * @param dataset 数据集
	 * @param featIndex 特征下标
	 * @return
	 */
	private Set<Object> getUniqueAttrValues(Table dataset, int featIndex) {
		int numEntries = dataset.rows();
		List<Object> labelValues = new ArrayList<Object>();
		for(int i=0; i<numEntries; i++) {
			labelValues.add(dataset.at(i, featIndex));
		}
		return new HashSet<Object>(labelValues);
	}

	/**
	 * 按照给定特征划分数据集
	 * @param dataset 待划分数据集
	 * @param featIndex 特征对应下标
	 * @param featValue 特征取的值
	 * @return
	 */
	private Table splitDataset(Table dataset, int featIndex, Object featValue) {
		Table dataAfterSplit = new Table();
		addAttributes(dataset, dataAfterSplit);
		dataAfterSplit.setClassAttribute(dataset.classIndex());
		
		int numEntries = dataset.rows();
		for(int i=0; i<numEntries; i++) {
			TableRow row = dataset.row(i);
			if(row.at(featIndex).equals(featValue)) {
				dataAfterSplit.push(row);
			}
		}
		return dataAfterSplit;
	}
	
	private void addAttributes(Table srcData, Table dstData) {
		Iterator<Attribute> attrIter = srcData.attributes();
		while(attrIter.hasNext()) {
			Attribute attr = attrIter.next();
			if(attr instanceof StringAttribute) {
				dstData.addAttribute(new StringAttribute());
			} else if(attr instanceof NumericAttribute) {
				dstData.addAttribute(new NumericAttribute());
			} else if(attr instanceof NominalAttribute) {
				dstData.addAttribute(new NominalAttribute());
			} 
		}
	}

	private double calcGiniIndexOnFeatureValue(Table dataset) {
		//统计类标属性的所有可能值的出现次数
		Map<Object, Integer> labelCounts = countLabels(dataset);
		double numEntries = dataset.rows(); 
		double result = 1.0;
		Set<Object> labelSet = labelCounts.keySet();
		for(Object labelValue : labelSet) {
			double prob = labelCounts.get(labelValue) / numEntries;
			result -= Math.pow(prob, 2);
		}
		return result;
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
