import java.util.*;
import java.util.stream.Stream;
/*
 * Class to parse string into graph
 */
public class NetworkParser {
  Stream<String> lineStream;
  Set<String> uniqueTokens = new LinkedHashSet<>();

  NetworkParser(Stream<String> lineStream) {
    this.lineStream = lineStream;
  }

  public Graph<String> parse(){
    Graph<String> graph = new Graph<>();
    // for each line
    lineStream.forEach(line -> {
      // split line into word tokens
      ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(line.strip().split(" ")));
      if(tokens.size() < 1) return;
      String from = tokens.remove(0).strip(); // vertex to create edges from
      uniqueTokens.add(from); // add to set of unique tokens
      // for each remaining token (excluding first)
      for(String token : tokens) {
        String vertex = token.strip();
        if(vertex.length() <= 0) continue;
        graph.addDirectedEdge(from,vertex); // add edge between first token and current
        uniqueTokens.add(vertex); // add to set of unique tokens
      }
    });
    return graph;
  }

  public String getToken(int index) {
    return (String)uniqueTokens.toArray()[index];
  }
}
