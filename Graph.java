import java.util.*;
/*
 * Implementing generic graph abstract datatype
 * with required operations
 */

public class Graph<T> {
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
}
