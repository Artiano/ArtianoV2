package artiano.ml.classifier;

import java.io.*;
import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;

/**
 * <p>决策树分类器(使用C4.5算法实现)</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-10-15
 * @function 
 * @since 1.0.0
 */
public class DTreeClassifierUsingC4_5 extends Classifier{
	private static final long serialVersionUID = 4166947748580616301L;
	
	private ArrayList<ArrayList<String>> data = 
			new ArrayList<ArrayList<String>>();       //训练集
	private ArrayList<String> attributeList = 
			new ArrayList<String>(); 	//数据属性列表	
	private boolean[] isAttributeContinuous;
//	private String targetAttribute;  //target attribute
	private DTreeNode root;		 //Root of the decision tree constructed.	
	
	public DTreeClassifierUsingC4_5() {		
	}
	
	/**
	 * 决策树分类
	 * @return 数据训练是否成功，成功则返回true;否则,返回false
	 */
	public boolean train(Table trainSet) {		
		//检查输入的训练集及类标是否合法
		try {
			isTrainDataInputedValid();
		} catch(Exception e) {			
			return false;
		}
		intitialize(trainSet);  //初始化
		//深度复制trainLabel
		NominalAttribute copyOfTrainLabel = 
			deepCloneTrainLabel((NominalAttribute)trainSet.classAttribute());
		//构造决策树		
		root = constructDecisionTree(root, data, attributeList, copyOfTrainLabel);		
		return true;   //数据训练成功
	}

	//深度复制trainLabel
	private NominalAttribute deepCloneTrainLabel(NominalAttribute trainLabel) {
		NominalAttribute copyOfTrainLabel =
			new NominalAttribute(trainLabel.getName(), trainLabel.getVector().copy());
		Object[] nominals = trainLabel.nominalsArray();
		for(int i=0; i<nominals.length; i++) {
			copyOfTrainLabel.addNominal(nominals[i]);
		}
		return copyOfTrainLabel;
	}				
		
	private void intitialize(Table trainSet) {
		//初始化targetAttribute
	//	this.targetAttribute = trainLabel.getName();
		int columns = trainSet.columns();
		//初始化data
		this.data = tableToArrayList(trainSet);		
		//初始化 attributeList
		int classAttrIndex = trainSet.classIndex();	 //类标号属性的下标
		for(int j=0; j<columns; j++) {
			if(j == classAttrIndex) {  //如果是类标号属性，则不添加
				continue;
			}
			Attribute attr = trainSet.attribute(j);
			String attrName = attr.getName();
			attributeList.add(attrName);			
		}
		//attributeList.add(this.targetAttribute);
		//初始化isAttributeContinuous
		isAttributeContinuous = new boolean[columns];
		for(int j=0; j<columns; j++) {
			Attribute attr = trainSet.attribute(j);
			if(attr instanceof NominalAttribute) {  //离散型数据
				isAttributeContinuous[j] = false;
			} else if(attr instanceof NumericAttribute) {	//连续性数据
				isAttributeContinuous[j] = true;
			}
		}		
	}
	
