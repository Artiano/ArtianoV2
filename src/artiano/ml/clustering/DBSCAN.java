package artiano.ml.clustering;

import java.util.*;
import artiano.core.structure.Matrix;
import artiano.ml.clustering.structure.AbstractGraph.Edge;
import artiano.ml.clustering.structure.*;

public class DBSCAN {
	private double eps;				//半径
	private int minNeighborsNum;	//在指定半径内相邻的点的最少数目
	private Matrix dataPoints;		//数据点
	private List<List<Double>> distances;	//数据点之间的距离	
	private List<Integer> pointType;		//数据点的类型(核心，边界，噪声)
	private int numberOfPoints;				//数据点的个数
	
	public DBSCAN(double eps, int minNeighborsNum, Matrix dataPoints) {
		super();
		this.eps = eps;
		this.minNeighborsNum = minNeighborsNum;
		this.dataPoints = dataPoints;
		this.numberOfPoints = dataPoints.rows();
	}
	
	/**
	 * 对输入的数据点进行聚类处理
	 * @return 划分的所有簇
	 */
	public List<Matrix> cluster() {
		Matrix copyOfDataPoints = this.dataPoints.clone();		
		//计算所有数据点之间的距离
		getDistanceBetweenAnyTwoPoints(copyOfDataPoints);
		//根据数据点之间的距离指定数据点的类型
		assignTypeForAllPoints();		
		//获取来自核心点的簇
		List<Map<Integer, Matrix>> clustersList = 
			getClustersOfCorePoints(copyOfDataPoints);
		//为所有的边界点随机地指派一个与之关联的簇
		putBorderPointsToRelativeCluster(copyOfDataPoints, clustersList);
		//得到最终的簇
		List<Matrix> finalClusters = getFinalClusters(clustersList);
		return finalClusters;
	}

	//获取来自核心点的簇
	private List<Map<Integer,Matrix>> getClustersOfCorePoints(Matrix copyOfDataPoints) {
		//构造核心点构成的图
		Graph<Vertex> graph = constructGraphForCorePoints(copyOfDataPoints);
		//获取图的所有连通分支
		List<List<Integer>> connectedBranches = graph.getConnectedBranches();
		//把每一个连通分支作为一个簇
		List<Map<Integer, Matrix>> pointsOfCoreClusters = 
			new ArrayList<Map<Integer, Matrix>>();
		for(int i=0; i<connectedBranches.size(); i++) {
			List<Integer> branch = connectedBranches.get(i);
			Map<Integer, Matrix> pointsOfACluster = new HashMap<Integer, Matrix>();
			for(int j=0; j<branch.size(); j++) {
				Vertex vertex = graph.getVertex(branch.get(j));
				int indexInDataPoint = vertex.getIndexInDataPoints(); 
				pointsOfACluster.put(indexInDataPoint, 
					copyOfDataPoints.row(indexInDataPoint));
			}
			pointsOfCoreClusters.add(pointsOfACluster);
		}
		return pointsOfCoreClusters;
	}
	
	//为所有的边界点随机地指派一个与之关联的簇
	private void putBorderPointsToRelativeCluster(Matrix copyOfDataPoints,
			List<Map<Integer, Matrix>> pointsOfCoreClusters) {
		for(int i=0; i<numberOfPoints; i++) {
			if(pointType.get(i) == PointType.BORDER) {  //边界点
				for(int j=0; j<numberOfPoints; j++) {
					double distance = getDistance(i, j);
					if(pointType.get(j) == PointType.CORE && distance<=eps) {
						for(Map<Integer, Matrix> clusterMap: pointsOfCoreClusters) {
							//该边界点与此核心点相邻，加到这个核心点所在的簇中
							if(clusterMap.containsKey(j)) {
								clusterMap.put(i, copyOfDataPoints.row(i));
							}
						}
					}
				}
			}
		}
	}		

	//构造核心点构成的图
	private Graph<Vertex> constructGraphForCorePoints(Matrix copyOfDataPoints) {
		//获取核心点
		Map<Integer, Matrix> corePointsMap = getCorePoints(copyOfDataPoints);
		Set<Integer> indices = corePointsMap.keySet();
		//构造图的顶点
		List<Edge> edges = constructEdges(indices);  
		//构造图的顶点
		List<Vertex> corePoints = constructVertices(corePointsMap, indices);
		Graph<Vertex> graph = new UnWeightedGraph<Vertex>(edges, corePoints);
		return graph;
	}

	//得到最终的簇
	private List<Matrix> getFinalClusters(List<Map<Integer, Matrix>> clustersList) {
		List<Matrix> finalClusters = new ArrayList<Matrix>();
		for(int i=0; i<clustersList.size(); i++) {
			Map<Integer, Matrix> clusterMap = clustersList.get(i);
			Set<Integer> indices = clusterMap.keySet();
			int count = 0;
			Matrix firstPointInMap = null;
			for(int index: indices) {
				if(count == 0){
					firstPointInMap = clusterMap.get(index);
					count++;
					continue;
				} else {
					firstPointInMap.mergeAfterRow(clusterMap.get(index));
				}
			}			
			finalClusters.add(firstPointInMap);
		}
		return finalClusters;
	}
	
