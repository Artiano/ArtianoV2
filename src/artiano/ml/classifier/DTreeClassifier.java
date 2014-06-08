package artiano.ml.classifier;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;

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
	private ArrayList<String> attributeList = new ArrayList<String>(); 	
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
		ArrayList<String> copyOfAttList = new ArrayList<String>(attributeList);
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
		NominalAttribute predictionList = new NominalAttribute();				
		for(int i=0; i<dataSet.rows(); i++) {
			TableRow singleItem = dataSet.row(i);  //一条数据
			
			DTreeNode current = root;
			int matchNum = 0;		//到目前为止已经匹配的属性数目
			boolean searchComplete = false;
			while(current.nextNodes.size() >= 1) {
				String attribute = current.attribute;							
				int indexOfAttr = this.attributeList.indexOf(attribute);				
				Object valueSearched = singleItem.at(indexOfAttr); //Value
				
				/* The discrete value is not exist. */
				ArrayList<ArrayList<Object>> attributeValueList = 
					constructAttributeValueList(this.trainset, this.attributeList);
				if(!attributeValueList.get(indexOfAttr).contains(valueSearched)) {
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
						matchNum++;												
						break;
					}
				}								
				
				if(searchComplete) {
					break;
				}					
			}
			 
			if(matchNum == attributeList.size() ) {   //匹配完成
				if(! "".equals(current.label)) {					
					predictionList.push(current.label);
				} 		
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
			Table remainingTrainSet, ArrayList<String> remainingAttribute) {		
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
			Table remainingTrainSet,
			ArrayList<String> remainingAttribute) {		
		int indexOfAttr = remainingAttribute.indexOf(p.attribute);
		ArrayList<ArrayList<Object>> attributeValueList = 
			constructAttributeValueList(remainingTrainSet, remainingAttribute);	
		ArrayList<Object> attrValues = attributeValueList.get(indexOfAttr);
		
		//更新剩下的属性列表
		remainingAttribute.remove(p.attribute);
		ArrayList<String> newRemainingAttribute = remainingAttribute; 
		
		//属性的每一个值都将作为当前节点的子树
		int attrIndexInOrigin = this.attributeList.indexOf(p.attribute);
		for(int j=0; j<attrValues.size(); j++) {		
			Table newRemainingData = 
				getNewRemainingData(remainingTrainSet, attrIndexInOrigin, attrValues.get(j));	
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
	
	//更新还未得到匹配的数据集
	private Table getNewRemainingData(
			Table remainingTrainSet, int indexOfAttr, Object attrValue) {
		Table newRemainingData = new Table();
		//添加属性
		Iterator<Attribute> attrIter = remainingTrainSet.attributes();
		while(attrIter.hasNext()) {
			Attribute attr = attrIter.next();
			if(attr instanceof NumericAttribute) {
				newRemainingData.addAttribute(
					new NumericAttribute(attr.getName(), new IncrementVector()));
			} else if(attr instanceof NominalAttribute) {
				newRemainingData.addAttribute( 
					new NominalAttribute(attr.getName(), new IncrementVector()));
			}			
		}
		//指定类标属性
		newRemainingData.setClassAttribute(remainingTrainSet.classIndex());
		
		//添加属性值
		for(int i=0; i<remainingTrainSet.rows(); i++) {
			TableRow currentItem = remainingTrainSet.row(i);
			if(attrValue.equals(currentItem.at(indexOfAttr))) {
				newRemainingData.push(currentItem);
			}
		}	
		return newRemainingData;
	}
	
	// 得到剩下的属性中拥有最大信息增益的那个属性在属性列表中的下标
	private int getMaxInformationGainAttribute(
			Table remainingTrainSet, ArrayList<String> remainingAttribute) {
		double max_gain = 0;
		int max_index = 0;	//拥有最大信息增益的属性的下标  
		for(int i=0; i<remainingAttribute.size(); i++) {			
			//获取当前属性的信息增益
			double temp_gain = 
				computeInformationGain(remainingTrainSet, remainingAttribute.get(i));
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		return max_index;
	}
	
	/**
	 * 计算特定属性的信息增益
	 * @param remainingData 还未被分类的数据集
	 * @param attribute 当前要计算信息增益的属性
	 * @return 属性的信息增益
	 */
	private double computeInformationGain(Table remainingTrainSet, 
			Object attribute) {		
		double inforGain = 0;								
		//Add entropy(S);
		ArrayList<Integer> labelCounts =  
			countAttributeValuesApperances(remainingTrainSet.classAttribute());
		for(int i=0; i<labelCounts.size(); i++) {
			double temp = labelCounts.get(i) * 1.0 / remainingTrainSet.rows();
			inforGain += -1 * temp * Math.log10(temp) / Math.log10(2);
		}
		
		//统计该属性的每一个值的出现次数
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount =				
			countAttrValuesApperances(remainingTrainSet, indexOfAttr);		
				
		//获取下标为indexOfAttr的属性剩下的值(相同的值只取一次)
		ArrayList<Object> attrValues = new ArrayList<Object>();
		for(int i=0; i<remainingTrainSet.rows(); i++) {
			TableRow currentSample = remainingTrainSet.row(i);
			if(!attrValues.contains(currentSample.at(indexOfAttr))) {
				attrValues.add(currentSample.at(indexOfAttr));
			}
		}					
		for(int j=0; j<eachCount.size(); j++) {
			double entropy = 
				getEntropy(remainingTrainSet, indexOfAttr, attrValues.get(j));			
			inforGain += 
				(-1.0 * eachCount.get(j)) / remainingTrainSet.rows() * entropy;
		}		
		return inforGain;
	}
	
	/**
	 * 计算属性的一个值的熵
	 * @param remainingTrainSet - 还未被分类的数据集
	 * @param attrIndex - 当前需要计算属性值的信息增益的属性在属性列表中的下标
	 * @param attrValue - 当前使用的属性值
	 * @return 该属性值的信息增益
	 */
	private double getEntropy(Table remainingTrainSet,
			int attrIndex, Object attrValue) {
		ArrayList<Integer> labelValueCounts =
			countLabelsForSpecifiedAttrValue(remainingTrainSet, attrIndex, attrValue);		
		
		int attributeValueCount = 0;
		//如果某个类标号的计数为0，则熵为0
		for(int i=0; i<labelValueCounts.size(); i++) {
			attributeValueCount += labelValueCounts.get(i);
			if(labelValueCounts.get(i) == 0) {
				return 0;
			}
		}
		
		/* 计算熵 */		
		double entropy = 0;		//该属性值的熵		
		for(int i=0; i<labelValueCounts.size(); i++) {
			double temp = labelValueCounts.get(i) * 1.0 / attributeValueCount;
			entropy += -1 * temp * Math.log10(temp) / Math.log10(2);
		}		
		return entropy;
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

	//统计属性的每一个值的出现次数
	private ArrayList<Integer> countAttributeValuesApperances(
			Attribute remainingTrainLabel) {
		Map<String, Integer> attrValueCountsMap =
				new LinkedHashMap<String, Integer>();
		for(int i=0; i<remainingTrainLabel.size(); i++) {
			String attrValue = remainingTrainLabel.get(i).toString();
			if(!attrValueCountsMap.containsKey(attrValue)) {
				attrValueCountsMap.put(attrValue, 1);
			} else {
				attrValueCountsMap.put(attrValue, attrValueCountsMap.get(attrValue) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;
	}
	
	//统计属性的每一个值的出现次数
	private ArrayList<Integer> countAttrValuesApperances(
			Table remainingTrainSet, int indexOfAttr) {
		Map<Object, Integer> attrValueCountsMap =
				new LinkedHashMap<Object, Integer>();
		for(int i=0; i<remainingTrainSet.rows(); i++) {
			TableRow singleData = remainingTrainSet.row(i);
			Object attrValue = singleData.at(indexOfAttr);
			if(!attrValueCountsMap.containsKey(attrValue)) {
				attrValueCountsMap.put(attrValue, 1);
			} else {
				attrValueCountsMap.put(attrValue, attrValueCountsMap.get(attrValue) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;
	}

	//统计特定属性取某一特定值是对应的数据集中，各个类标的出现次数
	private ArrayList<Integer> countLabelsForSpecifiedAttrValue(
			Table trainset, int indexOfAttr, Object attrValue) {
		Attribute classAttr = trainset.classAttribute(); //类标属性 
		Map<Object, Integer> attrValueCountsMap =
				new LinkedHashMap<Object, Integer>();
		for(int i=0; i<trainset.rows(); i++) {
			TableRow singleData = trainset.row(i);
			if(!singleData.at(indexOfAttr).equals(attrValue)) {
				continue;
			}
			
			
			Object label = classAttr.get(i);
			if(!attrValueCountsMap.containsKey(label)) {
				attrValueCountsMap.put(label, 1);
			} else {
				attrValueCountsMap.put(label, attrValueCountsMap.get(label) + 1);
			}
		}	
		ArrayList<Integer> attrValueCounts = 
			new ArrayList<Integer>(attrValueCountsMap.values());
		return attrValueCounts;		
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
	 * 构造不含有重复的属性列表 attributeValueList
	 * @param trainSet - 数据集
	 * @param attributeList - 属性列表
	 */
	private ArrayList<ArrayList<Object>> constructAttributeValueList(
			Table trainSet, ArrayList<String> attributeList) {
		ArrayList<ArrayList<Object>> attributeValueList = 
			new ArrayList<ArrayList<Object>>();
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
