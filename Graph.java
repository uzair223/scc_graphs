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
    for(var vertex : graph.entrySet()) {
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
    for(var vertex : graph.entrySet()) {
      for(T to : vertex.getValue()) {
        invGraph.addDirectedEdge(to, vertex.getKey());
      }
    }
    return invGraph;
  }

  public List<T> getDistance(T src, int degrees) {
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
      // If current vertex is within degrees distance from src (excluding src) then add to output array.
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
  public Set<T> breadthFirstSearch(T src) {
    Set<T> visited = new HashSet<>();
    Queue<T> queue = new LinkedList<>();

    queue.add(src);
    visited.add(src);

    while (!queue.isEmpty()) {
      T currentVertex = queue.remove();
      for(T neigh : graph.get(currentVertex)) {
        if(visited.contains(neigh)) continue;
        visited.add(neigh);
        queue.add(neigh);
      }
    }

    visited.remove(src);
    return visited;
  }

  public Map.Entry<T,Integer> findMostConnected() {
    return sortMap(graph.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey,
      x-> breadthFirstSearch(x.getKey()).size(),
      (a,b)->b,
      HashMap::new))).firstEntry();
  }
}
