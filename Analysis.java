/*
 * SCC Social Networks Summer Project
*/
public class Analysis {
  
  public static void main(String[] args) {
    String inputString = "Raman Abe Carla Anwar Cat\nCat Abe\nCarla Raman Abe\nAbe\nAnwar Abe Carla Raman";
    Graph<String> network = new NetworkParser(inputString).parse();
    
    System.out.println(network.countVertices());
   }
}