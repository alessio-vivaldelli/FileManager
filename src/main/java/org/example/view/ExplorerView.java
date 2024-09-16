package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.Icons.CircleIcon;
import org.example.Layout.MOverlayLayout;
import org.example.MItem;
import org.example.SearchText;
import org.example.TabPage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import org.example.Layout.WrapLayout;
import org.example.Util;


public class ExplorerView extends TabPage {

    public static final int ICON_SIZE = 30;
    public static final String SHORTUCT_ITEM_CONSTRAINS = "growx,pad 0,gapbottom 2,gaptop 2";
    public static final String TAG_ITEM_CONSTRAINS = "growx,pad 0,gapbottom 2,gaptop 2";

    private static int count = 1;

    JSplitPane splitPane;
    JTabbedPane shortcutTabs;
    JPanel panel;
    public JTree tree;
    public JPanel fileView_p;
    public JPanel innerPanel;
    public SearchText searchText;
    public JButton newTagButton;
    public JButton newShortcut;
    public JPanel tagsListBar;
    public JPanel shortcutListBar;
    public JPanel disksListBar;
    public JPanel cloudListBar;
    public JPanel insideTagPanel;
    public JTextField newTagField;
    public JButton colorButton;

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
        fileView_p.setFocusable(true);


        JScrollPane fileView = new JScrollPane(fileView_p);
        fileView.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
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

        JPanel leftPanel = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "[]", "[][][][]"));

        shortcutListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "", "[fill]0"));
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        JLabel shortcutTitle = new JLabel("Shortcut");
        shortcutTitle.setHorizontalAlignment(SwingConstants.CENTER);
        shortcutTitle.putClientProperty( "FlatLaf.styleClass", "h3" );
        shortcutListBar.add(shortcutTitle, "growx,pad 0,gapbottom 5,gaptop 10");
//        for (int i = 0; i < 5; i++) {
//            JButton b = new JButton("Test");
//            b.setIcon((Icon) UIManager.get("FileView.directoryIcon"));
//            b.setHorizontalAlignment(JButton.LEFT);
//            b.setFocusable(false);
//            b.putClientProperty("JButton.buttonType", "borderless");
//            shortcutListBar.add(b, "growx,pad 0");
//        }
        newShortcut = new JButton("New Shortcut"){
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D g2d = (Graphics2D) g;
                float[] dashingPattern1 = {4.5f, 4.5f};
                Stroke stroke2 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);

                g2d.setStroke(stroke2);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawRoundRect(0,0, (int) (this.getWidth()*0.98f), (int) (this.getHeight()*0.98f), 20,20);
            }
        };
        newShortcut.setFocusable(false);
        newShortcut.putClientProperty("FlatLaf.styleClass", "medium");
        newShortcut.putClientProperty("JButton.buttonType", "borderless");

        shortcutListBar.add(newShortcut, "growx,pad 0,gapbottom 10,gaptop 15,gapleft 20,gapright 20");
//        shortcutListBar.add(sep, "growx,gaptop 50,gapleft 10,gapright 10");

        leftPanel.add(shortcutListBar, "growx,gapleft 0, gapright 0");


        tagsListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "", "[fill]0"));
        JLabel tagsTitle = new JLabel("Tags");
        insideTagPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 0,0));
        tagsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tagsTitle.putClientProperty( "FlatLaf.styleClass", "h3" );
        tagsListBar.add(tagsTitle, "growx,pad 0,gapbottom 5,gaptop 10");
        tagsListBar.add(insideTagPanel, "growx,pad 0");


        newTagButton = new JButton("New Tag"){
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D g2d = (Graphics2D) g;
                float[] dashingPattern1 = {4.5f, 4.5f};
                Stroke stroke2 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);

                g2d.setStroke(stroke2);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawRoundRect(0,0, (int) (this.getWidth()*0.98f), (int) (this.getHeight()*0.98f), 20,20);
            }
        };
        newTagButton.setFocusable(false);
        newTagButton.putClientProperty("FlatLaf.styleClass", "medium");
        newTagButton.putClientProperty("JButton.buttonType", "borderless");

        newTagField = new JTextField(){
            @Override
            protected void paintBorder(Graphics g) {
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D g2d = (Graphics2D) g;
                float[] dashingPattern1 = {4.5f, 4.5f};
                Stroke stroke2 = new BasicStroke(1f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);

                g2d.setStroke(stroke2);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawRoundRect(0,0, (int) (this.getWidth()*0.98f), (int) (this.getHeight()*0.98f), 20,20);
            }
        };
        newTagField.setHorizontalAlignment(SwingConstants.CENTER);
        colorButton = new JButton();
        colorButton.setIcon(null);
        colorButton.setOpaque(false);
        colorButton.setPreferredSize(new Dimension(16,16));
        colorButton.addActionListener(e -> {
            colorButton.setIcon(new CircleIcon(Util.generateNewColor()));
        });
        newTagField.putClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, colorButton );
        newTagField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true );
        newTagField.putClientProperty("JTextField.placeholderText", "new tag");

        newTagField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                colorButton.setIcon(new CircleIcon(Util.generateNewColor()));
                colorButton.setVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(newTagField.getText().isEmpty()) {
                    colorButton.setIcon(null);
                }
            }
        });

        tagsListBar.add(newTagField, "growx,pad 0,gapbottom 2,gaptop 15,gapleft 20,gapright 20");

