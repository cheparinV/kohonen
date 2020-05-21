package practice.desktop;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Demo extends JPanel implements TreeSelectionListener {

    private JTree tree;
    private JTextArea logPane;
    private JSplitPane workspace;

    public Demo() {
        super(new GridLayout(1,0));

        //Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Smile");
        createNodes(top);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);

        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(Color.white);

        //Create the log pane.
        logPane = new JTextArea();
        logPane.setEditable(false);
        JScrollPane logView = new JScrollPane(logPane);
        redirectSystemStreams();

        workspace = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        workspace.setTopComponent(placeholder);
        workspace.setBottomComponent(logView);
        workspace.setDividerLocation(700);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(treeView);
        splitPane.setRightComponent(workspace);

        Dimension minimumSize = new Dimension(100, 50);
        placeholder.setMinimumSize(minimumSize);
        logView.setMinimumSize(minimumSize);
        workspace.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(300);
        splitPane.setPreferredSize(new Dimension(1000, 800));

        //Add the split pane to this panel.
        add(splitPane);
    }

    private void createNodes(DefaultMutableTreeNode top) {

    }

    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logPane.append(text);
            }
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    /** Required by TreeSelectionListener interface. */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node != null && node.isLeaf()) {
            int pos = workspace.getDividerLocation();
            workspace.setTopComponent((JPanel) node.getUserObject());
            workspace.setDividerLocation(pos);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI(boolean exitOnClose) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println(ex);
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Smile Demo");
        frame.setSize(new Dimension(1600, 1200));
        frame.setLocationRelativeTo(null);

        if (exitOnClose)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        else
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Add content to the window.
        frame.add(new Demo());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI(true));
    }
}
