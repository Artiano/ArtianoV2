package artiano.ml.clustering.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGraph<V> implements Graph<V> {
	protected List<V> vertices;		//顶点
	protected List<List<Integer>> neighbors;	//邻接表		
	
	/** Construct a graph from edges and vertices stored in arrays */
	protected AbstractGraph(int[][] edges, V[] vertices){
		this.vertices = new ArrayList<V>();
		for(int i=0; i<vertices.length; i++) {
			this.vertices.add(vertices[i]);
		}
		
		createAdjacencyLists(edges, vertices.length);
	}
	
	/** Construct a graph from edges and vertices stored in List */
	protected AbstractGraph(List<Edge> edges, List<V> vertices){
		this.vertices = vertices;
		createAdjacencyLists(edges, vertices.size());
	}
	
	/** Construct a graph for integer vertices 0,1,2... and edge list */
	@SuppressWarnings("unchecked")
	protected AbstractGraph(List<Edge> edges, int numberOfVertices){
		vertices = new ArrayList<V>();	//Create vertices
		for(int i=0; i<numberOfVertices; i++){
			vertices.add((V)(new Integer(i)));	//vertices is {0,1,...}
		}
		
		createAdjacencyLists(edges, numberOfVertices);
	}
	 
	/** Construct a graph from integer vertices 0,1 and edge array */
	@SuppressWarnings("unchecked")
	protected AbstractGraph(int[][] edges, int numberOfVertices){
		vertices = new ArrayList<V>();
		for(int i=0; i<numberOfVertices; i++){
			vertices.add((V)(new Integer(i)));
		}
		
		createAdjacencyLists(edges, numberOfVertices);		
	}
	
	/** Create adjacency lists for each vertex */
 	private void createAdjacencyLists(int[][] edges,
			int numberOfVertices) {
		//Create a linked list 
		neighbors = new ArrayList<List<Integer>>();
		for(int i=0; i<numberOfVertices; i++) {
			neighbors.add(new ArrayList<Integer>());
		}
		
		for(int i=0; i<edges.length; i++) {
			int u = edges[i][0];
			int v = edges[i][1];
			neighbors.get(u).add(v);
		}
	}
	
 	/** Create adjacency lists for each vertex */
 	private void createAdjacencyLists(List<Edge> edges, 
 			int numberOfVertices){
 		//Create a linked list
 		neighbors = new ArrayList<List<Integer>>();
 		for(int i=0; i<numberOfVertices; i++){
 			neighbors.add(new ArrayList<Integer>());
 		}
 		
 		for(Edge edge : edges) {
 			neighbors.get(edge.u).add(edge.v);
 		}
 	}

 	/** 返回图中顶点的数目 */
 	public int getSize(){
 		return vertices.size();
 	}
 	
 	/** 返回途中的顶点集合 */
 	public List<V> getVertices(){
 		List<V> verticesCopy = new ArrayList<V>();
 		for(int i=0; i<vertices.size(); i++){
 			verticesCopy.add(vertices.get(i));
 		}
 		
 		return verticesCopy;
 	}
 	
 	/** 返回图中指定下标的顶点  */
 	public V getVertex(int index){
 		return vertices.get(index);
 	}
 	
 	/** 返回图中指定顶点的下标  */
 	public int getIndex(V v){
 		return vertices.indexOf(v);
 	}
 	
 	/** 返回图中指定下标处的顶点的相邻顶点  */
 	public List<Integer> getNeighbors(int index){
 		return neighbors.get(index);
 	}
 	
 	/** 返回图中指定下标的顶点  */
 	public int getDegree(int v){
 		return neighbors.get(v).size();
 	}
 	
 	/** 返回图中指定下标处的顶点的相邻顶点  */
 	public int[][] getAdjacencyMatrix(){
 		int[][] adjacencyMatrix = new int[getSize()][getSize()]; 		
 		for(int i=0; i<neighbors.size(); i++){
 			for(int j=0; j<neighbors.get(i).size(); j++){
 				int v = neighbors.get(i).get(j);
 				adjacencyMatrix[i][v] = 1;
 			}
 		} 		
 		return adjacencyMatrix;
 	}

 	/** 打印图的邻接矩阵 */
 	public void printAdjacencyMatrix(){
 		int[][] adjacencyMatrix = getAdjacencyMatrix();
 		for(int i=0; i<adjacencyMatrix.length; i++){
 			for(int j=0; j<adjacencyMatrix.length; j++){
 				System.out.print(adjacencyMatrix[i][j]+" ");
 			}
 			System.out.println();
 		}
 		System.out.println(); 		
 	}

 	/** 打印图的边 */
 	public void printEdges(){
 		for(int u=0; u<neighbors.size(); u++){
 			System.out.print("Vertex "+u+": ");
 			for(int j=0; j<neighbors.get(u).size(); j++){
 				System.out.print("("+u+", "+neighbors.get(u).get(j)+") ");
 			}
 			System.out.println();
 		}
 	} 	 	
 	
 	/** 得到图的连通部分 */
 	public List<List<Integer>> getConnectedBranches(){
 		int v = 0 ;
 		List<List<Integer>> connectedParts = 
 			new ArrayList<List<Integer>>();
 		int numberOfVerticesFound = 0; 		
 		while(numberOfVerticesFound < getSize()){
 			Tree tree = bfs(v);
 			numberOfVerticesFound += 
 				tree.getNumberOfVerticesFound(); //+= not =
 			int vertexnSearched = 
 				getNumOfUnserachedVertex(tree); 			
 			if(vertexnSearched == -1){
 				connectedParts.add(tree.searchOrders) ;
 				return connectedParts;
 			}else {
 				connectedParts.add(tree.searchOrders) ;
 				v = vertexnSearched;
 			}
 		} 		 		 		
		return connectedParts; 		
 	}
 	
 	private int getNumOfUnserachedVertex(Tree tree){
 		List<Integer> verticesSearched = 
 			tree.searchOrders;
 		int numberOfVertices = getSize();
 		for(int i=0; i<numberOfVertices; i++){
 			if(! verticesSearched.contains(i)){ 
 				return i;		//该图不是连通图
 			}
 		} 
		return -1;		//The graph is connected 		
 	}
 	
 	/** Edge inner class inside the AbstractGraph class */
 	public static class Edge {
 		public int u;	//边的起点对应的顶点的下标
 		public int v;	//边的终点对应的顶点的下标		
 		
 		/** 构造边(u, v) */
 		public Edge(int u, int v) {
			super();
			this.u = u;
			this.v = v;
		}
 	}
 	
 	/** 从下标为v的顶点处开始深度优先搜索图 */
 	public Tree dfs(int v){
 		List<Integer> searchOrders = new ArrayList<Integer>();
 		int[] parent = new int[vertices.size()];
 		for(int i=0; i<parent.length; i++){
 			parent[i] = -1; 			
 		} 		
 		//标志顶点是否已经被遍历
 		boolean[] isVisited = new boolean[vertices.size()]; 		
 		//递归搜索
 		dfs(v, parent, searchOrders, isVisited); 		
 		return new Tree(v, parent, searchOrders);
 	}
 	
 	/** 深度优先搜索 */
 	private void dfs(int v, int[] parent, List<Integer> searchOrders,
 			boolean[] isVisited){
 		//将当前访问到的顶点加到搜索到的顶点列表中
 		searchOrders.add(v);
 		isVisited[v] = true;	//顶点v已经被访问 		
 		for(int i : neighbors.get(v)){
 			if(! isVisited[i]){
 				parent[i] = v;
 				dfs(i, parent, searchOrders, isVisited);	//递归搜索
 			}
 		}
 	}
 	
 	/** 从下标为v的顶点开始进行广度优先遍历 */
 	public Tree bfs(int v){
 		List<Integer> searchOrders = new ArrayList<Integer>();
 		int[] parent = new int[vertices.size()];
 		for(int i=0; i<parent.length; i++){
 			parent[i] = -1; 			
 		}
 		
 		LinkedList<Integer> queue = 
 			new LinkedList<Integer>();
 		boolean[] isVisited = new boolean[vertices.size()];
 		queue.offer(v);
 		isVisited[v] = true;	//标记为已经访问
 		
 		while(! queue.isEmpty()){
 			int u = queue.poll();	//Dequeue to u
 			searchOrders.add(u);	//u searched 			
 			for(int w: neighbors.get(u)){
 				if(! isVisited[w]){
 					queue.offer(w);		//Enqueue w
 					parent[w] = u;
 					isVisited[w] = true;	//标记为已经访问
 				} 				
 			} 			
 		} 		
 		return new Tree(v, parent, searchOrders);
 	}
 	
 	/** Tree内部类 */
 	public class Tree {
 		private int root ;	//The root of the tree
 		private int[] parent;	//Store the parent of each vertex
 		private List<Integer> searchOrders ; 	//Store the search order
		 		
 		/** Construct a tree with root , parent and search order */
 		public Tree(int root, int[] parent, List<Integer> searchOrders) {
			this.root = root;
			this.parent = parent;
			this.searchOrders = searchOrders;
		}
 		
 		/** 返回树的根节点 */
 		public int getRoot(){
 			return root;
 		} 
 		
 		/** Return the parent of vertex v */
 		public int getParent(int v){
 			return parent[v];
 		}
 		
 		/** 返回遍历中得到的顶点列表 */
 		public List<Integer> getSearchOrders(){
 			List<Integer> copyOfSearchOrders = 
 				new  ArrayList<Integer>();
 			for(Integer integer : searchOrders){
 				copyOfSearchOrders.add(integer);
 			} 			
 			return copyOfSearchOrders;
 		}
 		
 		/** 返回查找到的顶点的数目 */
 		public int getNumberOfVerticesFound(){
 			return searchOrders.size();
 		}
 		
 		/** 得到从根节点到下标为index的节点的路径  */
 		public List<V> getPath(int index){
 			ArrayList<V> path = new ArrayList<V>();
 			
 			do {
 				path.add(vertices.get(index));
 				index = parent[index];
 			}while(index != -1);
 			
 			return path;
 		}
 		
 		/** 输出从根节点到下标为index的节点的路径 */
 		public void printPath(int index){
 			List<V> path = getPath(index);
 			System.out.print("A path from "+vertices.get(root)+" to "+
 					vertices.get(index)+": ");
 			for(int i=path.size()-1; i>=0; i--){
 				System.out.print(path.get(i)+" ");
 			}
 			System.out.println();
 		}
 	
 		public void printTree() {
 			System.out.println("Root is: "+vertices.get(root));
 			System.out.print("Edges:  ");
 			for(int i=0; i<parent.length; i++){
 				if(parent[i] != -1){
 					//Display an edge
 					System.out.print("("+vertices.get(parent[i])+
 							", "+vertices.get(i)+")");
 				}
 			}
 			System.out.println();
 		}
 	}
}