package artiano.ml.classifier;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;
import artiano.probability.splitAttrSelect.InformationGain;

/**
 * <p> 决策树分类器(使用ID3算法实现)</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-2
 * @function 
 * @since 1.0.0
 */
public class DTreeClassifier extends Classifier {
	private static final long serialVersionUID = 1016638292310337476L;
	
	private Table trainset;			//训练集
	//数据属性列表
	private List<String> attributeList = new ArrayList<String>(); 	
	private DTreeNode root;		 //决策树的根节点
	
	public DTreeClassifier() {		
	}
			
	/** 决策树分类
	 * @param trainSet 训练集
	 * @return 数据训练是否成功
	 */
	public boolean train(Table trainSet) {		
		//检验待训练的数据是否合法
		try {
			isTrainDataInputedValid(trainSet);
		} catch(Exception e) {			
			return false;
		}
		initialize(trainSet);
		this.trainset = trainSet;
		
		//深度复制属性列表
		List<String> copyOfAttList = new ArrayList<String>(attributeList);
		copyOfAttList.remove(trainSet.classIndex());	//去掉类标属性
		// 构造决策树
		root = constructDecisionTree(root, trainset, copyOfAttList);		
		return true; 
	}				
	
	//初始化属性列表及训练集
	private void initialize(Table trainSet) {
		int columns = trainSet.columns();
		// 初始化属性列表
		attributeList = new ArrayList<String>();
		for(int j=0; j<columns; j++) {
			Attribute attr = trainSet.attribute(j);
			String attrName = attr.getName();
			attributeList.add(attrName);			
		}				
	}	
	
	/**
	 * 预测 
	 * @param samples 待分类数据集
	 * @return 待分类数据的类标号
	 */
	public NominalAttribute predict(Table dataSet) {
		if(dataSet == null) { 
			return null;
		}
		
		// 获取数据集每一维上所有可能出现的值得集合 
		List<List<Object>> attrValueList = 
				constructAttributeValueList(this.trainset, this.attributeList);
		
		NominalAttribute predictionList = new NominalAttribute();				
		for(int i=0; i<dataSet.rows(); i++) {
			TableRow singleItem = dataSet.row(i);  //一条数据			
			
			DTreeNode current = root;
			boolean searchComplete = false;
			while(current.nextNodes.size() >= 1) {
				String attribute = current.attribute;							
				int indexOfAttr = this.attributeList.indexOf(attribute);				
				Object valueSearched = singleItem.at(indexOfAttr); //Value						
				
				/* 当前维上出现未知值，中断此次搜索 */
				if(!attrValueList.get(indexOfAttr).contains(valueSearched)) {
					break;
				}
				
				searchComplete = false;
				//搜索一个属性的每一个可能值，来匹配待分类数据
				List<DTreeNode> childrenNodes = current.nextNodes;					
				for(int j=0; j<childrenNodes.size(); j++) {
					DTreeNode childNode = childrenNodes.get(j);
					//前一个属性得到匹配
					if(valueSearched.equals(childNode.previousDecision)) {
						//待分类的数据得到完全匹配
						if(childNode.nextNodes == null) { 							
							predictionList.push(childNode.label);
							searchComplete = true;
							break;
						}						
						current = childNode;	//准备匹配下一个属性的值												
						break;
					}
				}								
				
				if(searchComplete) {
					break;
				} 				
			}
			if(!searchComplete) {	//搜索失败
				predictionList.push("");
			}
				
		}		
		return predictionList;
	}	
	
	/**
	 * 构造决策树
	 * @param p 决策树的根节点
	 * @param remainingTrainSet 剩下的有待分类的数据
	 * @param remainingAttribute 还未进行匹配的属性
	 * @return 构造的决策树的根节点
	 */
	private DTreeNode constructDecisionTree(DTreeNode p, 
			Table remainingTrainSet, List<String> remainingAttribute) {		
		if(p == null) {
			p = new DTreeNode();
		}
	
		//剩下的数据的类标号相同，将当前节点的类标号置为剩下的那个类标号，决策树构造完成
		Attribute classAttr = remainingTrainSet.classAttribute();   //类标属性
		if(allTheSameLabel(classAttr)) {
			String label = classAttr.get(0).toString();
			p.label = label;
			return p;
		}			
				
		 /* 所有的属性都已经进行匹配，但还没有完成分类，则将剩下的数据中出现次数最多的类标号
		 	作为当前节点的类标号，并且停止构造决策树  */
		if(remainingAttribute.size() == 0) {
			p.label = mostCommonLabel(classAttr);
			return p;
		}
		
		/* 根据最大信息增益来决定哪个属性作为分裂属性 */
		int max_index = 
			getMaxInformationGainAttribute(remainingTrainSet, remainingAttribute);
		p.attribute = remainingAttribute.get(max_index);
		// 构造子决策树												
		constructSubTree(p, remainingTrainSet, remainingAttribute);								
		return p;
	}
			
