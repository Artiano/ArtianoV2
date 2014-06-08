package artiano.probability.splitAttrSelect;

import java.util.*;

import artiano.core.structure.Attribute;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.NumericAttribute;
import artiano.core.structure.StringAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;

/**
 * 信息增益计算类
 */
public class InformationGain {
	/**
	 * 计算数据集在给定的特征上的信息增益
	 * @param dataset 数据集
	 * @param featIndex 特征的下标
	 * @return
	 */
	public double calcInformationGain(Table dataset, int featIndex) {
		if(featIndex < 0 || featIndex >= dataset.columns()) {
			throw new IndexOutOfBoundsException("featIndex is out of bounds.");
		}
		
		//创建唯一的分类标签列表
		Set<Object> uniqueAttrValues = getUniqueAttrValues(dataset, featIndex);
		
		ShannonEntropy shannonEnt = new ShannonEntropy();
		double baseEntropy = shannonEnt.calcShannonEntropy(dataset);
		double entropy = 0.0;
		double informationGain = 0.0;
		for(Object featValue : uniqueAttrValues) { //计算每种划分方式的信息熵
			Table subDataset = splitDataset(dataset, featIndex, featValue);
			double prob = subDataset.rows() / (dataset.rows() * 1.0);
			entropy += prob * shannonEnt.calcShannonEntropy(subDataset);			
		}
		informationGain = baseEntropy - entropy;
		return informationGain;
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
	
}
