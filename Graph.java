import java.util.*;
import java.util.stream.Collectors;
/*
 * Implementing generic graph abstract datatype
 * with required operations
 */

public class Graph<T extends Comparable<T>> {
  private Map<T, List<T>> graph = new HashMap<>();

  public void addVertex(T vertex) {
    graph.putIfAbsent(vertex, new LinkedList<T>());
  }

  public void addDirectedEdge(T from, T to) {
    if(!graph.containsKey(from)) this.addVertex((from));
    if(!graph.containsKey(to)) this.addVertex(to);
    graph.get(from).add(to);
  }

  public int countVertices() { return graph.keySet().size(); }
  
  public int countEdges() {
    int count = 0;
    for(T key : graph.keySet()) count += graph.get(key).size();
    return count;
  }


  public Set<T> getKeys() { return graph.keySet(); }
  public List<T> get(T key) { return graph.get(key); }
  public boolean hasKey(T key) { return graph.containsKey(key); }

  // Task 1 - graph density using formula: D = |E|/|(V|(|V|-1)) 
  public double getDensity() {
    double E = (double)this.countEdges();
    double V = (double)this.countVertices();
    return E/(V*(V-1));
  }

  private LinkedHashMap<T, Integer> sortMap (Map<T, Integer> map){
    /* Map<T, Integer> Sort utility function:
     * --------------------------------------
     * sort by number of incoming edges
     * if vertices have the same number of incoming edges, then sort by key
     */
    return map.entrySet()
    .stream()
    .sorted(
      new Comparator<Map.Entry<T, Integer>>() {
        @Override
        public int compare(Map.Entry<T,Integer> o1, Map.Entry<T,Integer> o2) {
          int cmp = -o1.getValue().compareTo(o2.getValue());
          if(cmp != 0) return cmp;
          return o1.getKey().compareTo(o2.getKey());
        };
      }
    )
    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, 
       (a,b)->b, LinkedHashMap::new));
  }

  // Task 2 - maximum incoming edges
  public LinkedHashMap<T, Integer> getNumIncomingEdges() {
    Map<T, Integer> incomingEdges = new HashMap<>();
    /*
     * for each vertex in the graph,
     * for each outgoing edge from the current vertex,
     *    if incomingEdges map already contains the vertex
     *      then increment by 1,
     *      otherwise initialize to 1.
     */
    for(Map.Entry<T, List<T>> vertex : graph.entrySet()) {
      for(T to : vertex.getValue()) {
        incomingEdges.merge(to, 1, Integer::sum);
      }
    }
    return sortMap(incomingEdges);
  }

  // Task 3 - maximum outgoing edges
  public LinkedHashMap<T, Integer> getNumOutgoingEdges() {
    return sortMap(
      graph.entrySet().stream().collect(Collectors.toMap(
          Map.Entry::getKey,
          x->x.getValue().size(), // transforming to map with key T and value correspondent to T's connections
          (a,b)->b,
          HashMap::new
        )
    ));
  }

  // Task 4 - degrees of separation
  public Graph<T> invert() {
    /*
     * Graph invert utility function
     * -----------------------------
     * Function to invert a -> b relation to b <- a
     * i.e. convert from graph of following to graph of followers
     */
    Graph<T> invGraph = new Graph<>();
    for(Map.Entry<T, List<T>> vertex : graph.entrySet()) {
      for(T to : vertex.getValue()) {
        invGraph.addDirectedEdge(to, vertex.getKey());
      }
    }
    return invGraph;
  }

  public List<T> getDistance(T src, int degrees) {
    // returning list of vertices 'degrees' distance away from src
    List<T> out = new LinkedList<>();

    Queue<T> queue = new LinkedList<>();
    Set<T> visited = new HashSet<>();
    Map<T, Integer> distances = new HashMap<>();

    queue.add(src);
    visited.add(src);
    distances.put(src, 0);

    // while there are still unvisited vertices in the queue
    while(!queue.isEmpty()) {
      // pop queue
      T currentVertex = queue.remove();
      Integer currentDistance = distances.get(currentVertex);
      // If current vertex is degrees distance from src (excluding src) then add to output array.
      if(currentDistance == degrees) out.add(currentVertex);
      // If current vertex is less than degrees distance, then add unvisited neighbours to queue for searching.
      if(currentDistance < degrees) {
        for(T neigh : graph.get(currentVertex)) {
          if(visited.contains(neigh)) continue;
          queue.add(neigh);
          visited.add(neigh);
          distances.put(neigh, currentDistance+1);
        }
      }
    }
    return out;
  }

  // Task 6 - maximum reach
  public Graph<T> breadthFirstSearch(T src) {
    // returning subgraph of results of breadth first search rooted at 'src' vertex
    Set<T> visited = new HashSet<>();
    Graph<T> visitedGraph = new Graph<>();
    Queue<T> queue = new LinkedList<>();

    queue.add(src);
    visited.add(src);

    // while there are still vertices nodes in the queue
    while (!queue.isEmpty()) {
      // pop queue
      T currentVertex = queue.remove();
      // explore neighbours
      List<T> neighbours =  graph.get(currentVertex);
      neighbours.sort(Comparator.naturalOrder());
      for(T neigh : neighbours) {
        // if neighbour is already visited then skip
        if(visited.contains(neigh)) continue;
        // otherwise add edge between current vertex and neighbour in the visited subgraph
        // queue the neighbour vertex for searching
        visitedGraph.addDirectedEdge(currentVertex, neigh);
        visited.add(neigh);
        queue.add(neigh);
      }
    }
    return visitedGraph;
  }

  public Map.Entry<T,Integer> findMostConnected() {
    // compute bfs on each node; return one with the highest no. of connections
    return sortMap(graph.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey,
      x -> breadthFirstSearch(x.getKey()).getKeys().size()-1,
      (a,b)->b,
      HashMap::new))).firstEntry();
  }
}
