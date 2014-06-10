package artiano.ml.classifier.test;

import java.util.*;

import artiano.core.structure.*;
import artiano.ml.classifier.*;

/**
 * <p>Description: Classifiers Test</p>
 * @author JohnF Nash
 * @version 1.0.0
 * @date 2013-8-27
 * @function 
 * @since 1.0.0
 */
public class KDTreeTest {	
	static double[] trainDataArr_2 = {			
		1, 2, 10,
		3, 2, 5,
		2, 8, 4,
		1, 5, 8,
		2, 7, 5,
		2, 6, 4, 
		3, 1, 2,
		1, 4, 9
	};  //第一列为类标号	
	
	public static void testKDTree() {				
		int dimension = 3;
		Matrix trainData = 
				new Matrix(trainDataArr_2.length / dimension, dimension, trainDataArr_2);
		Table trainSet = new Table(trainData);
		trainSet.setClassAttribute(0);    //设置类标号属性的下标
		KDTree tree = new KDTree(trainSet);
		System.out.println("bfs: ");
		tree.bfs();
						
		/*------------------  Find nearest of a specified data point -----*/
		double[] point = {-1, 0};
		Matrix target = new Matrix(1, dimension-1, point);
		List<KDTree.KDNode> kNearest = tree.findKNearest(target, 3);  //Find 3-nearest point of point target
		System.out.println("\n3 Nearest data point is:");
		for(int i=0; i<kNearest.size(); i++) {
			Matrix iNearest = kNearest.get(i).nodeData;
			System.out.print("(");
			for(int j=0; j<iNearest.columns(); j++) {
				if(j < iNearest.columns() - 1) {
					System.out.print(iNearest.at(j) + ", ");
				} else {
					System.out.print(iNearest.at(j));
				}		
			}
			System.out.println(")");
		}			
	}
			 
	public static void main(String[] args) {											
		testKDTree();
	}
}
