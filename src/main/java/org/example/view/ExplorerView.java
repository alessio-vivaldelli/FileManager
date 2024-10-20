package org.example.view;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.Icons.CircleIcon;
import org.example.Icons.ClosedFolderIcon;
import org.example.Icons.GreaterIcon;
import org.example.Icons.SmallFolderIcon;
import org.example.Layout.MOverlayLayout;
import org.example.MItem;
import org.example.SearchText;
import org.example.TabPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import org.example.Layout.WrapLayout;
import org.example.DatabasesUtil;


public class ExplorerView extends TabPage {

    public static final int ICON_SIZE = 30;
    public static final String SHORTUCT_ITEM_CONSTRAINS = "growx,pad 0,gapbottom 2,gaptop 2";
    public static final String TAG_ITEM_CONSTRAINS = "growx,pad 0,gapbottom 2,gaptop 2";

    private static int count = 1;
    public static final int navigationHigh = 55;

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
    public JScrollPane fileView;

    private JButton navigationButtons;
    private JPanel navigationSearch;
    private JTextField navigationSearchField;
    private JTextField navigationPathTextField;

    private Rectangle draggingRectangle = null;
    private static final Color draggingRectangleColor = new Color(0,0.39f,0.56f, 0.3f);
    public boolean isDraggingItem = false;
    private Point draggingIconPoint;
    private Icon icon;
    public boolean isDraggingDir = false;

    public ExplorerView() {
        super("File Explorer " + count);
        count++;
        initView();
    }

    public void drawRectangle(Rectangle newRectangle){
        draggingRectangle = newRectangle;
        fileView_p.repaint();
    }
    public void stopDawRectangle(){
        draggingRectangle = null;
        fileView_p.repaint();
    }

    public void paintDraggedItem(Point mousePosition, Icon fileIcon, boolean isDir, boolean isMulti){
        if(!isDraggingItem){
            icon = (isMulti) ? new ClosedFolderIcon(Item.getIconSize()) : fileIcon;
            isDraggingDir = isDir;
        }
        isDraggingItem = true;
        draggingIconPoint = mousePosition;
        panel.repaint();

    }
    public void stopPainDraggedItem(){
        isDraggingItem = false;
        panel.repaint();
    }

    public JButton getNavigationButtons() {
        return navigationButtons;
    }

    public JTextField getNavigationSearchField() {
        return navigationSearchField;
    }

    public JPanel getNavigationSearch() {
        return navigationSearch;
    }

