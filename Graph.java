package graphs;

import java.util.*;

/**
 * Implements a graph. We use two maps: one map for adjacency properties
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated
 * with a vertex.
 * 
 * @author cmsc132
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		this.adjacencyMap = new HashMap<String, HashMap<String, Integer>>();
		this.dataMap = new HashMap<String, E>();
	}

	public void addVertex(String vertexName, E data) {
		if (dataMap.containsKey(vertexName))
			throw new IllegalArgumentException("Given vertex already exists");

		adjacencyMap.put(vertexName, new HashMap<String, Integer>());
		dataMap.put(vertexName, data);
	}

	public void addDirectedEdge(String startVertexName, String endVertexName, int cost) {
		if (!dataMap.containsKey(startVertexName) || !dataMap.containsKey(endVertexName))
			throw new IllegalArgumentException("Given vertices are not part of the graph");

		getAdjacentVertices(startVertexName).put(endVertexName, cost);
	}

	public String toString() {
		TreeMap<String, E> tree = new TreeMap<String, E>();
		for (String vertex : dataMap.keySet()) {
			tree.put(vertex, dataMap.get(vertex));
		}
		StringBuffer ans = new StringBuffer();
		ans.append("Vertices: " + tree.keySet().toString() + "\n");
		ans.append("Edges:" + "\n");
		for (String s : tree.keySet()) {
			TreeMap<String, Integer> adjTree = new TreeMap<String, Integer>();
			for (String adj : adjacencyMap.get(s).keySet()) {
				adjTree.put(adj, adjacencyMap.get(s).get(adj));
			}
			String temp = "Vertex" + "(" + s + ")" + "--->" + adjTree.toString() + "\n";
			ans.append(temp);
		}
		return ans.toString();
	}

	public Map<String, Integer> getAdjacentVertices(String vertexName) {
		if (!adjacencyMap.containsKey(vertexName))
			return new HashMap<String, Integer>();

		return adjacencyMap.get(vertexName);
	}

	public int getCost(String startVertexName, String endVertexName) {
		if (!getAdjacentVertices(startVertexName).containsKey(endVertexName))
			return 0;
		return getAdjacentVertices(startVertexName).get(endVertexName);

	}

	public Set<String> getVertices() {
		return dataMap.keySet();
	}

	public E getData(String vertex) {
		if (!dataMap.containsKey(vertex))
			throw new IllegalArgumentException("Given vertex is not part of the graph");

		return dataMap.get(vertex);

	}

	public void doDepthFirstSearch(String startVertexName, CallBack<E> callBack) {
		if (!dataMap.containsKey(startVertexName))
			throw new IllegalArgumentException("Given vertex is not part of the graph");

		Stack<String> dfsStack = new Stack<String>();
		HashSet<String> visited = new HashSet<String>();

		dfsStack.push(startVertexName);
		while (!dfsStack.isEmpty()) {
			String vertex = dfsStack.pop();
			if (!visited.contains(vertex)) {
				callBack.processVertex(vertex, dataMap.get(vertex));
				for (String s : adjacencyMap.get(vertex).keySet()) {
					dfsStack.push(s);
				}
				visited.add(vertex);
			}
		}
	}

	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		if (!this.dataMap.containsKey(startVertexName))
			throw new IllegalArgumentException("Given vertex is not part of the graph");
		LinkedList<String> queue = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();

		queue.add(startVertexName);
		while (!queue.isEmpty()) {
			String vertex = queue.poll();
			if (!visited.contains(vertex)) {
				callback.processVertex(vertex, this.dataMap.get(vertex));
				queue.addAll(this.adjacencyMap.get(vertex).keySet());
				visited.add(vertex);
			}
		}
	}

	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {
		if (!dataMap.containsKey(startVertexName) || !dataMap.containsKey(endVertexName)) {
			throw new IllegalArgumentException("Given vertices are not part of the graph");
		}

		shortestPath.clear();

		HashMap<String, Integer> distances = new HashMap<String, Integer>();
		HashMap<String, String> predecessor = new HashMap<String, String>();
		Set<String> neighbors = new TreeSet<String>();
		ArrayList<String> notVisited = new ArrayList<String>();
		ArrayList<String> toVisit = new ArrayList<String>();

		for (String vertexes : dataMap.keySet()) {
			notVisited.add(vertexes);
		}
		toVisit.add(startVertexName);

		for (String item : notVisited) {
			distances.put(item, Integer.MAX_VALUE);
			predecessor.put(item, null);
		}

		distances.replace(startVertexName, 0);

		String currentVertex = startVertexName;
		int smallest = 0;

		while (toVisit.size() > 0) {
			neighbors.clear();
			neighbors.addAll(getAdjacentVertices(currentVertex).keySet());

			for (String vertex : neighbors) {
				if (notVisited.contains(vertex)) {
					toVisit.add(vertex);
				}
				if (getCost(currentVertex, vertex) < distances.get(vertex)) {
					distances.replace(vertex, getCost(currentVertex, vertex));
					predecessor.replace(vertex, currentVertex);
				}
			}

			notVisited.remove(currentVertex);
			toVisit.remove(currentVertex);

			if (toVisit.size() != 0) {
				smallest = Integer.MAX_VALUE;
				for (String s : toVisit) {
					if (smallest > distances.get(s)) {
						smallest = distances.get(currentVertex);
						currentVertex = s;
					}
				}
			}
		}

		if (notVisited.contains(endVertexName)) {
			shortestPath.add("None");
			return -1;
		}

		smallest = 0;
		currentVertex = endVertexName;
		String check = endVertexName;

		while (!check.equals(startVertexName)) {
			smallest += distances.get(currentVertex);
			shortestPath.add(0, currentVertex);
			check = currentVertex;
			currentVertex = predecessor.get(currentVertex);
		}
		if (startVertexName.equals(endVertexName)) {
			shortestPath.add(startVertexName);
		}
		return smallest;
	}

}
