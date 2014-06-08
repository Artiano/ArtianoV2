package artiano.ml.clustering;

import java.util.*;
import java.util.Map.Entry;
import java.text.DecimalFormat;
import artiano.core.operation.Preservable;
import artiano.core.structure.*;
import artiano.ml.BaseKDTree.BaseKDNode;
import artiano.ml.clustering.structure.KDTree;

/**
 * <p>Description: KMeans.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-15
 * @function 
 * @since 1.0.0
 */
public class KMeans extends Preservable {
	private static final long serialVersionUID = 1L;

	private Matrix data;  //待聚类的数据集		
	
	public KMeans() {		
	}
	
	/**
	 * 对数据集进行聚类
	 * @param data 待聚类的数据集
	 * @param k 将要划分的簇的数目
	 * @return clusterMap 键值对，簇的中心作为键，簇内的数据点作为值 
	 */
	public Map<Matrix, Matrix> kmeans(Matrix data, int k) {
		this.data = data;		
		dataValidationCheck(data, k);     //检查输入的参数的合法性 		
		//聚类
		return findFinalCenters(data, k);
	}

	/**
	 * 聚类
	 * @param data 待聚类的数据集
	 * @param k 将要划分的簇的数目
	 * @return clusterMap -  键值对，簇的中心作为键，簇内的数据点作为值
	 */
	private Map<Matrix, Matrix> findFinalCenters(Matrix data, int k) {
		//获取随机产生中心点的次数
		final int NUMOFGENERATINGCENTERS = 
				computeNumOfCombinations(data.rows(), k) / 7;  
		//存储NUMOFGENERATINGCENTERS次实验的所有的最终中心点
		Matrix allExerCenters = 
			new Matrix(NUMOFGENERATINGCENTERS * k, data.columns());
		//存储所有的聚类结果
		List<Map<Matrix, Matrix>> allClusterMaps =
			new ArrayList<Map<Matrix, Matrix>>();			
		//存储每次实验的聚类评估值
		Matrix allEvaluations = new Matrix(NUMOFGENERATINGCENTERS, 1);  
		
		//进行NUMOFGENERATINGCENTERS次实验，每次随机选择指定数目的中心点，再进行聚类
		for(int i=0; i<NUMOFGENERATINGCENTERS; i++) {
			//随机选择k个中心点
			Matrix initialCenters = initializeCenters(data, k);
			//k-means
			Map<Matrix, Matrix> tempClusterMap = kmeans(initialCenters);  
			//由tempClusterMap获取中心点
			Matrix finalCenters = getCentersFromClusterMap(k, tempClusterMap);  			
			allExerCenters.set(new Range(i*3, i*3+3), 
				new Range(0, data.columns()), finalCenters);
			allClusterMaps.add(tempClusterMap);
			allEvaluations.set(i, 0, evaluate(tempClusterMap));
		}
		
		//在进行的实验中,选择聚类误差最小的一次的聚类结果作为最终的聚类结果
		double minEvaluation = allEvaluations.at(0, 0);
		int minIndex = 0;
		for(int i=1; i<allEvaluations.rows(); i++) {
			if(allEvaluations.at(i, 0) < minEvaluation) {
				minEvaluation = allEvaluations.at(i, 0);
				minIndex = i;
			}
		}						
		return allClusterMaps.get(minIndex);
	}
	