    private void initView() {
        panel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if(isDraggingItem) {
                    icon.paintIcon(panel,
                            (Graphics2D) g, draggingIconPoint.x - (icon.getIconWidth()/2),
                            draggingIconPoint.y - (icon.getIconHeight()/2));
                }
            }
        };
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
        center.setLayout(new MigLayout("insets 0, wrap 1",
                "[grow]0", "[]0[grow]0"));


        JPanel path = new JPanel(new MigLayout("insets 0",
                "[grow]10[]0", "[]0"));
        path.setPreferredSize(new Dimension(0, navigationHigh));
        navigationButtons = new JButton(){
            @Override
            protected void paintBorder(Graphics g) {
                super.paintBorder(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255,255,255,20));
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawRoundRect(0,0, this.getWidth()-1, this.getHeight()-1,this.getHeight()-1,this.getHeight()-1);
            }
        };
        navigationButtons.putClientProperty("JButton.buttonType", "roundRect");
        navigationButtons.putClientProperty("FlatLaf.style", "background: darken($Panel.background,1%)"); // hoverBackground: #fff
        navigationButtons.setPreferredSize(new Dimension(0, navigationHigh));
        navigationButtons.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        navigationButtons.setOpaque(false);
        navigationButtons.setBackground(new Color(0,0,0,0));
        navigationButtons.setFocusable(false);



        path.add(navigationButtons, "growx,gapleft 10, gapbottom 10, gaptop 10");
        
        navigationSearch = new JPanel() {
            @Override
            protected void paintBorder(Graphics g) {
                super.paintBorder(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255,255,255,20));
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawRoundRect(0,0, this.getWidth()-1, this.getHeight()-1,this.getHeight()-1,this.getHeight()-1);
            }};
        navigationSearch.setPreferredSize(new Dimension(150, navigationHigh));
        navigationSearch.setBackground(new Color(0,0,0,0));
        navigationSearch.setLayout(new BorderLayout());
        navigationSearch.setOpaque(false);
        navigationSearch.setFocusable(false);

        // https://docs.oracle.com/javase/tutorial/essential/io/find.html
        navigationSearchField = new JTextField(){
            @Override
            protected void paintBorder(Graphics g) {}
        };
        navigationSearchField.setPreferredSize(new Dimension(0,navigationHigh));
        navigationSearchField.setBackground(new Color(0,0,0,0));
        navigationSearchField.setHorizontalAlignment(SwingConstants.LEFT);
        navigationSearchField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        navigationSearchField.putClientProperty("JTextField.placeholderText", "Search");
        navigationSearch.add(navigationSearchField, BorderLayout.CENTER);

        navigationPathTextField = new JTextField(){
            @Override
            protected void paintBorder(Graphics g) {}
        };
        navigationPathTextField.setPreferredSize(new Dimension(0,navigationHigh));
        navigationPathTextField.putClientProperty("FlatLaf.styleClass", "h4");
        navigationPathTextField.setBackground(new Color(0,0,0,0));
        navigationPathTextField.setBorder(new LineBorder(Color.RED));
        navigationPathTextField.setHorizontalAlignment(SwingConstants.LEFT);
        navigationPathTextField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        path.add(navigationSearch, "gapright 10, gapbottom 10, gaptop 10, align right");

        JPanel files = new JPanel();
        files.setBackground(Color.CYAN);

        center.add(path, "growx");
        center.add(files, "grow");


        innerPanel.add(shortcutTabs, BorderLayout.WEST);
        innerPanel.add(center, BorderLayout.CENTER);

        fileView_p = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10)){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if(draggingRectangle != null){
                    g.setColor(ExplorerView.draggingRectangleColor);
                    g.fillRoundRect(draggingRectangle.x, draggingRectangle.y, draggingRectangle.width, draggingRectangle.height, 10, 10);
                }
            }
        };
        fileView_p.setFocusable(true);

        fileView = new JScrollPane(fileView_p);
        InputMap im = fileView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "none");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "none");

        fileView.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        fileView.getVerticalScrollBar().setUnitIncrement(20);
        fileView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileView.setBorder(new EmptyBorder(0, 0, 0, 0));
        fileView.setMinimumSize(new Dimension(400, 50));


        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new File("My Computer"));
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.setFocusable(false);
        tree.setEditable(true);

        JScrollPane treePane = new JScrollPane(tree);

        JPanel leftPanel = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "[]",
                "[][][][]"));

        shortcutListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "",
                "[fill]0")){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if(isDraggingItem && isDraggingDir) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g2.setColor(new Color(1, 1, 1, 0.5f));
                    g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    int width = g2.getFontMetrics(g2.getFont()).stringWidth("Add Shortcut");
                    g2.drawString("Add Shortcut", this.getWidth() / 2 - width / 2, this.getHeight() / 2);
                }
            }
        };


        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        JLabel shortcutTitle = new JLabel("Shortcut");
        shortcutTitle.setHorizontalAlignment(SwingConstants.CENTER);
        shortcutTitle.putClientProperty( "FlatLaf.styleClass", "h2" );
        shortcutListBar.add(shortcutTitle, "growx,pad 0,gapbottom 5,gaptop 10");

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

//        shortcutListBar.add(newShortcut, "growx,pad 0,gapbottom 10,gaptop 15,gapleft 20,gapright 20");

        leftPanel.add(shortcutListBar, "growx,gapleft 0, gapright 0, gaptop 10");

        tagsListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1",
                "", "[fill]0"));
        JLabel tagsTitle = new JLabel("Tags");
        insideTagPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 0,0));
        tagsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tagsTitle.putClientProperty( "FlatLaf.styleClass", "h2" );
        tagsListBar.add(tagsTitle, "growx,pad 0,gapbottom 5,gaptop 10");

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
            colorButton.setIcon(new CircleIcon(DatabasesUtil.generateNewColor()));
        });
        newTagField.putClientProperty( FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, colorButton );
        newTagField.putClientProperty( FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true );
        newTagField.putClientProperty("JTextField.placeholderText", "new tag");
        newTagField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                colorButton.setIcon(new CircleIcon(DatabasesUtil.generateNewColor()));
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
        tagsListBar.add(insideTagPanel, "growx,pad 0");

        leftPanel.add(tagsListBar, "growx,gapleft 0, gapright 0");

        leftPanel.add(sep,"growx,gaptop 20,gapleft 10,gapright 10");

        // uncomment to reintroduce Tree view