	/**
	 * Classify data 
	 * @return classifications predicted of data.
	 */
	public NominalAttribute predict(Table data) {
		if(data == null) {  //Input empty
			return new NominalAttribute();
		}
		ArrayList<ArrayList<String>> samples = tableToArrayList(data);		
		NominalAttribute predictionAttr = new NominalAttribute("label"); //Store predictions
		for(int i=0; i<data.rows(); i++) {
			List<String> singleItem = samples.get(i);  //A sample
			
			DTreeNode current = root;
//			int matchNum = 0;		//Number of attribute matched.
			boolean searchComplete = false;
			while(current.nextNodes.size() >= 1) {
				String attribute = current.attribute;							
				int indexOfAttr = this.attributeList.indexOf(attribute);				
				String valueSearched = singleItem.get(indexOfAttr); //Value
				int attrIndexInOrigin = this.attributeList.indexOf(attribute);
				
				/* The discrete value is not exist. */
				ArrayList<ArrayList<String>> attributeValueList = 
					constructAttributeValueList(this.data, this.attributeList);
				if(!attributeValueList.get(indexOfAttr).contains(valueSearched)
						&& !isAttributeContinuous[attrIndexInOrigin]) {
					break;
				}				
				
				if(!isAttributeContinuous[attrIndexInOrigin]) {
					searchComplete = false;
					//Search each branch of an decision variable to match the sample
					List<DTreeNode> childrenNodes = current.nextNodes;					
					for(int j=0; j<childrenNodes.size(); j++) {
						DTreeNode childNode = childrenNodes.get(j);
						if(valueSearched.equals(childNode.previousDecision)) {  //Previous attribute match
							if(childNode.nextNodes == null) {  //All same label or exactly matched.
								predictionAttr.push(childNode.label);
								searchComplete = true;
								break;
							}
							
							current = childNode;	//Match with next branch of previous attribute.
	//						matchNum++;						
							
							break;
						}
					}								
					
					if(searchComplete) {
						break;
					}
					
				} else {
					searchComplete = false;										
		//			matchNum++;
					
					//Search each branch of an decision variable to match the sample
					List<DTreeNode> childrenNodes = current.nextNodes;
					double middleValue = 
						Double.parseDouble(childrenNodes.get(0).previousDecision);
					double value = Double.parseDouble(valueSearched);
					if(value < middleValue) {			
						current = current.nextNodes.get(0);						
					} else {
						current = current.nextNodes.get(1);											
					}									
					if(current.nextNodes == null) {	
						predictionAttr.push(current.label);
						break;
					}
				}				
			}
			/* 
			if(matchNum == attributeList.size() - 1 ) {   //Search complete.				
				predictionAttr.push(current.label);
			} else {  				
				int attributeIndex = this.attributeList.indexOf(current.attribute);
				if(!searchComplete && !isAttributeContinuous[attributeIndex]) {					
					//predictionAttr.push(null);   //Not exactly matched
				}							
			} */
		}
		
		return predictionAttr;
	}
	
	/**
	 * 构造决策树
	 * @param p 决策树的根节点
	 * @param remainingData 剩下的待使用的数据
	 * @param remainingAttribute 剩下的待使用的属性
	 * @return 构造的决策树的根节点
	 */
	private DTreeNode constructDecisionTree(DTreeNode p, 
			ArrayList<ArrayList<String>> remainingData, 
			ArrayList<String> remainingAttribute, 
			NominalAttribute remainingTrainLabel) {		
		if(p == null) {
			p = new DTreeNode();
		}
					
		// 检查是不是剩下的数据的类标都相同，如果是，则决策树构造完成
		if(allTheSameLabel(remainingTrainLabel)) {
			p.label = remainingTrainLabel.get(0).toString();
			return p;
		}		
				
		//All the attributes has been considered ,yet not complete the classification
		if(remainingAttribute.size() == 0 
			 && remainingData.size() > 0) {
			final int indexOfAttr = attributeList.indexOf(p.attribute);
			System.out.println("attr: " + p.attribute + ", index: " + indexOfAttr);
			if(indexOfAttr < 0) {
				return p;
			}
			if(!isAttributeContinuous[indexOfAttr]) {
				p.label = mostCommonLabel(remainingTrainLabel);
			}				
			return p;
		}
		
		/* Find decision variable by finding the max information gain. */
		int max_index = 
			getMaxGainRatioAttribute(remainingData, remainingAttribute, remainingTrainLabel);
		p.attribute = remainingAttribute.get(max_index);
		
		//Create sub tree
		final int indexOfAttr = attributeList.indexOf(p.attribute);		
		if(!isAttributeContinuous[indexOfAttr]) {													
			constructDiscreteAttributeSubTree(p, remainingData,
					remainingAttribute, remainingTrainLabel);			
		} else {  			
			constructContinuousSubTree(p, remainingData, 
					remainingAttribute, remainingTrainLabel);
		}						
		return p;
	}
	
