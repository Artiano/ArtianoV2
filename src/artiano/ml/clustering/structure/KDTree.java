package artiano.ml.clustering.structure;

import java.util.*;

import artiano.core.structure.Matrix;
import artiano.core.structure.Range;
import artiano.ml.BaseKDTree;

/**
 * <p>Description: KD Tree for KMeans.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-15
 * @function 
 * @since 1.0.0
 */
public class KDTree extends BaseKDTree {	
	
	/**
	 * Constructor
	 * @param dataSet - 数据集
	 */
	public KDTree(Matrix dataSet) {
		root = buildKDTree(dataSet);
	}

	/**
	 *  构造KD-Tree
	 * @param dataSet - 数据集 
	 * @return 决策树的根节点
	 */
	private BaseKDNode buildKDTree(Matrix dataSet) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}						
		BaseKDTree.BaseKDNode root = 
			new BaseKDTree.BaseKDNode(dataSet);   //初始化根节点		
		expandSubKDTree(root);   //构造子树		
		return root;
	}
	
	/**  
	 * 删除指定的节点
	 * @param node - 将要删除的节点
	 * @return 删除成功与否。成功，则返回true;否则，返回false 
	 */
	@Override
	protected boolean delete(BaseKDTree.BaseKDNode nodeToDelete) {
		//将要删除的节点是树中唯一的一个节点
		if(root.treeData.rows() == 1) {	  
			root = null;
			return true;
		}
		
		Matrix newTreeData = 
			new Matrix(root.treeData.rows() - 1, root.treeData.columns());	
		//广度优先遍历来获取newTreeData和newTreeLabel
		int count = 0;
		Queue<BaseKDTree.BaseKDNode> nodeQueue = 
			new LinkedList<BaseKDTree.BaseKDNode>();
		nodeQueue.add(root);
		while(!nodeQueue.isEmpty()) {
			BaseKDTree.BaseKDNode node = nodeQueue.poll();
			if(node != nodeToDelete) {  //不是要删除的节点
				newTreeData.setRow(count, node.nodeData);				
				count++;
			}									
			if(node.left != null) {
				nodeQueue.add(node.left);
			}			
			if(node.right != null) {
				nodeQueue.add(node.right);
			}
		}
		
		if(count == root.treeData.rows()) {  //没有找到要删除的节点
			return false;
		} else {
			root = buildKDTree(newTreeData);			
			return true;
		}				
	}		
	
	/**
	 * 找到与指定数据最接近的节点
	 * @param target - 将要寻找最近邻居的手
	 * @return 最近邻居
	 */
	public BaseKDTree.BaseKDNode findNearest(Matrix target) {		
		/* 1. 二分查找来获取查找路径 */
		BaseKDTree.BaseKDNode current = root;		
		int featureIndex = current.featureIndex;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Stack<BaseKDTree.BaseKDNode> searchPath = new Stack();		
		while(current != null) {										
			searchPath.push(current);	//将找到的节点放入栈中			
							
			//二分查找
			featureIndex = current.featureIndex;  //获取下一次分裂的维下标
			if(target.at(featureIndex) <= current.partitionValue) {
				current = current.left;
			} else {
				current = current.right;
			}
		}
		BaseKDTree.BaseKDNode nearestNode = searchPath.peek();	//节点数据
		double max_dist = distance(nearestNode.nodeData, target);
		double min_dist = max_dist;		
		
		/* 2. trace search */
		BaseKDTree.BaseKDNode kd_point = null;		
		while(! searchPath.empty()) {
			BaseKDTree.BaseKDNode back_point = searchPath.pop();			
			double distance2 = distance(back_point.nodeData, target);
			if(min_dist > distance2) {
				min_dist = distance2;
				nearestNode = back_point;				
			}
			
			featureIndex = back_point.featureIndex;  //作为数据分类的属性
			
			double dist1 = 
				distance(target.column(featureIndex), 
					back_point.nodeData.column(featureIndex));
			if(dist1 < max_dist) {  //Get next sub space
				if(target.at(featureIndex) <= back_point.partitionValue) {
					kd_point = back_point.right;
				} else {
					kd_point = back_point.left;
				}
				
				if(kd_point != null) {
					searchPath.push(kd_point);
				}				
			}
			
			if(kd_point == null) {  //Trace complete
				continue;
			}
			
			double dist3 = distance(kd_point.nodeData, target);
			if(dist3 < min_dist) {
				nearestNode = kd_point;
				min_dist = dist3;
			}
		}
		return nearestNode;
	}
	
	/**
	 * 构造子树
	 * @param kdNode - 子树的根节点
	 */
	private void expandSubKDTree(BaseKDTree.BaseKDNode kdNode) {
		if(kdNode == null) {	//叶子节点
			return;
		}
		partition_features(kdNode);  //构造左子树和右子树		
		//构造左子树
		if(kdNode.left != null) {
			expandSubKDTree(kdNode.left);
		}		
		//构造右子树
		if(kdNode.right != null) {
			expandSubKDTree(kdNode.right);
		}
	}
	
	//将当前数据划分为左右两颗子树
	private void partition_features(BaseKDTree.BaseKDNode kdNode) {
		if(kdNode.treeData == null) {   //Leaf node
			return;
		}
			
		int partitionFeatureIndex = 
			   getPartitionFeatureIndex(kdNode.treeData); //Get partition feature index
		kdNode.featureIndex = partitionFeatureIndex;
		
		//Sort the feature matrix by feature with specified index 
		sortFeatureByIndex(kdNode, partitionFeatureIndex);    
		
		/* Assign data to left child and right child of kdNode. */
		Matrix data = kdNode.treeData;
		kdNode.nodeData = 
				data.row(data.rows() / 2);		
		kdNode.partitionValue = 
				data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.
				
		/* 给左子树赋值 */
		int leftDataRowNum = data.rows() / 2; 		
		Matrix leftData = null;
		if(leftDataRowNum > 0) {
			leftData = new Matrix(leftDataRowNum, data.columns());
			for(int i=0; i< leftDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					leftData.set(i, j, data.at(i, j));
				}
			}
		}				
		/* 给右子树赋值 */
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = null;
		if(rightDataRowNum > 0) {
			rightData = new Matrix(rightDataRowNum, data.columns());
			for(int i=0; i<rightDataRowNum; i++) {
				for(int j=0; j<data.columns(); j++) {
					rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
				}
			}
		}		
		
		//kdNode指向子树的引用 
		BaseKDTree.BaseKDNode leftChild =  new BaseKDTree.BaseKDNode(leftData);
		BaseKDTree.BaseKDNode rightChild = new BaseKDTree.BaseKDNode(rightData);
		if(leftChild.treeData != null) {
			kdNode.left = leftChild;
		}		
		if(rightChild.treeData != null) {
			kdNode.right = rightChild;
		}		
	}	
	
	/**
	 * 根据分裂属性将数据集进行排列 
	 * @param dataSet - 数据集
	 * @param featureIndex - 分裂属性在属性列表中的下标 
	 */
	private void sortFeatureByIndex(BaseKDTree.BaseKDNode node, final int featureIndex) {
		Matrix dataSet = node.treeData;				
		List<Matrix> dataList = new ArrayList<Matrix>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
		}
				
		//对treeData按照特征属性值由小到大进行排序
		boolean needNextPass = true;		
		for(int k=1; k<dataList.size() && needNextPass; k++){
			//Array may be sorted and next pass not needed
			needNextPass = false;			
			for(int i=0; i<dataList.size()-k; i++){
				Matrix mat1 = dataList.get(i);				
				Matrix mat2 = dataList.get(i + 1);				
				if(mat1.at(featureIndex) > mat2.at(featureIndex)){
					//Swap mat1 with mat2											
					dataList.set(i, mat2);
					dataList.set(i + 1, mat1);									
				}				
			}
		}		
		
		//获取排好序的tree data矩阵
		double[] data = new double[dataSet.rows() * dataSet.columns()];		
		for(int i=0; i<dataList.size(); i++) {
			Matrix current = dataList.get(i);
			for(int j=0; j<current.columns(); j++) {
				data[i * dataSet.columns() + j] = current.at(j);
			}
		}
		Matrix sortedTreeData = 
				new Matrix(dataSet.rows(), dataSet.columns(), data);
		node.treeData = sortedTreeData;						
	}

	/**
	 * 找到目标数据点的k近邻
	 * @param target - 目标数据点
	 * @return - k-nearest data point of target data point
	 */
	public List<BaseKDNode> findKNearest(Matrix target, int k) {				
		if(root == null) {
			return null;
		}					
		Matrix copyData = root.treeData.clone();
		KDTree tree = new KDTree(copyData);
		//依次寻找1,2,...., k近邻
		List<BaseKDNode> kNearest = new ArrayList<BaseKDNode>();
		for(int i=0; i<k; i++) {
			BaseKDNode nearestNode = tree.findNearest(target);		
			kNearest.add(nearestNode);
			tree.delete(nearestNode);
		}						
		return kNearest;
	}
}