	/**
	 * k-means
	 * Find output matrix of the cluster centers, one row per cluster center
	 * @param centers 簇中心
	 * @return 聚类得到的所有簇，簇中心点作为键，簇内数据点作为值 
	 * Output matrix of the cluster centers, one row per cluster center
	 */
	private Map<Matrix, Matrix> kmeans(Matrix centers) {					
		KDTree kdTree = new KDTree(centers);  //构造簇中心组成的KD-Tree
		//用LinkedHashMap来维持簇中心点之间的顺序
		Map<Matrix, Matrix> clusterMap = new LinkedHashMap<Matrix, Matrix>();
		for(int i=0; i<centers.rows(); i++) {
			boolean isPart = false;
			for(int m=0; m<data.rows(); m++) {
				if(centers.row(i).equals(data.row(m))) {
					isPart = true;
					break;
				}
			}			
			if(isPart) {
				clusterMap.put(centers.row(i), centers.row(i));
			}			
		}
		
		//获取非簇中心的数据点
		Matrix remaining = getNonCenterData(centers);
		for(int i=0; i<remaining.rows(); i++) {
			Matrix currentData = remaining.row(i);  //当前要找到最近中心点的数据点
			BaseKDNode node = kdTree.findKNearest(currentData, 1).get(0);
			Matrix center = node.nodeData;   //与remaining.row(i)最接近的中心点
			if(clusterMap.containsKey(center)) {
				clusterMap.get(center).mergeAfterRow(currentData);				
			} else {
				clusterMap.put(center, currentData);
			}						
		}
		
		//得到更新后的簇中心点
		Matrix updatedCenters = getUpdatedCenters(clusterMap, data.columns());	
		if(centers.equals(updatedCenters)) {   //簇中心点已经稳定，停止迭代
			return clusterMap;
		} else {
			return kmeans(updatedCenters);
		}
	}
	
	/**
	 * 更新数据点
	 * @param clusterMap 簇中心点以及簇内数据点   
	 * @param columns 数据点的维数
	 * @return 更新后的数据点
	 */
	private Matrix getUpdatedCenters(Map<Matrix, Matrix> clusterMap, int columns) {
		int centerNum = clusterMap.keySet().size();
		Matrix newCenters = new Matrix(centerNum, columns);
		Set<Entry<Matrix, Matrix>> entrySet = clusterMap.entrySet();
		int count = 0;
		DecimalFormat f = new DecimalFormat("#.###");   //实数格式化 
		for(Entry<Matrix, Matrix> entry: entrySet) {	
			Matrix aCluster = entry.getValue();
			Matrix newCenter = new Matrix(1, columns);  //计算平均矩阵
			for(int j=0; j<columns; j++) {
				double sum = 0;
				for(int i=0; i<aCluster.rows(); i++) {
					sum += aCluster.at(i, j); 
				}				
				double value = 
					Double.parseDouble(f.format(sum / aCluster.rows()));  //实数格式化
				newCenter.set(0, j, value);
			}									
			newCenters.setRow(count, newCenter);
			count++;
		}
		return newCenters;				
	}
	
	/**
	 * 得到数据点中去掉中心点之后剩下的数据点
	 * @param centers 选择的初始聚类中心
	 * @return remaining 非中心数据点
	 */
	private Matrix getNonCenterData(Matrix centers) {
		//统计数据集中作为簇中心的数据点的数目
		int centerInDataNum = 0; 
		for(int i=0; i<centers.rows(); i++) {
			boolean isInData = false;
			for(int m=0; m<data.rows(); m++) {
				if(centers.row(i).equals(data.row(m))) {
					isInData = true;
				}
			}			
			if(isInData) {
				centerInDataNum++;
			}
		}
		
		//获取数据集中的非中心点
		Matrix remaining = new Matrix(data.rows()-centerInDataNum, data.columns());
		int count = 0;
		for(int i=0; i<data.rows(); i++) {
			boolean isSplit = false;    //当前数据点是不是中心点
			for(int m=0; m<centers.rows(); m++) {
				if(data.row(i).equals(centers.row(m))) {
					isSplit = true;
					break;
				}
			}			
			if(!isSplit) {
				remaining.setRow(count, data.row(i));
				count++;
			}					
		}
		return remaining;
	}

	/**
	 * 获取完整的一次聚类的质量评估值  
	 * @param clusterMap - cluster map 
	 * @return - 聚类质量评估值
	 */
	private double evaluate(Map<Matrix, Matrix> clusterMap) {
		Set<Entry<Matrix, Matrix>> entrySet = clusterMap.entrySet();
		double evaluation = 0;
		for(Entry<Matrix, Matrix> entry: entrySet) {	
			Matrix center = entry.getKey();  //簇中心
			Matrix membersOfCluster = entry.getValue();
			evaluation += evaluate(center, membersOfCluster);
		}
		return evaluation;
	} 
	
