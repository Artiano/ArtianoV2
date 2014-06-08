package artiano.ml.clustering.structure;

import artiano.core.structure.Matrix;

public class Vertex {
	private int indexInDataPoints;   //在数据点列表dataPoints中的下标
	private Matrix data;			 //该顶点的数据
	
	public Vertex(int indexInDataPoints, Matrix data) {
		super();
		this.indexInDataPoints = indexInDataPoints;
		this.data = data;
	}

	public int getIndexInDataPoints() {
		return indexInDataPoints;
	}

	public void setIndexInDataPoints(int indexInDataPoints) {
		this.indexInDataPoints = indexInDataPoints;
	}

	public Matrix getData() {
		return data;
	}

	public void setData(Matrix data) {
		this.data = data;
	}
	
}