//        leftPanel.add(treePane, "growx, growy, gaptop 20");


        disksListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1",
                "", "[fill]0"));
        JLabel disksTitle = new JLabel("Disks");
        disksTitle.setHorizontalAlignment(SwingConstants.CENTER);
        disksTitle.putClientProperty( "FlatLaf.styleClass", "h2" );
        disksListBar.add(disksTitle, "growx,pad 0,gapbottom 5,gaptop 10");

        leftPanel.add(disksListBar, "growx,gapleft 0, gapright 0");

        cloudListBar = new JPanel(new MigLayout("insets 0, fillx, wrap 1",
                "", "[fill]0"));
        JLabel cloudTitle = new JLabel("Cloud");
        cloudTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cloudTitle.putClientProperty( "FlatLaf.styleClass", "h2" );
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

        files.setLayout(new BorderLayout());
        files.add(centerPanel, BorderLayout.CENTER);

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
        tmp.setIcon(new SmallFolderIcon(16));
        tmp.setHorizontalAlignment(JButton.LEFT);
        tmp.setFocusable(false);
        tmp.putClientProperty("JButton.buttonType", "borderless");
//        tmp.putClientProperty("FlatLaf.styleClass", "large");
        shortcutListBar.add(tmp, ExplorerView.SHORTUCT_ITEM_CONSTRAINS,1);
        shortcutListBar.revalidate();
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
        tmp.setIcon(new SmallFolderIcon(16));
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
        tmp.setIcon(new SmallFolderIcon(16));
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

    public JTextField getNavigationPathTextField() {
        return navigationPathTextField;
    }

    public JButton addPathButton(String pathElem, File folder){

        JButton tmp = new JButton(pathElem){
            @Override
            public String toString() {
                return folder.getPath();
            }
        };
        navigationButtons.add(tmp, 0);
        tmp.putClientProperty("FlatLaf.style", "background: #00000000; hoverBackground: darken($Panel.background,1%)"); // hoverBackground: #fff
        tmp.putClientProperty("FlatLaf.styleClass", "h4");
        tmp.setFocusable(false);
        navigationButtons.revalidate();
        navigationButtons.repaint();
        if(navigationButtons.getComponentCount() > 1){
            JButton icon = new JButton();
            icon.setEnabled(false);
            icon.putClientProperty("JButton.buttonType", "borderless");
            icon.setIcon(new GreaterIcon(16));
            navigationButtons.add(icon, 1);
            totalWidth += (int) icon.getPreferredSize().getWidth();
        }
        totalWidth += (int) tmp.getPreferredSize().getWidth();
        return tmp;
    }

    private int totalWidth = 0;

    public void checkNavigationDimension(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(navigationButtons);
        if(totalWidth >= topFrame.getWidth()*0.85f){
            while (totalWidth >= topFrame.getWidth()*0.85f){
                int tmpWidth = (int) navigationButtons.getComponent(0).getPreferredSize().getWidth();
                navigationButtons.remove(0);
                navigationButtons.revalidate();
                navigationButtons.repaint();
                totalWidth -= tmpWidth;
            }
            ((JButton) navigationButtons.getComponent(0)).setText("...");
        }else if(navigationButtons.getComponentCount() > 0){
            JButton tmp = ((JButton) navigationButtons.getComponent(0));
            if(tmp.getText().equals("...")){
                String txt = ((new File(tmp.toString())).getName().isEmpty()) ? (new File(tmp.toString())).getPath() :
                        (new File(tmp.toString())).getName();
                tmp.setText(txt);
            }
        }
    }

    public void clearPathButtons(){
        navigationButtons.removeAll();
        navigationButtons.repaint();
        navigationButtons.revalidate();
        totalWidth = 0;
    }


}