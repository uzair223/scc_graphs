import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;

public class TreePanel {
  public JPanel pane = new JPanel();
  private JTree tree = new JTree();
  private Graph<String> graph;
  private Map<String, DefaultMutableTreeNode> nodeMap;

  public TreePanel() {
    DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
    renderer.setLeafIcon(null);
    renderer.setClosedIcon(null);
    renderer.setOpenIcon(null);
    tree.setVisible(false);

    pane.setLayout(new BorderLayout());
    tree.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
    pane.add(new JLabel("Propagation tree"), BorderLayout.PAGE_START);
    pane.add(tree, BorderLayout.CENTER);
  }
  
  public void setTree(Graph<String> graph, String root) {
    this.graph = graph;
    tree.setVisible(true);
    nodeMap = new HashMap<>();
    pane.setVisible(true);
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
    nodeMap.put(root, rootNode);
    construct(root, rootNode);

    DefaultTreeModel model = new DefaultTreeModel(rootNode);
    tree.setModel(model);
    for (int i = 0; i < tree.getRowCount(); i++) {
      tree.expandRow(i);
    }
    tree.repaint();
  }

  private void construct(String key, DefaultMutableTreeNode node) {
    for(String child : this.graph.get(key)) {
      if (!this.nodeMap.containsKey(child)) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        this.nodeMap.put(child, childNode);
        node.add(childNode);
        this.construct(child, childNode);
      }
    }
   }
}