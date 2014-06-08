/**
 * 	NormalBayesClassifier.java
 */
package artiano.ml.classifier;

import java.util.*;
import java.util.Map.Entry;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;

/**
 * <p>
 * 朴素贝叶斯分类器
 * </p>
 * 
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function
 * @since 1.0.0
 */
public class NaiveBayesClassifier extends Classifier {
	private static final long serialVersionUID = -1923319469209965644L;
	
	private Matrix trainData;				//训练集
	private Attribute classAttribute;	//训练集类标号
	private Table trainResult;				//训练结果
	private Map<Object, Integer> eachlabelCount = 
		new LinkedHashMap<Object, Integer>();	 //对每个类标号计数
	
	/** 训练数据
	 * @param trainSet 训练集
	 * @return 训练是否成功，true表示训练成功，false表示训练失败
	 */
	public boolean train(Table trainSet) {		
		try {
			// 检查输入的训练数据的有效性
			isTrainingDataValid(trainSet);
		} catch (Exception e) {
			return false; 	// 训练数据不合法，训练失败，退出 
		}
		//this.trainData = trainSet.toMatrix();
		//获取训练集去掉类标那一列之后的数据
		this.trainData = generateTrainDataWithoutLabel(trainSet);
		classAttribute = trainSet.classAttribute();

		// 将训练集按照类标号聚集
		Map<Object, Matrix> labelMap = groupTraningDataByLabel();	
		generateTrainingResult(labelMap);  //训练数据，产生训练结果
		return true;
	}		

	//获取训练集去掉类标那一列之后的数据
	private Matrix generateTrainDataWithoutLabel(Table trainSet) {
		int rows = trainSet.rows();
		int columns = trainSet.columns();
		int classAttrIndex = trainSet.classIndex();		//类标属性的下标
		Matrix data = new Matrix(rows, columns-1);
		for(int i=0; i<rows; i++) {
			TableRow singleData = trainSet.row(i);
			for(int j=0; j<columns; j++) {
				if(j < classAttrIndex) {
					data.set(i, j, (Double)singleData.at(j));
				} else if(j > classAttrIndex) {
					data.set(i, j-1, (Double)singleData.at(j));
				}
			}
		}
		return data;
	}
	
	/**
	 * 对待分类的数据集进行分类
	 * 
	 * @param samples - 待分类数据集
	 * @return 分类的结果
	 */
	public NominalAttribute predict(Table samples) {		
		Matrix samplesMat = samples.toMatrix();
		NominalAttribute result = new NominalAttribute("label");
		for (int i = 0; i < samples.rows(); i++) {			
			Object predictResult = classifySingleData(samplesMat.row(i));
			result.push(predictResult);
		}
		return result;
	}

	/**
	 * 对待分类的数据所属的分类进行预测
	 * 
	 * @param sample - 待预测数据
	 * @return 带预测数据的分类
	 * @throws Exception
	 */
	private Object classifySingleData(Matrix sample) {
		if (sample.rows() > 1) {
			throw new IllegalArgumentException(
				"The test sample matrix can only be one row. For"
				+ " multiple rows, please use method predict(Matrix sample, Matrix result)");
		}

		/* 通过数据属于某个类标号的可能性大小来将该数据归到该类 */
		List<Object> labelList = 
			new ArrayList<Object>(eachlabelCount.keySet());						
		List<Double> probabilityList = 
			computeEachLabelProbabilityOfData(sample, labelList);
		double maxPorba = probabilityList.get(0);
		int maxIndex = 0;
		for (int m = 1; m < probabilityList.size(); m++) {
			if (probabilityList.get(m) > maxPorba) {
				maxPorba = probabilityList.get(m);
				maxIndex = m;
			}
		}
		return labelList.get(maxIndex);    // 返回预测的类标号
	}

	//计算一条数据分别属于某个类表的可能性
	private List<Double> computeEachLabelProbabilityOfData(Matrix sample,
			List<Object> labelList) {
		List<Double> probabilityList = new ArrayList<Double>();		
		System.out.println("label size: " + labelList.size() + ", columns: " + sample.columns());
		for (int j = 0; j < labelList.size(); j++) {
			double probabilitiy = 1;
			for (int k = 0; k < sample.columns(); k++) {				
				double aver = 
					(double) trainResult.at(j * sample.columns() + k , 0); 
				double stdDeviation = 
					(double) trainResult.at(j * sample.columns() + k, 1);
				double a = (1.0 / (Math.sqrt(Math.PI * 2) * stdDeviation));
				double b = Math.pow((sample.at(0, k) - aver), 2);
				double c = 2 * Math.pow(stdDeviation, 2);
				probabilitiy *= (a * Math.pow(Math.E, -1 * b / c)); 
			}
			// 计算数据属于该类的可能性大小
			double labelAppearProba = 
				eachlabelCount.get(labelList.get(j)) * 1.0 / trainData.rows(); 
			probabilitiy *= labelAppearProba;			
			probabilityList.add(probabilitiy);  // 该条数据属于第j类的可能性大小 
		}
		return probabilityList;
	}
	
