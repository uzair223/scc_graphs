import java.util.*;
import java.util.stream.Stream;
/*
 * Class to parse string into graph
 */
public class NetworkParser {
  Stream<String> lineStream;

  NetworkParser(Stream<String> lineStream) {
    this.lineStream = lineStream;
  }
  public Graph<String> parse(){
    Graph<String> graph = new Graph<>();
    lineStream.forEach(line -> {
      ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(line.split(" ")));
      if(tokens.size() < 1) return;
      String from = tokens.remove(0);
      for(String token : tokens) {
        String vertex = token.strip();
        if(vertex.length() > 0) graph.addDirectedEdge(from,vertex);
      }
    });
    return graph;
  }
}