	//构造图的顶点
	private List<Vertex> constructVertices(Map<Integer, Matrix> corePointsMap,
			Set<Integer> indices) {		
		List<Vertex> corePoints = new ArrayList<Vertex>();
		for(int index: indices) {
			Vertex vertex = new Vertex(index, corePointsMap.get(index));
			corePoints.add(vertex);
		}
		return corePoints;
	}

	//构造图的边
	private List<Edge> constructEdges(Set<Integer> indices) {		
		List<Edge> edges = new ArrayList<Edge>();		
		int i = 0;
		for(Integer index_1: indices) {
			int j = 0;
			for(Integer index_2: indices) {
				if(index_1 < index_2) {					
					double distance = getDistance(index_1, index_2);
					if(distance <= eps) {
						//两个核心点之间的距离小于eps,则在它们之间添加一条边
						Edge edge = new Edge(i, j);
						edges.add(edge);
					}
				}
				j++;
			}
			i++;
		}
		return edges;
	}
	
	//计算所有数据点之间的距离
	private void getDistanceBetweenAnyTwoPoints(Matrix dataPoints) { 
		distances = new ArrayList<List<Double>>(numberOfPoints);
		for(int i=0; i<numberOfPoints; i++) {
			distances.add(new ArrayList<Double>());
			for(int j=0; j<=i; j++) {
				double distance;
				if(j == i) {
					distance = 0;
				} else {
				   distance = getDistance(dataPoints.row(i), dataPoints.row(j));					
				}				
				distances.get(i).add(distance);
			}
		}
	}
	
	//计算两个数据点之间的欧氏距离
	private double getDistance(Matrix verctor_1, Matrix verctor_2) {
		double distance = 0;
		int columns = verctor_1.columns();
		for(int j=0; j<columns; j++) {
			distance += Math.pow(verctor_1.at(j)-verctor_2.at(j), 2);
		}
		return Math.sqrt(distance);
	}
	
	//得到第i个数据点到第j个数据点的距离
	private double getDistance(int i, int j) {
		double distance = 0;
		if(j<=i) {
			distance = distances.get(i).get(j);
		} else {
			distance = distances.get(j).get(i);
		}
		return distance;
	}
	
	//根据数据点之间的距离来指定数据点的类型
	private void assignTypeForAllPoints() {
		pointType = new ArrayList<Integer>();
		for(int i=0; i<numberOfPoints; i++) {			
			pointType.add(PointType.UNASSIGNED);
		}
		assignTypeForCorePoints();		//根据距离确定核心点
		assignTypeForOtherPoints();     //确定边界点和噪声点
	}

	//根据距离确定核心点
	private void assignTypeForCorePoints() {
		for(int i=0; i<numberOfPoints; i++) {
			int numOfPointsInEps = 0;  //数据点i半径eps之内的数据点数
			for(int j=0; j<numberOfPoints; j++) {
				if(j == i) {
					continue;
				}
				double distance = getDistance(i, j);		
				if(distance <= eps) {
					numOfPointsInEps += 1;
				}
			}
			if(numOfPointsInEps >= minNeighborsNum) { 
				pointType.set(i, PointType.CORE);		//核心点
			}
		}
	}
	
	//确定边界点和噪声点
	private void assignTypeForOtherPoints() {
		for(int i=0; i<numberOfPoints; i++) {
			if(pointType.get(i) == PointType.UNASSIGNED) {  //还未指定类型
				boolean isNoisePoint = true; 
				for(int j=0; j<numberOfPoints; j++) {
					double distance = getDistance(i, j);
					if(pointType.get(j) == PointType.CORE && distance <= eps) {
						//与核心点的距离小于eps，为边界点
						isNoisePoint = false;
						break;
					}
				}
				if(isNoisePoint) {
					pointType.set(i, PointType.NOISE);
				} else {
					pointType.set(i, PointType.BORDER);
				}
			}
		}
	}
		
	//获取核心点
	private Map<Integer, Matrix> getCorePoints(Matrix dataPoints) {
		Map<Integer, Matrix> corePoints = new HashMap<Integer, Matrix>();
		int numOfPoints = dataPoints.rows();
		for(int i=0; i<numOfPoints; i++) {
			if(pointType.get(i) == PointType.CORE) {
				corePoints.put(i, dataPoints.row(i));
			}
		}
		return corePoints;
	}
	
	//获取被划分为噪声点的数据点
	public List<Matrix> getNoisePoints() {
		List<Matrix> noisePointList = new ArrayList<Matrix>();
		for(int i=0; i<numberOfPoints; i++) {
			if(pointType.get(i) == PointType.NOISE) {
				noisePointList.add(dataPoints.row(i));
			}
		}
		return noisePointList;
	}
	
	//数据点的类型类
	private final class PointType {
		final static int UNASSIGNED = 0;	//没有指定时为UNASSIGNED
		final static int CORE = 1;		//核心点
		final static int BORDER = 2;	//边缘点
		final static int NOISE = 3;		//噪声点
	}
}
