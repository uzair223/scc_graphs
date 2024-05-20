import javax.swing.*;
import java.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.Collectors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class Analysis extends JPanel {
  private static final int PREF_W = 800;
  private static final int PREF_H = 650;
  private static final int PADDING = 20;
  private static final int POINT_RADIUS = 25;
  private static final int ARR_SIZE = 4;
  private static final TreePanel treePanel = new TreePanel();

  private Graph<String> invGraph;
  private Graph<String> bfs;
  private Info info;
  
  Map<String, Node> nodes = new HashMap<>();

  private static String selected;

  public Analysis(Graph<String> graph, NetworkParser parser) {
    // initializing graph/variables before draw cycle
    this.invGraph = graph.invert();
    this.info = new Info(graph, parser);
    this.addMouseListener(new MouseHandler());
  }

  void drawArrow(Graphics g1, Point p1, Point p2, int r) {
    // utility function to draw line with arrow end
    Graphics2D g = (Graphics2D) g1.create();

    double dx = p2.x - p1.x, dy = p2.y - p1.y;
    double angle = Math.atan2(dy, dx); // calculating angle of arrow
    int len = (int) Math.sqrt(dx*dx + dy*dy) - r; // calculating length subtracted by radius of node
    // drawing arrow
    AffineTransform at = AffineTransform.getTranslateInstance(p1.x, p1.y);
    at.concatenate(AffineTransform.getRotateInstance(angle));
    g.transform(at);

    g.drawLine(0, 0, len, 0);
    g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                  new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
  }
   
  Color changeAlpha(Color c, int alpha) {
    // utility function to change transparency of colour
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
  }

  @Override
  protected void paintComponent(Graphics g) {
    // main paint cycle
    super.paintComponent(g);
    Graphics2D g1 = (Graphics2D)g;
    g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Set<String> keys = invGraph.getKeys();
    // creating nodes on a circular path
    int i = 0;
    int pointRadius = (Math.min(getWidth(), getHeight()))/2-POINT_RADIUS*2-PADDING;
    for(String key : keys) {
      double rads = 2*Math.PI/invGraph.countVertices()*i;
      int x1 = getWidth()/2+(int)(Math.cos(rads)*pointRadius);
      int y1 = getHeight()/2+(int)(Math.sin(rads)*pointRadius);
      this.nodes.put(key, new Node(new Point(x1,y1), POINT_RADIUS, key, Color.lightGray, Color.darkGray));
      i++;
    }
    // drawing arrows from node to node
    for(String key : keys) {
      Point p1 = nodes.get(key).p;
      for(String neigh : invGraph.get(key)) {
        // if none selected, then make edges gray
        if(bfs==null) g1.setColor(Color.gray);
        // if a node has been selected, then highlight the propagated network's edges
        else if(bfs.hasKey(key) && bfs.get(key).contains(neigh)) g1.setColor(Color.blue);
        // and fade the remaining edges
        else g1.setColor(changeAlpha(Color.gray, 50));
        Node node = nodes.get(neigh);
        drawArrow(g1, node.p, p1, node.r);
      }
    }
    // drawing nodes
    for(String key : keys) {
      Node n = nodes.get(key);
      if(bfs != null && !bfs.hasKey(key) && key != selected) {
        n.color = new Color(0xDD, 0xDD, 0xDD);
      }
      n.draw(g1);
    }
    // drawing info/stats in corner
    g.setColor(Color.black);
    g.setFont(getFont().deriveFont(14f));
    info.draw(g1, PADDING, PADDING);
  }

  @Override
  public Dimension getPreferredSize() { return new Dimension(PREF_W, PREF_H); }

  public static void main(String[] args) {
    // application entry point
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        try {
          // read file
          File file = new File(args[0]);
          Scanner reader = new Scanner(file);
          // convert file to string stream with new line delimiter then parse through network parser
          NetworkParser parser = new NetworkParser(reader.useDelimiter("\n").tokens());
          Graph<String> graph = parser.parse();
          reader.close();
  
          // UI
          Analysis mainPanel = new Analysis(graph, parser);
          JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel.pane, mainPanel);

          splitPane.setOneTouchExpandable(true);
          splitPane.setDividerLocation(150);

          JFrame frame = new JFrame("DrawGraph");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.getContentPane().add(splitPane);
          frame.pack();
          frame.setLocationByPlatform(true);
          frame.setVisible(true);
        } catch (FileNotFoundException e) {
          // handle file not found error
          System.err.println("File not found: "+args[0]);
        }
      }
    });
  }

  private class MouseHandler extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      selected = null;
      bfs = null;
      Point mouse = e.getPoint();
      // select node if mouse click coordinates are within a node's bounding box
      nodes.values().forEach(n -> {
        if(n.contains(mouse)) {
          selected = n.label;
          // compute breadth first search to visualize propagation network
          bfs = invGraph.breadthFirstSearch(selected);
          treePanel.setTree(invGraph, selected);
        }
      });
      // repaint main component
      e.getComponent().getParent().repaint();
    }
  }

  private static class Node {
    // utility node class
    private Point p; // point location
    private int r; // point radius
    private String label;
    private Color color;
    private Color selectColor;
    private Rectangle b = new Rectangle();

    public Node(Point p, int r, String label, Color color, Color selectColor) {
      this.p = p;
      this.r = r;
      this.label = label;
      this.color = color;
      this.selectColor = selectColor;
      setBoundary(b);
    }
    private void setBoundary(Rectangle b) {
      // set bounding box
      b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
    }
    
    public void draw(Graphics g) {
      // draw node as circle with label drawn over; if selected then highlight border
      g.setColor(this.color);
      g.fillOval(b.x, b.y, b.width, b.height);
      if (selected == label) {
        g.setColor(this.selectColor);
        g.drawOval(b.x, b.y, b.width, b.height);
      }
      g.setColor(Color.black);
      FontMetrics metrics = g.getFontMetrics();
      int labelWidth = metrics.stringWidth(label);
      g.drawString(label, p.x-labelWidth/2, p.y+metrics.getHeight()/2-3);
    }

    // check if coordinate is within bounding box
    public boolean contains(Point p) { return b.contains(p); }
  }

  private class Info {
    // utility class to draw information about the network graph
    private Double density;
    private Map.Entry<String, Integer> mostFollowers;
    private Map.Entry<String, Integer> followingMost;
    private Map.Entry<String, Integer> mostOutreach;
    private Double median;

    private String first;
    private int dos;

    Info(Graph<String> graph, NetworkParser parser) {
      // computing all static information before main paint cycle
      density = graph.getDensity();
      List<Integer> numArray = graph.getNumIncomingEdges().values().stream().collect(Collectors.toList()); //.reversed();
      int size = numArray.size();
      if (size % 2 == 0) median = ((double)numArray.get(size/2) + (double)numArray.get(size/2 - 1))/2;
      else median = (double) numArray.get(size/2);
      mostFollowers = graph.getNumIncomingEdges().firstEntry();
      followingMost = graph.getNumOutgoingEdges().firstEntry();
      mostOutreach = graph.invert().findMostConnected();
      first = parser.getToken(0);
      dos = graph.invert().getDistance(first, 2).size();
    }

    public void draw(Graphics g, int x, int y) {
      // drawing labels
      String[] labels = new String[9];
      labels[0] = String.format("1. Density=%.4f", density);
      labels[1] = "2. Most followers: "+mostFollowers;
      labels[2] = "3. Following most: "+followingMost;
      labels[3] = String.format("4. Number of people 2 deg from %s=%d", first, dos);
      labels[4] = String.format("5. Median followers=%.1f", median);
      labels[5] = "6. Most outreach: "+mostOutreach;

      labels[6] = selected != null ? "[SELECTED "+selected+"]" : "[SELECT A NODE]";
      labels[7] = selected != null ? String.format(
          "Followers=%d    Following=%d",
          invGraph.invert().get(selected).size(),
          invGraph.get(selected).size()
        ) : "";
      labels[8] = bfs!=null ? "Outreach="+ (bfs.countVertices()-1) : "";

      FontMetrics metrics = g.getFontMetrics();
      for(int i = 0; i < labels.length; i++) {
        g.drawString(labels[i], x, y + metrics.getHeight()*(i+1)-3);
      } 
    }
  }
}