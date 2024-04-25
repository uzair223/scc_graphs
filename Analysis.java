import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * SCC Social Networks Summer Project
*/
public class Analysis {
  
  public static void main(String[] args) {
    try {
      File file = new File(args[0]);
      Scanner reader = new Scanner(file);
      Graph<String> network = new NetworkParser(reader.useDelimiter("\n").tokens()).parse();
      reader.close();
      System.out.println(network.countVertices());
    } catch (FileNotFoundException e) {
      System.out.println("An error occured");
      e.printStackTrace();
    }
   }
}