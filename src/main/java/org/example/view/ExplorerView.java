package org.example.view;

import net.miginfocom.swing.MigLayout;
import org.example.Item;
import org.example.Main;
import org.example.TabPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Flow;

import com.formdev.flatlaf.extras.*;
import org.example.WrapLayout;


public class ExplorerView extends TabPage {

    public static final int ICON_SIZE = 30;

    private static int count = 1;

    JSplitPane splitPane;
    JTabbedPane shortcutTabs;
    JPanel panel;
    public JTree tree;

    public ExplorerView() {
        super("File Explorer " + count);
        count++;
        initView();
    }

    private void initView() {

        panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // Left panel
        shortcutTabs = new JTabbedPane(JTabbedPane.LEFT);

        shortcutTabs.putClientProperty("JTabbedPane.tabAlignment", SwingConstants.LEADING);
        shortcutTabs.putClientProperty("JTabbedPane.tabIconPlacement", SwingConstants.LEADING);


        JTextField left = new JTextField();
        left.setBackground(Color.BLUE);
        left.setOpaque(true);
        left.setPreferredSize(new Dimension(70, 100));

        // Main panel
        JPanel center = new JPanel();
        center.setBackground(Color.RED);
        center.setPreferredSize(new Dimension(100, 100));
        center.setLayout(new MigLayout("insets 0, wrap 1", "[grow]0", "[]0[grow]0"));

        JPanel path = new JPanel();
        path.setBackground(Color.YELLOW);
        path.setPreferredSize(new Dimension(5, 60));

        JPanel files = new JPanel();
        files.setBackground(Color.CYAN);

        center.add(path, "growx");
        center.add(files, "grow");


        panel.add(shortcutTabs, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);

        JPanel fileView_p = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));

        fileView_p.add(new Item(new File("C:\\Users\\aless\\Documents")));
        fileView_p.add(new Item(new File("C:\\Users\\aless\\Pictures")));
        fileView_p.add(new Item(new File("C:\\Users\\aless\\Music")));
        fileView_p.add(new Item(new File("C:\\Users\\aless\\Downloads\\Telegram Desktop\\photo_2024-08-22_14-29-16.jpg")));
        fileView_p.add(new Item(new File("C:\\Users\\aless\\Downloads\\Iscrizione.pdf")));
//        for (int i = 0; i < 15; i++) {
//            JButton d = new JButton("Test" + i);
//            int dim  = (int) (Main.screenDimension.width * 0.08);
//            d.setPreferredSize(new Dimension(dim, dim));
//            fileView_p.add(d);
//        }

        JScrollPane fileView = new JScrollPane(fileView_p);
        fileView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileView.setBorder(new EmptyBorder(0, 0, 0, 0));
        fileView.setMinimumSize(new Dimension(400, 50));


        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new File("My Computer"));
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.setFocusable(false);
//        tree.setCellRenderer(new FileSystemTreeRenderer());
        tree.setEditable(true);

        JScrollPane treePane = new JScrollPane(tree);

        JPanel leftPanel = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "[]", "[][][grow]"));

        JPanel shortcutBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "", "[fill]0"));
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);

        for (int i = 0; i < 5; i++) {
            JButton b = new JButton("Test");
            b.setFocusable(false);
            b.putClientProperty("JButton.buttonType", "borderless");

            shortcutBar.add(b, "growx,pad 0");
        }
        leftPanel.add(shortcutBar, "growx");
        leftPanel.add(sep,"growx,gaptop 20");
        leftPanel.add(treePane, "growx, growy, gaptop 20");

        JScrollPane leftScrollPanel = new JScrollPane(leftPanel);
        leftScrollPanel.setPreferredSize(new Dimension( 200, 50));

        splitPane = new JSplitPane();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(leftScrollPanel, BorderLayout.WEST);
        centerPanel.add(fileView, BorderLayout.CENTER);
//        splitPane.setLeftComponent(leftScrollPanel);
//        splitPane.setRightComponent(fileView);
//        splitPane.setDividerLocation(0.3);

        files.setLayout(new BorderLayout());
        files.add(centerPanel, BorderLayout.CENTER);
    }


    @Override
    public JPanel getUI() {
        return panel;
    }

    public void setDivider(float location) {
        splitPane.setDividerLocation(location);
    }


    public JTabbedPane getShortcutTabs() {
        return shortcutTabs;
    }

    public JTree getTree() {
        return tree;
    }


    public class FileSystemTreeRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 0;
        private FileSystemView mView = FileSystemView.getFileSystemView();

        public FileSystemTreeRenderer() {
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            try {
                File f = null;
                if (value instanceof DefaultMutableTreeNode){f = (File) ((DefaultMutableTreeNode) value).getUserObject();}
                else {f = new File((String) value);}
                l.setText(f.toString());
                l.setIcon(mView.getSystemIcon(f));
//                System.out.println("Icon Set");
            } catch (Exception e) {
//                System.out.println(value);
            }
            return l;
        }

    }
}