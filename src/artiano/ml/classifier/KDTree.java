package artiano.ml.classifier;

import java.util.*;

import artiano.core.structure.*;
import artiano.core.structure.Table.TableRow;
import artiano.ml.BaseKDTree;

/**
 * <p>Description: KD Tree for KNN.</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-9-6
 * @function 
 * @since 1.0.0
 */
public class KDTree extends BaseKDTree {
	
	public KDTree() {		
	}
	
	public KDTree(Table trainSet) {
		//获取训练集去掉类标那一列之后剩下的数据
		Matrix dataSetMat = 
			generateTrainDataWithoutLabel(trainSet); 
		Attribute classAttribute = trainSet.classAttribute(); //类标属性
		root = buildKDTree(dataSetMat, classAttribute);
	}
	
	//获取训练集去掉类标那一列之后剩下的数据
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
	 *  构造KD-Tree
	 * @param dataSet - 数据集 
	 * @return 构造的决策树的根节点
	 */
	public KDNode buildKDTree(Matrix dataSet, Attribute dataLabel) {
		if(dataSet.rows() < 1) {
			throw new IllegalArgumentException("Empty data set!");
		}						
		KDNode root = new KDNode(dataSet, dataLabel); //初始化根节点		
		expandSubKDTree(root);   //构造子树		
		return root;
	}
	
	/**  
	 * 删除指定的节点
	 * @param node - 将要删除的节点
	 * @return 删除成功与否。成功，则返回true;否则，返回false 
	 */
	@Override
	public boolean delete(BaseKDNode nodeToDelete) {
		//将要删除的节点是树中唯一的一个节点
		if(root.treeData.rows() == 1) {	  
			root = null;
			return true;
		}
		
		Matrix newTreeData = 
				new Matrix(root.treeData.rows() - 1, root.treeData.columns());
		NominalAttribute newTreeLabel = new NominalAttribute("label");
		
		//广度优先遍历来获取newTreeData和newTreeLabel
		int count = 0;
		Queue<KDNode> nodeQueue = new LinkedList<KDNode>();
		nodeQueue.add(((KDNode)root));
		while(!nodeQueue.isEmpty()) {
			KDNode node = nodeQueue.poll();
			if(node != nodeToDelete) {    //不是要删除的节点
				newTreeData.setRow(count, node.nodeData);				
				newTreeLabel.push(node.nodeLabel);
				count++;
			}						
			
			if(node.left != null) {
				nodeQueue.add((KDNode) node.left);
			}			
			if(node.right != null) {
				nodeQueue.add((KDNode) node.right);
			}
		}
		
		if(count == root.treeData.rows()) {  //没有找到要删除的节点
			return false;
		} else {
			root = buildKDTree(newTreeData, newTreeLabel);			
			return true;
		}				
	}		
	
