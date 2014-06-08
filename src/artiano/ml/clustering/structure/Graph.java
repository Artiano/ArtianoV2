package artiano.ml.clustering.structure;

import java.util.List;

public interface Graph<V> {
	/** 返回图中顶点的数目 */
	public int getSize();
	
	/** 返回图中的顶点集合  */
	public java.util.List<V> getVertices();
	
	/** 返回图中指定下标的顶点  */
	public V getVertex(int index);
	
	/** 返回图中指定顶点的下标  */
	public int getIndex(V v);
	
	/** 返回图中指定下标处的顶点的相邻顶点  */
	public java.util.List<Integer> getNeighbors(int index);
	
	/** 返回指定顶点的度数 */
	public int getDegree(int index);
	
	/** 返回图的邻接矩阵 */
	public int[][] getAdjacencyMatrix();
	
	/** 打印图的邻接矩阵 */
	public void printAdjacencyMatrix();
	
	/** 打印图的边 */
	public void printEdges();
	
	/** 得到图的连通部分 */
	public List<List<Integer>> getConnectedBranches();
	
	/** 获取图的深度优先查找树 */
 	public AbstractGraph<V>.Tree dfs(int v);
 	
 	/** 获取图的广度优先遍历树 */
 	public AbstractGraph<V>.Tree bfs(int v);
}