	/**
	 * 获取一个簇的聚类质量评估值
	 * @param center 簇中心
	 * @param members - 簇内的数据点(包括簇中心)
	 * @return 聚类质量评估值
	 */
	private double evaluate(Matrix center, Matrix members) {
		double evaluation = 0;
		for(int i=0; i<members.rows(); i++) {
			evaluation += distance(center, members.row(i));
		}
		return evaluation;
	}
	
	/**
	 * 获取两个向量之间的欧氏距离
	 * @param mat1 向量1
	 * @param mat2 向量2
	 * @return 两个向量之间的欧氏距离
	 */
	private double distance(Matrix mat1, Matrix mat2) {
		if(mat1.columns() != mat2.columns() || mat1.rows() != 1 || mat2.rows() != 1) {
			throw new IllegalArgumentException("两个向量应该具有相同的大小!");
		}		
		double distance = 0;
		for(int j=0; j<mat1.columns(); j++) {
			distance += Math.pow(mat1.at(0, j) - mat2.at(0, j), 2);
		}
		return distance;
	}
	
	/**
	 * 从数据集中随机选择k个中心点，作为初始中心点
	 * @param data - 待聚类的数据
	 * @param k - 要得到的簇的数目
	 * @return 随机选择的k个中心点
	 */
	private Matrix initializeCenters(Matrix data, int k) {
		Matrix centers = new Matrix(k, data.columns());
		int sampleCount = data.rows();
		Random random = new Random(System.currentTimeMillis());
		int count = 0;
		while(count < k) {			
			int randomRowIndex = random.nextInt(sampleCount);
			Matrix currentData = data.row(randomRowIndex);			
			/* 检查当前数据点是不是已经被选为了中心点  */
			boolean alreadyExist = false;
			for(int i=0; i<count; i++) {
				if(currentData.equals(centers.row(i))) {
					alreadyExist = true;
					break;
				}
			}
			if(!alreadyExist) {
				centers.setRow(count, currentData);
				count++;
			}			
		}
		return centers;
	}

	/**
	 * 由聚类结果得到簇中心点
	 * @param k 聚类要得到簇的数
	 * @param clusters 
	 * @return - centers
	 */
	private Matrix getCentersFromClusterMap(int k, Map<Matrix, Matrix> clusters) {
		Matrix finalCenters = new Matrix(k, data.columns());
		Set<Entry<Matrix, Matrix>> entrySet = clusters.entrySet();
		int count = 0;
		for(Entry<Matrix, Matrix> entry: entrySet) {
			finalCenters.setRow(count, entry.getKey());
			count++;
		}
		return finalCenters;
	}
	
	/**
	 * 计算从n个中选择r个的排列的个数
	 * @param n 所有的元素的数目
	 * @param r 从所有的元素中选择r个
	 * @return 从n个中选择r个的排列的个数
	 */
	private int computeNumOfCombinations(int n, int r) {
		int result = 1;
		for(int i=0; i<r; i++) {
			result *= (n - i);
		}		
		for(int i=r; i>0; i--) {
			result /= i;
		}
		return result;
	}
	
	/**
	 * 检查输入的待聚类数据的合法性
	 * @param data - 待聚类数据集
	 * @param k - 要得到的簇的数目
	 * @throws IllegalArgumentException
	 */
	private void dataValidationCheck(Matrix data, int k) {
		if(data == null) {
			throw new IllegalArgumentException("待聚类的数据集不能为空!");
		}
		
		if(k <= 1) {
			throw new IllegalArgumentException("聚类得到的簇的数目k不能少于2!");
		} else if(k > data.columns()) {
			throw new IndexOutOfBoundsException("k应该不大于数据点的数目!");
		}
	}
}