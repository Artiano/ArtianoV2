package artiano.ml.clustering.structure;

import java.util.List;

public class UnWeightedGraph<V> extends AbstractGraph<V> {
	/** Construct a graph from edges and vertices stored in arrays */
	public UnWeightedGraph(int[][] edges, V[] vertices) {
		super(edges, vertices);
	}

	/** Construct a graph from edges and vertices stored in List */
	public UnWeightedGraph(List<Edge> edges, List<V> vertices) {
		super(edges, vertices);
	}
	
	/** Construct a graph for integer vertices 0,1,2 and edge list */
	public UnWeightedGraph(List<Edge> edges, int numberOfVertices) {
		super(edges, numberOfVertices);
	}
	
	/** Construct a graph for integer vertices 0,1,2 and edge array */
	public UnWeightedGraph(int[][] edges, int numberOfVertices) {
		super(edges, numberOfVertices);
	}
}
