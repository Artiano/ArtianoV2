package artiano.ml.clustering;

import java.util.*;

import artiano.core.structure.Matrix;
import artiano.ml.clustering.structure.Cluster;

public class Agenes {
	/**
	 * 对数据点使用Agenes算法进行聚类(类间相似度使用MIN进行描述)
	 * @param dataPoints  要进行聚类的数据点
	 * @param clusterNumber 要得到的类的数目
	 * @return 最终的聚类
	 */
	public static List<Cluster> cluster(Matrix dataPoints, int clusterNumber) {
		// 判断输入的数据是否合法
		isDataValid(dataPoints, clusterNumber);

		List<Cluster> finalClusters = new ArrayList<Cluster>();
		// 初始情况下，每个数据点都作为一个簇
		finalClusters = initializeClsters(dataPoints);
		/*
		 * 合并簇间距离最近的两个簇，知道簇的数目等于clusterNumber 类间相似性用MIN来描述
		 */
		while (finalClusters.size() > clusterNumber) {
			//获取当前距离最近的两个簇在簇列表中的下标
			List<Integer> closestClusterIndices = getCurrentTwoClosestClusters(finalClusters);
			//合并这两个簇
			mergeTwoClusters(finalClusters, closestClusterIndices);			
		}
		return finalClusters;
	}

	//合并当前距离最近的两个簇
	private static void mergeTwoClusters(List<Cluster> finalClusters,
			List<Integer> closestClusterIndices) {
		int clusterIndex1 = closestClusterIndices.get(0);
		int clusterIndex2 = closestClusterIndices.get(1);
		Cluster cluster1 = finalClusters.get(clusterIndex1);		
		Cluster cluster2 = finalClusters.get(clusterIndex2);		
		// 将两个簇合并后得到的簇
		Cluster clusterMerged = new Cluster(cluster1,cluster2);
		finalClusters.remove(cluster1);
		finalClusters.remove(cluster2);
		finalClusters.add(clusterMerged);
	}

	// 初始情况下，每个数据点都作为一个簇
	private static List<Cluster> initializeClsters(Matrix dataPoints) {
		List<Cluster> initialClusters = new ArrayList<Cluster>();
		int rows = dataPoints.rows();
		for (int i = 0; i < rows; i++) {
			Matrix dataPoint = dataPoints.row(i);
			Cluster cluster = new Cluster(dataPoint);
			initialClusters.add(cluster);
		}
		return initialClusters;
	}

	// 计算两个样本点之间的欧几里得距离
	private static double getDistance(Matrix dataPoint1, Matrix dataPoint2) {
		double distance = 0;
		int columns = dataPoint1.columns();
		for (int j = 0; j < columns; j++) {
			double temp = Math.pow((dataPoint1.at(j) - dataPoint2.at(j)), 2);
			distance = distance + temp;
		}
		distance = Math.pow(distance, 0.5);
		return distance;
	}

	//获取当前最接近的两个簇
	private static List<Integer> getCurrentTwoClosestClusters(List<Cluster> currentClusters) {
		int clusterIndex_1 = 0;
		int clusterIndex_2 = 0;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < currentClusters.size(); i++) {
			for (int j = 0; j < currentClusters.size(); j++) {
				if (i != j) {
					Cluster cluster_1 = currentClusters.get(i);
					Cluster cluster_2 = currentClusters.get(j);
					Matrix dataPoints_1 = cluster_1.getDataPoints();
					Matrix dataPoints_2 = cluster_2.getDataPoints();
					//获取当前两个簇最小的簇间距离
					for (int m = 0; m < dataPoints_1.rows(); m++) {
						for (int n = 0; n < dataPoints_2.rows(); n++) {
							// 簇1中的点到簇2中的点的距离
							double tempDistance = getDistance(
									dataPoints_1.row(m), dataPoints_2.row(n));
							if (tempDistance < minDistance) {
								minDistance = tempDistance;
								clusterIndex_1 = i;
								clusterIndex_2 = j;
							}
						}
					}
				}
			}
		}
		return Arrays.asList(clusterIndex_1, clusterIndex_2);
	}

	private static void isDataValid(Matrix dataPoints, int clusterNumber) {
		if (dataPoints == null || dataPoints.rows() == 0) {
			throw new IllegalArgumentException("数据集为空!");
		}
		if (clusterNumber <= 1) {
			throw new IllegalArgumentException("最终得到的簇的数目clusterNumber不能少于2!");
		} else if (clusterNumber > dataPoints.rows()) {
			throw new IllegalArgumentException("最终得到的簇的数目clusterNumber不能多于数据点的数目!");
		}
	}
}