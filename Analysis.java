import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
 * SCC Social Networks Summer Project
*/
public class Analysis {
  
  public static void main(String[] args) {
    try {
      File file = new File(args[0]);
      Scanner reader = new Scanner(file);
      NetworkParser parser = new NetworkParser(reader.useDelimiter("\n").tokens());
      Graph<String> network = parser.parse();
      reader.close();
      
      // Task 1
      System.out.println("Network density:\t\t\t"+network.getDensity());
      // Task 2
      System.out.println("Highest number of followers:\t\t"+network.getNumIncomingEdges().firstEntry().getKey());
      // Task 3
      System.out.println("Follows the most people:\t\t"+network.getNumOutgoingEdges().firstEntry().getKey());
      // Task 4
      String first = parser.getToken(0);
      int degrees = 2;
      System.out.println("Number of people "+degrees+" d.o.s. from "+first+":\t"+network.invert().getDistance(first, degrees).size());
      // Task 5
      var numArray = network.getNumIncomingEdges().values().stream().collect(Collectors.toList()).reversed();
      int size = numArray.size();
      double median;
      if (size % 2 == 0) median = ((double)numArray.get(size/2) + (double)numArray.get(size/2 - 1))/2;
      else median = (double) numArray.get(size/2);
      System.out.println("Median number of followers:\t\t"+median);

    } catch (FileNotFoundException e) {
      System.out.println("An error occured");
      e.printStackTrace();
    }
   }
}