	/**
	 * 计算向量的标准差
	 * 
	 * @param numbers - 将要计算标准差的数据
	 * @return 向量的标准差
	 */
	private double computeStandardDeviation(Matrix numbers) {
		double average = computeAverage(numbers);  // 获取向量平均值
		double variance = 0; 		// 方差
		for (int j = 0; j < numbers.columns(); j++) {
			variance += Math.pow(numbers.at(0, j) - average, 2);
		}
		return Math.sqrt(variance / numbers.columns());
	}

	/**
	 * 计算向量的平均值
	 * 
	 * @param numbers - 要计算平均值的向量
	 * @return 向量的平均值
	 */
	private double computeAverage(Matrix numbers) {
		double sum = 0;
		for (int j = 0; j < numbers.columns(); j++) {
			sum += numbers.at(0, j);
		}
		return sum / numbers.columns();
	}

	/**
	 * 训练训练集，并保存训练结果,用于预测阶段
	 * 
	 * @param labelMap - 类标号以及对应的训练集组成的键值对
	 */
	private void generateTrainingResult(Map<Object, Matrix> labelMap) {
		trainResult = new Table();
		trainResult.addAttribute(new NumericAttribute("average"));   //平均值
		trainResult.addAttribute(new NumericAttribute("stdDeviation")); //标准差
		trainResult.addAttribute(new NominalAttribute("label"));		//类标号
		
		Set<Entry<Object, Matrix>> entrySet = labelMap.entrySet();		
		for(Entry<Object, Matrix> entry : entrySet) {		
			Matrix dataWithSameLabel = entry.getValue();
			// 统计各个类标号的出现次数
			eachlabelCount.put(entry.getKey(), dataWithSameLabel.rows());
			
			Matrix reverse = dataWithSameLabel.t();  // 获取矩阵的转置
			//计算训练集数据的平均值，标准差,以及对应类标号，用于后面数据类标号预测
			for (int i = 0; i < reverse.rows(); i++) {				
				TableRow tableRow = trainResult.new TableRow(); 
				double aver = computeAverage(reverse.at(new Range(i, i + 1),
						new Range(0, reverse.columns())));
				double stdDeviation = computeStandardDeviation(reverse.at(
						new Range(i, i + 1), new Range(0, reverse.columns())));
				tableRow.set(0, aver);
				tableRow.set(1, stdDeviation);
				tableRow.set(2, entry.getKey());
				trainResult.push(tableRow);
			}
		}
	}

	/**
	 * 将训练集按照类标号进行聚集
	 * 
	 * @return 键值对, 类标号作为键, 类标号对应的数据作为值.
	 */
	private Map<Object, Matrix> groupTraningDataByLabel() {
		Map<Object, Matrix> labelMap = new HashMap<Object, Matrix>();
		for (int i = 0; i < trainData.rows(); i++) {
			Object label = classAttribute.get(i);  // 获取该条数据的类标号
			if(!labelMap.containsKey(label)) {
				labelMap.put(label, trainData.row(i));
			} else {
				Matrix oldMatrix = labelMap.get(label);
				oldMatrix.mergeAfterRow(trainData.row(i));
				labelMap.put(label, oldMatrix);
			}		
		}
		return labelMap;
	}

	/**
	 * 检查输入的训练集的合法性
	 * 
	 * @throws IllegalArgumentException
	 *      trainData is null, or class Attribute is not appointed 
	 *      in trainData
	 */
	private void isTrainingDataValid(Table trainData) {
		// 检查训练集和对应的类标号是否为空
		if (trainData == null) {
			throw new IllegalArgumentException("训练集为空!");
		}
		if(trainData.classAttribute() == null) {
			throw new IllegalArgumentException("训练集对应的类标号为空!");			
		}

		/* 检查训练集是否为数值型 */
		int rows = trainData.rows();
		int columns = trainData.columns();
		for(int i=0; i<rows; i++) {
			TableRow tableRow = trainData.row(i);
			for(int j=0; j<columns; j++) {
				Object obj = tableRow.at(j);
				if(! (obj instanceof Double)) {
					throw new IllegalArgumentException("训练集只能为数值型!");
				}
			}
		}
	}

}