//        tagsListBar.add(newTagButton, "growx,pad 0,gapbottom 2,gaptop 15,gapleft 20,gapright 20");

        leftPanel.add(tagsListBar, "growx,gapleft 0, gapright 0");

        leftPanel.add(sep,"growx,gaptop 20,gapleft 10,gapright 10");

        //TODO: removed treePane, on leftPane row constrain was [][][grow]
//        leftPanel.add(treePane, "growx, growy, gaptop 20");


        disksListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "", "[fill]0"));
        JLabel disksTitle = new JLabel("Disks");
        disksTitle.setHorizontalAlignment(SwingConstants.CENTER);
        disksTitle.putClientProperty( "FlatLaf.styleClass", "h3" );
        disksListBar.add(disksTitle, "growx,pad 0,gapbottom 5,gaptop 10");

        leftPanel.add(disksListBar, "growx,gapleft 0, gapright 0");

        cloudListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "", "[fill]0"));
        JLabel cloudTitle = new JLabel("Cloud");
        cloudTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cloudTitle.putClientProperty( "FlatLaf.styleClass", "h3" );
        cloudListBar.add(cloudTitle, "growx,pad 0,gapbottom 5,gaptop 10");

        leftPanel.add(cloudListBar, "growx,gapleft 0, gapright 0");


        JScrollPane leftScrollPanel = new JScrollPane(leftPanel);
        leftScrollPanel.getVerticalScrollBar().setUnitIncrement(10);
        leftScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
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

//        JButton searchText = new JButton(""){
//
//            @Override
//            public void setText(String text) {
//                super.setText(text);
//
//            }
//        };


        searchText = new SearchText();

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

    public JButton addShortcutItem(File file){
        JButton tmp = new JButton(file.getName()){
            @Override
            public String toString() {
                return file.getPath();
            }
        };
        tmp.setIcon((Icon) UIManager.get("FileView.directoryIcon"));
        tmp.setHorizontalAlignment(JButton.LEFT);
        tmp.setFocusable(false);
        tmp.putClientProperty("JButton.buttonType", "borderless");
        shortcutListBar.add(tmp, ExplorerView.SHORTUCT_ITEM_CONSTRAINS,1);
        return tmp;
    }

    public void removeTagElement(String tagName){
        int count = insideTagPanel.getComponentCount();
        for (int i = 0; i < count; i++) {
            if (((JToggleButton) insideTagPanel.getComponent(i)).getText().equals(tagName)){
                insideTagPanel.remove(i);
                insideTagPanel.revalidate();
                break;
            }
        }
    }

    public JToggleButton addTagItem(String name, String color){
        JToggleButton b = new JToggleButton(name){
            @Override
            public Dimension getPreferredSize() {

                return super.getPreferredSize();
            }
        };

        b.setIcon(new CircleIcon(color));
        b.setHorizontalAlignment(JButton.LEFT);
        b.setFocusable(false);
        b.putClientProperty("JButton.buttonType", "borderless");
        insideTagPanel.add(b, 0);
        return b;
    }

    public JButton addDiskItem(File file){
        JButton tmp = new JButton(file.getPath()){
            @Override
            public String toString() {
                return file.getPath();
            }
        };
        tmp.setIcon((Icon) UIManager.get("FileView.directoryIcon"));
        tmp.setHorizontalAlignment(JButton.LEFT);
        tmp.setFocusable(false);
        tmp.putClientProperty("JButton.buttonType", "borderless");
        disksListBar.add(tmp, ExplorerView.SHORTUCT_ITEM_CONSTRAINS,1);
        return tmp;
    }

    public JButton addCloudItem(File file){
        JButton tmp = new JButton(file.getName()){
            @Override
            public String toString() {
                return file.getPath();
            }
        };
        tmp.setIcon((Icon) UIManager.get("FileView.directoryIcon"));
        tmp.setHorizontalAlignment(JButton.LEFT);
        tmp.setFocusable(false);
        tmp.putClientProperty("JButton.buttonType", "borderless");
        cloudListBar.add(tmp, ExplorerView.SHORTUCT_ITEM_CONSTRAINS,1);
        return tmp;
    }

    public JTextField getNewTagText(){
        return newTagField;
    }
    public String getNewTagColor(){
        return ((CircleIcon) colorButton.getIcon()).getColor();
    }
}