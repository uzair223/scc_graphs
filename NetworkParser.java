import java.util.*;
/*
 * Class to parse string into graph
 */
public class NetworkParser {
  String rawString;

  NetworkParser(String rawString) {
    this.rawString = rawString;
  }
  public Graph<String> parse(){
    Graph<String> graph = new Graph<>();
    rawString.lines().forEach(line -> {
      ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(line.split(" ")));
      if(tokens.size() < 1) return;
      String from = tokens.remove(0);
      for(String vertex : tokens) {
        graph.addDirectedEdge(from, vertex);
      }
    });
    return graph;
  }
}