	/**
	 * 找到与指定数据最接近的节点
	 * @param root 根节点
	 * @param target 将要寻找最近邻居的手
	 * @return 最近邻居
	 */
	public KDNode findNearest(KDNode root, Matrix target) {
		/* 1. 二分查找来获取查找路径 */
		KDNode current = root;		
		int featureIndex = current.featureIndex;		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Stack<KDNode> searchPath = new Stack();		
		while(current != null) {										
			searchPath.push(current);	//将找到的节点放入栈中					
			featureIndex = current.featureIndex;  //获取下一次分裂的维下标			
			//二分查找
			if(target.at(featureIndex) <= current.partitionValue) {
				current = (KDNode) current.left;
			} else {
				current = (KDNode) current.right;
			}
		}
		KDNode nearestNode = searchPath.peek();	//节点数据
		double max_dist = distance(nearestNode.nodeData, target);
		double min_dist = max_dist;		
		
		/* 2. trace search */
		KDNode kd_point = null;		
		while(! searchPath.empty()) {
			KDNode back_point = searchPath.pop();			
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
					kd_point = (KDNode) back_point.right;
				} else {
					kd_point = (KDNode) back_point.left;
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
	private void expandSubKDTree(KDNode kdNode) {		
		if(kdNode == null) {	//叶子节点
			return;
		}		
		partition_features(kdNode);  //构造左子树和右子树		
		//构造左子树
		if(kdNode.left != null) {
			expandSubKDTree((KDNode) kdNode.left);
		}		
		//构造右子树
		if(kdNode.right != null) {
			expandSubKDTree((KDNode) kdNode.right);
		}
	}
	
	//将当前数据划分为左右两颗子树
	private void partition_features(KDNode kdNode) {
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
		Attribute labels = kdNode.treeLabel;
		kdNode.nodeData = data.row(data.rows() / 2);		
		kdNode.nodeLabel = labels.get(data.rows()/2);
		kdNode.partitionValue = 
			data.at(data.rows()/2, partitionFeatureIndex);  //Partition key value.
				
		/* 给左子树赋值 */
		int leftDataRowNum = data.rows() / 2; 		
		Matrix leftData = null;
		NominalAttribute leftLabel = null;
		if(leftDataRowNum > 0) {
			leftData = new Matrix(leftDataRowNum, data.columns());
			leftLabel = new NominalAttribute("label");
			for(int i=0; i< leftDataRowNum; i++) { 
				leftLabel.push(labels.get(i));
				for(int j=0; j<data.columns(); j++) {
					leftData.set(i, j, data.at(i, j));					
				}
			}
		}		
		
		/* 给右子树赋值  */
		int rightDataRowNum = data.rows() - leftDataRowNum - 1;
		Matrix rightData = null;
		NominalAttribute rightLabel = null;
		if(rightDataRowNum > 0) {
			rightData = new Matrix(rightDataRowNum, data.columns());
			rightLabel = new NominalAttribute("label");
			for(int i=0; i<rightDataRowNum; i++) { 
				rightLabel.push(labels.get(leftDataRowNum+i+1));
				for(int j=0; j<data.columns(); j++) {
					rightData.set(i, j, data.at(leftDataRowNum+i+1, j));
				}
			}
		}		
		
		//kdNode指向子树的引用 
		KDNode leftChild =  new KDNode(leftData, leftLabel);
		KDNode rightChild = new KDNode(rightData, rightLabel);
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
	private void sortFeatureByIndex(KDNode node, final int featureIndex) {
		Matrix dataSet = node.treeData;
		Attribute dataLabel = node.treeLabel;		
		
		List<Matrix> dataList = new ArrayList<Matrix>();
		List<Object> labelList =  new ArrayList<Object>();
		for(int i=0; i<dataSet.rows(); i++) {
			dataList.add(dataSet.at(new Range(i,i+1), new Range(0, dataSet.columns())));
			labelList.add(dataLabel.get(i));
		}
				
		//对treeData按照特征属性值由小到大进行排序
		boolean needNextPass = true;		
		for(int k=1; k<dataList.size() && needNextPass; k++){			
			needNextPass = false;	//Array may be sorted and next pass not needed		
			for(int i=0; i<dataList.size()-k; i++){
				Matrix mat1 = dataList.get(i);				
				Matrix mat2 = dataList.get(i + 1);				
				if(mat1.at(featureIndex) > mat2.at(featureIndex)){
					//Swap mat1 with mat2											
					dataList.set(i, mat2);
					dataList.set(i + 1, mat1);
					
					//Swap labelMat1 with labelMat2
					Object labelMat1 = labelList.get(i);
					Object labelMat2 = labelList.get(i + 1);
					labelList.set(i, labelMat2);
					labelList.set(i + 1, labelMat1);
					needNextPass = true;  //还没排好序
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
		
		//获取排好序的类标矩阵
		NominalAttribute label = new NominalAttribute();
		for(int i=0; i<dataLabel.size(); i++) {
			label.push(labelList.get(i));			
		}
		node.treeLabel = label; 				
	}
	
	/**
	 * 找到目标数据点的k近邻
	 * @param target - 目标数据点
	 * @return - k-nearest data point of target data point
	 */
	public List<KDNode> findKNearest(Matrix target, int k) {
		List<KDNode> kNearest = new ArrayList<KDNode>();		
		if(root == null) {
			return null;
		}			
										
		KDTree tree = new KDTree();
		Matrix copyData = root.treeData.clone();
		Attribute copyLabel = (((KDNode)root).treeLabel) ;
		tree.root = tree.buildKDTree(copyData, copyLabel);
						
		//依次寻找1,2,...., k近邻
		for(int i=0; i<k; i++) {
			KDNode nearestNode = tree.findNearest(((KDNode)tree.root), target);		
			nearestNode.nodeData.print();
			kNearest.add(nearestNode);
			tree.delete(nearestNode);
		}						
		return kNearest;
	}	
	
	//KD-Tree节点	
	public static class KDNode extends BaseKDNode {		
		public Attribute treeLabel;	//子树的类标
		Object nodeLabel;		//该节点的类标
		
		KDNode(Matrix data, Attribute treeLabel) {
			super(data);
			this.treeLabel = treeLabel;
		}
		
		KDNode(int featureIndex, double value, Matrix nodeData) {
			super(featureIndex, value, nodeData);
		}
	}
}
