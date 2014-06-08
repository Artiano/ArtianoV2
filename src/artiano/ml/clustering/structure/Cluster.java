package artiano.ml.clustering.structure;

import artiano.core.structure.Matrix;

public class Cluster {
	private Matrix dataPoints;		//簇内的数据集
	private String clusterName;		//簇的名字			
	
	public Cluster(Matrix dataPoints) {
		this(dataPoints, "");
	}

	public Cluster(Matrix dataPoints, String clusterName) {
		super();
		this.dataPoints = dataPoints;
		this.clusterName = clusterName;
	}

	//合并两个簇为一个簇
	public Cluster(Cluster cluster1, Cluster cluster2) {
		this(cluster1, cluster2, "");
	}
	
	//合并两个簇为一个簇并指定新簇的名字
	public Cluster(Cluster cluster1, Cluster cluster2, 
			String newClusterName) {
		mergeTwoCluster(cluster1, cluster2, newClusterName);
	}

	//合并两个簇为一个簇并指定新簇的名字
	private void mergeTwoCluster(Cluster cluster1, Cluster cluster2,
			String newClusterName) {
		Matrix data_1 = cluster1.dataPoints.clone();   //clone
		Matrix data_2 = cluster2.dataPoints.clone();
		int cols_1 = data_1.columns();
		int cols_2 = data_2.columns();	
		if(cols_1 != cols_2) {
			throw new IllegalArgumentException("cluster1与cluster2中数据点的维数不一致!");
		}				
		this.dataPoints = new Matrix(data_1.rows(), data_1.columns(), data_1.data()); 
		dataPoints.mergeAfterRow(data_2);
		this.clusterName = newClusterName;
	}
	
	public Matrix getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(Matrix dataPoints) {
		this.dataPoints = dataPoints;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	public boolean equals(Object obj) {
		if(! (obj instanceof Cluster) ) {
			return false;
		} else {
			Cluster cluster = (Cluster)obj;
			if(cluster.getDataPoints().equals(this.dataPoints)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
