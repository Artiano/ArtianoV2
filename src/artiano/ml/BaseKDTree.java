package artiano.ml;

import java.util.LinkedList;
import java.util.Queue;

import artiano.core.structure.Matrix;

public abstract class BaseKDTree {
	
	protected BaseKDTree.BaseKDNode root;  //KD-Tree的根节点		

	/**
	 * 计算方差
	 * @param data - 数据矩阵
	 * @return 矩阵的方差
	 */
	protected double computeVariance(Matrix data) {
		double aver = computeAverage(data);  //Get average of numbers
		double variance = 0;
		for(int i=0; i<data.rows(); i++) {
			variance += Math.pow(data.at(i) - aver, 2);
		}
		return variance / data.rows();
	}

	/**
	 * 计算向量矩阵的平均值
	 * @param data - 矩阵
	 * @return 平均值
	 */
	private double computeAverage(Matrix data) {
		double sum = 0;
		for(int i=0; i<data.rows(); i++) {
			sum += data.at(i);
		}
		return sum / data.rows();
	}

	//广度优先遍历KD-Tree 
	public void bfs() {
		Queue<BaseKDNode> queue = new LinkedList<BaseKDNode>();
		queue.add(root);
		while(! queue.isEmpty()) {
			BaseKDNode node = queue.poll();	
			System.out.print("(");
			for(int i=0; i<node.nodeData.columns(); i++) {
				if(i < node.nodeData.columns() - 1 ) {
					System.out.print(node.nodeData.at(i) + ", ");
				} else {
					System.out.print(node.nodeData.at(i));
				}				
			}
			System.out.println(")");
			
			if(node.left != null) {
				queue.add(node.left);
			}
			
			if(node.right != null) {
				queue.add(node.right);
			}			
		}
	}

	/**
	 * Get index of feature which has max variance.
	 * 获取方差最大的属性在数据中的维下标
	 * @param dataSet - 数据集 
	 * @return - 属性的维下标
	 */
	protected int getPartitionFeatureIndex(Matrix dataSet) {
		double[] variances = new double[dataSet.columns()];  
		for(int j=0; j<dataSet.columns(); j++) {
			Matrix singlFeature = dataSet.column(j); 
			variances[j] = computeVariance(singlFeature);
		}
		
		/* 获取拥有最大方差的属性的下标 */
		double maxVariance = variances[0];
		int maxIndex = 0;
		for(int i=1; i<variances.length; i++) {
			if(variances[i] > maxVariance) {
				maxVariance = variances[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	/**
	 * 计算两个数据点之间的欧式距离
	 * @param point1 - 数据点
	 * @param point2 - 数据点
	 * @return 两个数据点之间的欧式距离
	 */
	protected double distance(Matrix point1, Matrix point2) {
		if(point1.columns() != point2.columns() || 
				point1.rows() != 1 || point2.rows() != 1) {
			throw new IllegalArgumentException(
				"两个数据点列的维数应该一致且行数为1!");
		}
		
		double distance = 0;
		for(int i=0; i<point1.columns(); i++) {
			distance += Math.pow(point1.at(i) - point2.at(i), 2);
		}
		return Math.sqrt(distance);
	}

	/**  
	 * 删除指定节点
	 * @param node 将要删除的节点 
	 * @return 节点删除是否成功 
	 */
	protected abstract boolean delete(BaseKDNode nodeToDelete);	
			
	//KDTree的节点	
	public static class BaseKDNode {		
		public int featureIndex;	//分类属性的下标
		public double partitionValue;		//分类属性的值
		public Matrix treeData;    //子树的数据		
		public Matrix nodeData;	//节点的数据
		public BaseKDNode left;	//左孩子
		public BaseKDNode right;   //右孩子
				
		public BaseKDNode(Matrix data) {
			this.treeData = data;
		}
		
		public BaseKDNode(int featureIndex, double value, Matrix nodeData) {
			this.nodeData = nodeData;
			this.featureIndex = featureIndex;
			this.partitionValue = value;
		}
	}
}
