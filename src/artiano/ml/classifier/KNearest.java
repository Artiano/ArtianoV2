package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.structure.Matrix;
import artiano.core.structure.NominalAttribute;
import artiano.core.structure.Table;
import artiano.core.structure.Table.TableRow;
import artiano.ml.classifier.KDTree.KDNode;

public class KNearest extends Classifier {
	private static final long serialVersionUID = 2277585000325381124L;
	
	private KDTree kdTree;			//kd-tree	
	private int k = 2;				//近邻的数目
	
	public KNearest() {	
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	/**
	 * 训练数据
	 * @param trainData - 训练集
	 * @param trainLabel - 类标
	 * @return - 训练是否成功
	 */
	public boolean train(Table trainData) {
		try {
			isTrainingDataValid(trainData);						
		} catch(NullPointerException e) {
			return false;
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}						
		kdTree = new KDTree(trainData);  //构造KD-Tree 		
		return true;
	}

	/**
	 * 使用构造的KD树对输入的数据进行分类
	 * @param samples 待分类的数据集
	 * @return 输入数据的类标构成的向量
	 */
	public NominalAttribute predict(Table samples) {
		Matrix sampleMat = samples.toMatrix();
		NominalAttribute results = new NominalAttribute("label");
		for(int i=0; i<samples.rows(); i++) {
			results.push(findKNearestForSingleSample(sampleMat.row(i), k));
		}
		return results;									
	}

	/**
	 * 找到数据集的分类
	 * @param samples - 待分类数据
	 * @return - 数据所划分的类标
	 */
	private Object findKNearestForSingleSample(Matrix samples, int k) {
		List<KDNode> nearestNode = kdTree.findKNearest(samples, k);		
		//Count each label
		Map<Object, Integer> eachLabelCount = countEackLabel(nearestNode);		
		//找出最频繁的类标
		Set<Entry<Object, Integer>> entrySet = eachLabelCount.entrySet();
		Object mostFreqLabel = null;
		int maxCount = 0;
		for(Entry<Object, Integer> entry : entrySet) {
			if(maxCount < entry.getValue()) {
				maxCount = entry.getValue();
				mostFreqLabel = entry.getKey();
			}
		}		
		return mostFreqLabel;
	}

	/**
	 * 统计每一个类标出现的次数
	 * @param nearestNode - k-nearest邻居 
	 * @return 类标计数
	 */
	private Map<Object, Integer> countEackLabel(List<KDNode> nearestNode) {
		Map<Object, Integer> eachLabelCount = 
				new HashMap<Object, Integer>();
		for(int i=0; i<nearestNode.size(); i++) {
			KDNode node = nearestNode.get(i);
			Object nodeLabel = node.nodeLabel;
			if(!eachLabelCount.containsKey(nodeLabel)) {
				eachLabelCount.put(nodeLabel, 1);
			} else {
				eachLabelCount.put(nodeLabel, eachLabelCount.get(nodeLabel) + 1);
			}
		}
		return eachLabelCount;
	}			 
	
	/**
	 * 检查输入的训练数据是否合法
	 * 
	 * @throws IllegalArgumentException
	 *      trainData is null, or class attribute in parameter 
	 *      trainData is not appointed
	 */
	private void isTrainingDataValid(Table trainData) {
		if (trainData == null) {
			throw new IllegalArgumentException("训练集为空!");
		}
		if(trainData.classAttribute() == null) {
			throw new IllegalArgumentException("还未在trainData中指定类标!");
		}

		/* 检查训练数据是否全为数值型  */
		int rows = trainData.rows();
		int columns = trainData.columns();
		for(int i=0; i<rows; i++) {
			TableRow tableRow = trainData.row(i);
			for(int j=0; j<columns; j++) {
				Object obj = tableRow.at(j);
				if(! (obj instanceof Double)) {
					throw new IllegalArgumentException("训练数据只能为数值型!");
				}
			}
		}				
	}
	
}