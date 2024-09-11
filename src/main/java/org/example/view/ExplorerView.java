package org.example.view;

import net.miginfocom.swing.MigLayout;
import org.example.Layout.MOverlayLayout;
import org.example.MItem;
import org.example.TabPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;

import org.example.Layout.WrapLayout;


public class ExplorerView extends TabPage {

    public static final int ICON_SIZE = 30;

    private static int count = 1;

    JSplitPane splitPane;
    JTabbedPane shortcutTabs;
    JPanel panel;
    public JTree tree;
    public JPanel fileView_p;
    public JPanel innerPanel;

    public ExplorerView() {
        super("File Explorer " + count);
        count++;
        initView();
    }

    private void initView() {

        panel = new JPanel();
        panel.setFocusable(true);
        panel.setLayout(new MOverlayLayout(panel));

        innerPanel = new JPanel();
        innerPanel.setFocusable(true);
        innerPanel.setLayout(new BorderLayout());


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


        innerPanel.add(shortcutTabs, BorderLayout.WEST);
        innerPanel.add(center, BorderLayout.CENTER);

        fileView_p = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));

        fileView_p.add(new MItem(new File("C:\\Users\\aless\\Documents")));
        fileView_p.add(new MItem(new File("C:\\Users\\aless\\Pictures")));
//        fileView_p.add(new Item(new File("C:\\Users\\aless\\Music")));
//        fileView_p.add(new Item(new File("C:\\Users\\aless\\Downloads\\Telegram Desktop\\photo_2024-08-22_14-29-16.jpg")));
//        fileView_p.add(new Item(new File("C:\\Users\\aless\\Downloads\\Iscrizione.pdf")));



        JScrollPane fileView = new JScrollPane(fileView_p);
        fileView.getVerticalScrollBar().setUnitIncrement(20);
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
            b.setIcon((Icon) UIManager.get("FileView.directoryIcon"));
            b.setHorizontalAlignment(JButton.LEFT);
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

        JButton searchText = new JButton("TESTTESTS");
        searchText.setAlignmentX(Component.RIGHT_ALIGNMENT); searchText.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); innerPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(searchText);
        panel.add(innerPanel);
    }
    public void addItem(MItem e){
        fileView_p.add(e);
        fileView_p.revalidate();
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

}