	//构造子决策树
	private void constructSubTree(DTreeNode p,
			Table remainingTrainSet, List<String> remainingAttribute) {		
		int indexOfAttr = remainingAttribute.indexOf(p.attribute);
		//获取不含有重复值的属性值列表
		List<List<Object>> attributeValueList = 
			constructAttributeValueList(remainingTrainSet, remainingAttribute);	
		List<Object> attrValues = attributeValueList.get(indexOfAttr);
		
		//更新剩下的属性列表
		remainingAttribute.remove(p.attribute);
		List<String> newRemainingAttribute = remainingAttribute; 
		
		//属性的每一个值都将作为当前节点的子树
		int attrIndexInOrigin = this.attributeList.indexOf(p.attribute);
		for(int j=0; j<attrValues.size(); j++) {		
			Table newRemainingData = 
				splitDataset(remainingTrainSet, attrIndexInOrigin, attrValues.get(j));	
			DTreeNode new_node = new DTreeNode();  //子树的根节点
			new_node.previousDecision = attrValues.get(j);
			if(newRemainingData.rows() == 0) {	//这个分支已经没有节点
				new_node.label = mostCommonLabel(remainingTrainSet.classAttribute());
				if(p.nextNodes == null) {
					p.nextNodes = new ArrayList<DTreeNode>();
				}
				p.nextNodes.add(new_node);    //将子树的根节点加到节点p上
				break;
			} else {				
				constructDecisionTree(new_node, newRemainingData, 
						newRemainingAttribute);    
			}
			
			if(p.nextNodes == null) {
				p.nextNodes = new ArrayList<DTreeNode>();
			}
			p.nextNodes.add(new_node);    //将子树的根节点加到节点p上			
		}
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
	
	// 得到剩下的属性中拥有最大信息增益的那个属性在属性列表中的下标
	private int getMaxInformationGainAttribute(
			Table remainingTrainSet, List<String> remainingAttribute) {
		double max_gain = 0;
		int max_index = 0;	//拥有最大信息增益的属性的下标  
		InformationGain IG = new InformationGain();  //信息增益计算类
		for(int i=0; i<remainingAttribute.size(); i++) {
			int featIndex = 
					this.attributeList.indexOf(remainingAttribute.get(i));
			//获取当前属性的信息增益			
			double temp_gain =
				IG.calcInformationGain(remainingTrainSet, featIndex);	
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		return max_index;
	}	
	
	/**
	 * 检查数据集相应的类标号是不是全部相同
	 * @param classAttribute 类标属性
	 * @return 数据集的类表是不是完全相同，是则返回true;否则，返回false
	 */
	private boolean allTheSameLabel(Attribute classAttribute) {
		Object firstLabel = classAttribute.get(0);
		int size = classAttribute.size();
		for(int i=1; i<size; i++) {
			Object currentLabel = classAttribute.get(i);
			if(!firstLabel.equals(currentLabel)) {
				return false;
			}
		}		
		return true;
	}
	
	/**
	 *  搜索数据集中出现最频繁的类标
	 * @param classAttribute 剩余的类标
	 * @return 数据集中出现最频繁的类标
	 */
	private String mostCommonLabel(Attribute classAttribute) {
		Map<String, Integer> labelMap = new HashMap<String, Integer>();
		int rows = classAttribute.size();
		for(int i=0; i<rows; i++) {			
			String label = classAttribute.get(i).toString();
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, 1);
			} else {
				labelMap.put(label, labelMap.get(label) + 1);
			}
		}

		String comomLabel = "";
		int maxCount = 0;
		for(Map.Entry<String, Integer> entry : labelMap.entrySet()) {
			if(entry.getValue().intValue() > maxCount) {
				maxCount = entry.getValue().intValue();
				comomLabel = entry.getKey();				
			}
		}
		return comomLabel;
	}
	
	/**
	 *	检查输入的数据是否合法 
	 * @param trainSet 训练集
	 * @throws IllegalArgumentException trainData is null
	 */
	private void isTrainDataInputedValid(Table trainSet) {
		if(trainSet == null) {
			throw new IllegalArgumentException("Train set can not be null.");
		}
		if(trainSet.classAttribute() == null) {
			throw new IllegalArgumentException("Class attribute should be appointed!");
		}
	}
	
	/**
	 * 构造不含有重复值的属性值列表 attributeValueList
	 * @param trainSet - 数据集
	 * @param attributeList - 属性列表
	 */
	private List<List<Object>> constructAttributeValueList(
			Table trainSet, List<String> attributeList) {
		List<List<Object>> attributeValueList = 
			new ArrayList<List<Object>>();
		for(int i=0; i<attributeList.size(); i++) {
			attributeValueList.add(new ArrayList<Object>());
		}
		
		for(int i=0; i<trainSet.rows(); i++) {			
			TableRow item  = trainSet.row(i);
			for(int j=0; j<attributeList.size(); j++) {
				int attrIndex = this.attributeList.indexOf(attributeList.get(j));
				if(!attributeValueList.get(j).contains(item.at(attrIndex))) {
					attributeValueList.get(j).add(item.at(attrIndex));
				}													
			}
		}		
		return attributeValueList;
	}
		
	/* 决策树节点类 */
	private static class DTreeNode implements Serializable {		
		private static final long serialVersionUID = 1L;
		
		String attribute = "";	  //节点对应的属性
		Object previousDecision = null;   //前一个属性决策的值
		String label = "";	//类标(对于叶子节点)		
		ArrayList<DTreeNode> nextNodes;		//子决策树的引用	
	}

}