	private void constructContinuousSubTree(DTreeNode p,
		ArrayList<ArrayList<String>> remainingData, 
		ArrayList<String> remainingAttribute,
		NominalAttribute remainingTrainLabel) {										
		ArrayList<ArrayList<String>> copyOfData = 
			new ArrayList<ArrayList<String>>(remainingData);
		final int indexOfAttr = this.attributeList.indexOf(p.attribute);
		Collections.sort(copyOfData, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				double value1 = Double.parseDouble(o1.get(indexOfAttr));
				double value2 = Double.parseDouble(o2.get(indexOfAttr));
				if(value1 > value2) {
					return 1;
				} else if(value1 == value2) {
					return 0;
				} else {
					return -1;
				}
			}
		});				
		
		int maxInfoGainIndex = 
			getMaxAttributeInfoGainIndex(copyOfData, remainingTrainLabel);										
		if(remainingData.size() == 0) {
			p.label = mostCommonLabel(remainingTrainLabel);
		}
		
		ArrayList<ArrayList<String>> leftChildData = 
			new ArrayList<ArrayList<String>>();
		NominalAttribute leftChildTrainLabel = 
			new NominalAttribute(remainingTrainLabel.getName(), new IncrementVector());
		for(int i=0; i<=maxInfoGainIndex; i++) {
			leftChildData.add(copyOfData.get(i));
			leftChildTrainLabel.push(remainingTrainLabel.get(i));
			leftChildTrainLabel.addNominal(remainingTrainLabel.get(i));
		}			
		
		ArrayList<ArrayList<String>> rightChildData = 
			new ArrayList<ArrayList<String>>();
		NominalAttribute rightChildTrainLabel = 
			new NominalAttribute(remainingTrainLabel.getName(), new IncrementVector());
		for(int i=maxInfoGainIndex+1; i<copyOfData.size(); i++) {
			rightChildData.add(copyOfData.get(i));
			rightChildTrainLabel.push(remainingTrainLabel.get(i));
			rightChildTrainLabel.addNominal(remainingTrainLabel.get(i));
		}
						
		double leftData = 
			Double.parseDouble(copyOfData.get(maxInfoGainIndex).get(indexOfAttr));
		double rightData = 
			Double.parseDouble(copyOfData.get(maxInfoGainIndex + 1).get(indexOfAttr));
		double middle = (leftData + rightData) / 2;
		System.out.println(p.attribute + ", left:  " + leftData 
			+ ", right: " + rightData);
		
		//Update remaining attributes		
		ArrayList<String> newRemainingAttribute = 
			new ArrayList<String>(remainingAttribute);	
		//newRemainingAttribute.remove(indexOfAttr);
		newRemainingAttribute.remove(this.attributeList.get(indexOfAttr));
		
		DTreeNode leftChildNode = new DTreeNode();  //Root of the sub tree
		leftChildNode.previousDecision = middle + "";
		leftChildNode.attribute = p.attribute;
		leftChildNode.label = mostCommonLabel(leftChildTrainLabel);		
		if(leftChildData.size() == 0) {	//Now has no sample of this branch
			leftChildNode.label = mostCommonLabel(remainingTrainLabel);
		} else {				
			constructDecisionTree(leftChildNode, leftChildData, 
					newRemainingAttribute, leftChildTrainLabel);    
		}
		
		DTreeNode rightChildNode = new DTreeNode();  //Root of the sub tree
		rightChildNode.attribute = p.attribute;
		rightChildNode.previousDecision = middle + "";
		rightChildNode.label = mostCommonLabel(rightChildTrainLabel);
		if(rightChildData.size() == 0) {	//Now has no sample of this branch
			rightChildNode.label = mostCommonLabel(remainingTrainLabel);
		} else {				
			constructDecisionTree(rightChildNode, rightChildData, 
					newRemainingAttribute, rightChildTrainLabel);    
		}	
		
		if(p.nextNodes == null) {
			p.nextNodes = new ArrayList<DTreeNode>();
		}
		p.nextNodes.add(leftChildNode);    //Add root of the sub tree to the node
		p.nextNodes.add(rightChildNode);    //Add root of the sub tree to the node			
	}	
	
	private void constructDiscreteAttributeSubTree(DTreeNode p,
			ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute,
			NominalAttribute remainingTrainLabel) {				
		int indexOfAttr = remainingAttribute.indexOf(p.attribute);
		ArrayList<ArrayList<String>> attributeValueList = 
			constructAttributeValueList(remainingData, remainingAttribute);	
		ArrayList<String> attrValues = attributeValueList.get(indexOfAttr);
		
		//Update remaining attributes		
		ArrayList<String> newRemainingAttribute = 
			new ArrayList<String>(remainingAttribute);
		newRemainingAttribute.remove(p.attribute);		
		
		//Each value of the attribute represents a branch of the decision tree
		int attrIndexInOrigin = this.attributeList.indexOf(p.attribute);
		for(int j=0; j<attrValues.size() && j<4; j++) {		
			ArrayList<ArrayList<String>> newRemainingData = 
				getNewRemainingData(remainingData, attrIndexInOrigin, attrValues.get(j));
			NominalAttribute newRemainingTrainLabel = 
				getNewRemainingTrainLabel(newRemainingData, attrIndexInOrigin,
						attrValues.get(j), remainingTrainLabel); 
			
			DTreeNode new_node = new DTreeNode();  //Root of the sub tree
			new_node.previousDecision = attrValues.get(j);
			if(newRemainingData.size() == 0) {	//Now has no sample of this branch
				new_node.label = mostCommonLabel(remainingTrainLabel);
				if(p.nextNodes == null) {
					p.nextNodes = new ArrayList<DTreeNode>();
				}
				p.nextNodes.add(new_node);    //Add root of the sub tree to the node
				break;
			} else {				
				constructDecisionTree(new_node, newRemainingData, 
						newRemainingAttribute, newRemainingTrainLabel);    
			}
			
			if(p.nextNodes == null) {
				p.nextNodes = new ArrayList<DTreeNode>();
			}
			p.nextNodes.add(new_node);    //Add root of the sub tree to the node			
		}
	}

	private int getMaxAttributeInfoGainIndex(
			ArrayList<ArrayList<String>> remainingData,
			NominalAttribute remainingTrainLabel) {		
		int maxInfoGainIndex = 0;
		double maxInfoGain = 0;
		for(int i=0; i<remainingData.size()-1; i++) {				
			String leftLabel = remainingTrainLabel.get(i).toString(); 
			String rightLabel = remainingTrainLabel.get(i+1).toString(); 
			if(leftLabel.equals(rightLabel)) {
				continue;
			}
			
			List<Object> nominals = remainingTrainLabel.nominals();
			double leftInfoGain = 0;
			double[] leftEachLabelCount = new double[nominals.size()]; 
			Arrays.fill(leftEachLabelCount, 0);
			for(int j=0; j<=i; j++) {
				int labelValueIndex = nominals.indexOf(remainingTrainLabel.get(i)); 
				leftEachLabelCount[labelValueIndex]++;
			}
						
			for(int j=0; j<nominals.size(); j++) {
				if(leftEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = ((double)leftEachLabelCount[j]) / (i+1);
				leftInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double[] rightEachLabelCount = new double[nominals.size()];
			Arrays.fill(rightEachLabelCount, 0);
			for(int j=i+1; j<remainingData.size(); j++) {
				//ArrayList<String> currentData = copyOfData.get(j);
				int labelValueIndex = nominals.indexOf(remainingTrainLabel.get(j));
					//remainingLabelValues.indexOf(currentData.get(targetAttrIndex));
				rightEachLabelCount[labelValueIndex]++;
			}
			
			double rightInfoGain = 0;
			int rightTotalCount = (remainingData.size()-i-1);
			for(int j=0; j<nominals.size(); j++) {
				if(rightEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = 
					((double)rightEachLabelCount[j]) / rightTotalCount;
				rightInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double infoGain = ((double)(i+1)) / remainingData.size() * leftInfoGain + 
					((double)rightTotalCount) / remainingData.size() * rightInfoGain;
			if(infoGain > maxInfoGain) {
				maxInfoGain = infoGain;
				maxInfoGainIndex = i;
			}
		}		
		return maxInfoGainIndex;
	}
	
	private ArrayList<ArrayList<String>> getNewRemainingData(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr,String attrValue) {
		ArrayList<ArrayList<String>> newRemainingData = 
				new ArrayList<ArrayList<String>>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentItem = remainingData.get(i);
			if(attrValue.equals(currentItem.get(indexOfAttr))) {
				newRemainingData.add(currentItem);
			}
		}	
		return newRemainingData;
	}
	
	private NominalAttribute getNewRemainingTrainLabel(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr,
			String attrValue, NominalAttribute remainingTrainLabel) {
		NominalAttribute newRemainingTrainLabel = 
			new NominalAttribute(remainingTrainLabel.getName(), new IncrementVector());
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentItem = remainingData.get(i);
			if(attrValue.equals(currentItem.get(indexOfAttr))) {
				newRemainingTrainLabel.push(remainingTrainLabel.get(i));
				newRemainingTrainLabel.addNominal(remainingTrainLabel.get(i));
			}
		}	
		return newRemainingTrainLabel;
	}
	
	private int getMaxGainRatioAttribute(ArrayList<ArrayList<String>> remainingData,
			ArrayList<String> remainingAttribute, NominalAttribute remainingTrainLabel) {
		double max_gain = 0;
		int max_index = 0;	//Attribute index where the attribute information gain max  
		for(int i=0; i<remainingAttribute.size(); i++) {
			//Get information gain of the attribute
			double temp_gain;
			if(isAttributeContinuous[i]) {
				int attrIndex = this.attributeList.indexOf(remainingAttribute.get(i));
				temp_gain = 
					getGainRatioForContinuousAttribute(remainingData,
						remainingTrainLabel, attrIndex);
			} else {
				temp_gain = 
					computeGainRatio(remainingData, remainingAttribute.get(i), remainingTrainLabel);
			}			
			if(max_gain < temp_gain) {
				max_gain = temp_gain;
				max_index = i;
			}
		}
		return max_index;
	}

	/**
	 * Compute information gain of a specified attribute.
	 * @param remainingData - remaining data to be classified.
	 * @param attribute - attribute to compute information gain.
	 * ***Attention: @param attrIndex - index of attribute to compute information gain.
	 * @return information gain of a specified attribute.
	 */
	private double computeGainRatio(ArrayList<ArrayList<String>> remainingData, 
			String attribute, NominalAttribute remainingTrainLabel) {		
		double inforGain = 
			computeInformationGain(remainingData, attribute, remainingTrainLabel);										
		double splitInfo = computeSplitInformation(remainingData, attribute);						
		return inforGain / splitInfo;
	}
	
	private double computeSplitInformation(
			ArrayList<ArrayList<String>> remainingData, String attribute) {
		/* Count each appearances of values of the attribute */
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount = 
			countAttributeValuesApperances(remainingData, indexOfAttr);		
				
		 /* Get remaining values of attribute in indexOfAttr
		 * Can not use
		 *   ArrayList<String> attrValues =attributeValueList.get(indexOfAttr)*/
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = remainingData.get(i);
			if(!attrValues.contains(currentSample.get(indexOfAttr))) {
				attrValues.add(currentSample.get(indexOfAttr));
			}
		}
		
		int allCount = remainingData.size();
		double splitInfo = 0;		
		for(int j=0; j<attrValues.size(); j++) {		
			double refactor = ((double)eachCount.get(j)) / allCount;
			splitInfo += -1 * refactor * Math.log(refactor) / Math.log(2.0);
		}
		return splitInfo;
	}
	
	/**
	 * Compute information gain of a specified attribute.
	 * @param remainingData - remaining data to be classified.
	 * @param attribute - attribute to compute information gain.
	 * ***Attention: @param attrIndex - index of attribute to compute information gain.
	 * @return information gain of a specified attribute.
	 */
	private double computeInformationGain(ArrayList<ArrayList<String>> remainingData, 
			String attribute, NominalAttribute remainingTrainLabel) {		
		double inforGain = 0;								
		//Add entropy(S);
		Map<Object, Integer> countMap = countEachLabelValue(remainingTrainLabel);
		List<Integer> countList = new ArrayList<Integer>(countMap.values());
		for(int i=0; i<countList.size(); i++) {
			double temp = countList.get(i) * 1.0 / remainingData.size();
			inforGain += -1 * temp * Math.log10(temp) / Math.log10(2);
		}
		
		/* Count each appearances of values of the attribute */
		int indexOfAttr = attributeList.indexOf(attribute);
		ArrayList<Integer> eachCount = 
			countAttributeValuesApperances(remainingData, indexOfAttr);		
				
		 /* Get remaining values of attribute in indexOfAttr
		 * Can not use
		 *   ArrayList<String> attrValues =attributeValueList.get(indexOfAttr)*/
		ArrayList<String> attrValues = new ArrayList<String>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> currentSample = remainingData.get(i);
			if(!attrValues.contains(currentSample.get(indexOfAttr))) {
				attrValues.add(currentSample.get(indexOfAttr));
			}
		}
		
		for(int j=0; j<attrValues.size(); j++) {
			double entropy = 
				getEntropy(remainingData, indexOfAttr, attrValues.get(j),
					remainingTrainLabel);			
			inforGain += 
				(-1.0 * eachCount.get(j)) / remainingData.size() * entropy;
		}		
		return inforGain;
	}
	
	/**
	 * Compute entropy of value of an attribute
	 * @param remainingData - remaining data to be classified.
	 * @param attrIndex - index of the attribute to compute entropy.
	 * @param attrValue - value of the attribute
	 * @return entropy of the attribute
	 */
	private double getEntropy(ArrayList<ArrayList<String>> remainingData, 
			int attrIndex, String attrValue, NominalAttribute remainingTrainLabel) {	
		Map<Object, Integer> countMap =
			countEachLabelValue(remainingData, attrIndex, 
					attrValue, remainingTrainLabel);
		List<Integer> countList = new ArrayList<Integer>(countMap.values());
		int attributeValueCount = 0;
		/*If one label value count is 0, the entropy is 0.   */
		for(int i=0; i<countList.size(); i++) {
			attributeValueCount += countList.get(i);
			if(countList.get(i) == 0) {
				return 0;
			}
		}
		
		/* Compute entropy */		
		double entropy = 0;		//Entropy of value of the attribute		
		for(int i=0; i<countList.size(); i++) {
			double temp = countList.get(i) * 1.0 / attributeValueCount;
			entropy += -1 * temp * Math.log10(temp) / Math.log10(2);
		}		
		return entropy;
	}
	
	private double getGainRatioForContinuousAttribute(
			ArrayList<ArrayList<String>> remainingData,
			NominalAttribute remainingTrainLabel, 
			final int attrIndex) {
		ArrayList<ArrayList<String>> copyOfData = 
			new ArrayList<ArrayList<String>>(remainingData);
		Collections.sort(copyOfData, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				double value1 = Double.parseDouble(o1.get(attrIndex));
				double value2 = Double.parseDouble(o2.get(attrIndex));
				if(value1 > value2) {
					return 1;
				} else if(value1 == value2) {
					return 0;
				} else {
					return -1;
				}
			}
		});
			
		int maxInfoGainIndex = 0;
		double maxInfoGain = 0;
		for(int i=0; i<remainingData.size()-1; i++) {				
			String leftLabel = remainingTrainLabel.get(i).toString(); 
			String rightLabel = remainingTrainLabel.get(i+1).toString(); 
			if(leftLabel.equals(rightLabel)) {
				continue;
			}
			
			List<Object> nominals = remainingTrainLabel.nominals();
			double leftInfoGain = 0;
			double[] leftEachLabelCount = new double[nominals.size()]; 
			Arrays.fill(leftEachLabelCount, 0);
			for(int j=0; j<=i; j++) {
				int labelValueIndex = nominals.indexOf(remainingTrainLabel.get(j)); 
				leftEachLabelCount[labelValueIndex]++;
			}
						
			for(int j=0; j<nominals.size(); j++) {
				if(leftEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = ((double)leftEachLabelCount[j]) / (i+1);
				leftInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double[] rightEachLabelCount = new double[nominals.size()];
			Arrays.fill(rightEachLabelCount, 0);
			for(int j=i+1; j<remainingData.size(); j++) {
				int labelValueIndex = nominals.indexOf(remainingTrainLabel.get(j));
				rightEachLabelCount[labelValueIndex]++;
			}
			
			double rightInfoGain = 0;
			int rightTotalCount = (remainingData.size()-i-1);
			for(int j=0; j<nominals.size(); j++) {
				if(rightEachLabelCount[j] == 0) {
					continue;
				}
				double refactor = 
					((double)rightEachLabelCount[j]) / rightTotalCount;
				rightInfoGain += -1 * refactor * Math.log(refactor) / Math.log(2);
			}
			
			double infoGain = ((double)(i+1)) / remainingData.size() * leftInfoGain + 
					((double)rightTotalCount) / remainingData.size() * rightInfoGain;
			if(infoGain > maxInfoGain) {
				maxInfoGain = infoGain;
				maxInfoGainIndex = i;
			}
		}		

		double splitInfo = 0;	
		double refactor_1 = 
			((double)(maxInfoGainIndex+1)) / remainingData.size();
		splitInfo += -1 * refactor_1 * Math.log(refactor_1) / Math.log(2.0);
		double refactor_2 = 
				((double)(remainingData.size()-maxInfoGainIndex-1)) / remainingData.size();
		splitInfo += -1 * refactor_2 * Math.log(refactor_2) / Math.log(2.0);
		
		double gainRatio = maxInfoGain / splitInfo;   //增益率
		return gainRatio;				
	}
		
	/**
	 * 检查剩下的数据集对应的类标是不是完全相同
	 * @param remainingTrainLabel 剩下的类标
	 * @return 剩下的数据集对应的类标是不是完全相同,是则返回true;否则,返回false
	 */
	private boolean allTheSameLabel(NominalAttribute remainingTrainLabel) {
		Object label = remainingTrainLabel.get(0);
		for(int i=1; i<remainingTrainLabel.size(); i++) {						
			if(!label.equals(remainingTrainLabel.get(i))) {
				return false;				
		    } 	
		}		
		return true;
	}
	
	/**
	 *  找出剩下的数据中出现次数最多的类标
	 * @param remainingTrainLabel - 剩下的数据对应的类标
	 * @return 剩下的数据中出现次数最多的类标
	 */
	private String mostCommonLabel(NominalAttribute remainingTrainLabel) {
		//统计各个类标出现的次数
		Map<Object, Integer> labelMap = countEachLabelValue(remainingTrainLabel);
		//找出出现次数最多的类标
		String comomLabel = "";
		int maxCount = 0;
		for(Map.Entry<Object, Integer> entry : labelMap.entrySet()) {
			if(entry.getValue().intValue() > maxCount) {
				maxCount = entry.getValue().intValue();
				comomLabel = entry.getKey().toString();				
			}
		}
		return comomLabel;
	}

	//统计各个类标出现的次数
	private Map<Object, Integer> countEachLabelValue(
			NominalAttribute remainingTrainLabel) {		
		Map<Object, Integer> labelMap = new LinkedHashMap<Object, Integer>();
		for(int i=0; i<remainingTrainLabel.size(); i++) {
			String label = remainingTrainLabel.get(i).toString();
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, 1);
			} else {
				labelMap.put(label, labelMap.get(label) + 1);
			}
		}
		return labelMap;
	}

	private Map<Object, Integer> countEachLabelValue(
			ArrayList<ArrayList<String>> remainingData,
			int attrIndex, String attrValue, 
			NominalAttribute remainingTrainLabel) {		
		Map<Object, Integer> labelMap = new LinkedHashMap<Object, Integer>();
		for(int i=0; i<remainingData.size(); i++) {
			String currentAttrValue = remainingData.get(i).get(attrIndex);
			if(!currentAttrValue.equals(attrValue)) {
				continue;
			}
			String label = remainingTrainLabel.get(i).toString();
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, 1);
			} else {
				labelMap.put(label, labelMap.get(label) + 1);
			}
		}
		return labelMap;
	}

	
	private ArrayList<Integer> countAttributeValuesApperances(
			ArrayList<ArrayList<String>> remainingData, int indexOfAttr) {
		Map<String, Integer> attrValueCountsMap =
				new LinkedHashMap<String, Integer>();
		for(int i=0; i<remainingData.size(); i++) {
			ArrayList<String> singleData = remainingData.get(i);
			String attrValue = singleData.get(indexOfAttr);
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
		
	/* Check whether the data inputed is valid. */
	private void isTrainDataInputedValid() {			
		if(data == null || attributeList == null) {
			throw new IllegalArgumentException("Parameter inputed can not be null.");
		}
	}
	
	/**
	 * Construct attributeValueList(not repeat) using training data
	 * @param data - training data
	 * @param attributeList - list of attributes
	 */
	private ArrayList<ArrayList<String>> constructAttributeValueList(
			ArrayList<ArrayList<String>> data, 
			ArrayList<String> attributeList) {
		ArrayList<ArrayList<String>> attributeValueList = 
			new ArrayList<ArrayList<String>>();
		for(int i=0; i<attributeList.size(); i++) {
			attributeValueList.add(new ArrayList<String>());
		}
		
		for(int i=0; i<data.size(); i++) {			
			ArrayList<String> item  = data.get(i);
			for(int j=0; j<attributeList.size(); j++) {
				int attrIndex = this.attributeList.indexOf(attributeList.get(j));
				//Attention:
				if(!attributeValueList.get(j).contains(item.get(attrIndex))) {
					attributeValueList.get(j).add(item.get(attrIndex));
				}													
			}
		}		
		return attributeValueList;
	}
	
	//将 Table类型的数据转化为 ArrayList类型的数据
	private ArrayList<ArrayList<String>> tableToArrayList(Table dataSet) {
		int rows = dataSet.rows();
		int columns = dataSet.columns();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		/* 初始化数据 */
		for(int i=0; i<rows; i++) {
			ArrayList<String> rowData = new ArrayList<String>();
			TableRow tableRow = dataSet.row(i);
			for(int j=0; j<columns; j++) {
				rowData.add(tableRow.at(j).toString());
			}
			data.add(rowData);
		}
		return data;
	}
	
	/* 决策树节点类 */
	private static class DTreeNode implements Serializable {		
		private static final long serialVersionUID = 1L;
		
		String attribute = "";	  //节点对应的属性
		String previousDecision = "";   //前一个属性决策的值
		String label = "";	//类标(对于叶子节点)		
		ArrayList<DTreeNode> nextNodes;		//子决策树的引用	
	}
}