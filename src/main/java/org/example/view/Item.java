package org.example.view;

import com.formdev.flatlaf.icons.FlatFileChooserHomeFolderIcon;
import org.example.Icons.ClosedFolderIcon;
import org.example.Icons.FileIcon;
import org.example.Icons.FlatDotsIcon;
import org.example.Icons.StarIcon;
import org.example.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Item extends JPanel {
    public File file;
    private FileSystemView mView = FileSystemView.getFileSystemView();
    private static final Map<String, Icon> iconCache = new HashMap<>();
//    private List<TagView> lists = new ArrayList<>();
    private JPanel tagsPanel;
    private JButton button;
    private JButton menuButton;
    private JButton starButton;
    private JPopupMenu popupMenu;

    public Item(File f) {
        super();
        this.setLayout(new BorderLayout());
        this.putClientProperty("FlatLaf.style", "arc: 16; background: darken($Panel.background,1%)");
        setPreferredSize(Item.getItemOuterPreferredSize());

        this.file = f;
        initUI();
    }

    protected void initUI() {
        JLayeredPane lp = new JLayeredPane();
        lp.setBounds(0, 0, Item.getItemOuterPreferredSize().width, Item.getItemOuterPreferredSize().height);

        popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Delete");
        JMenuItem menuItem2 = new JMenuItem("Rename");
        popupMenu.add(menuItem1); popupMenu.add(menuItem2);

        JLabel label = new JLabel();
        final int labelIconSize = Item.getIconSize();

        label.setBounds((Item.getItemOuterPreferredSize().width -( (int)(Item.getItemPreferredSize().width*0.7)))/2, (Item.getItemOuterPreferredSize().height - Item.getItemPreferredSize().height)/2, Item.getItemPreferredSize().width, Item.getItemPreferredSize().height);

        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);


        int i = file.getName().lastIndexOf('.');
        String extension = file.getName().substring(i+1);
        if (i > 0) {
            label.setText(file.getName().substring(0, i));
            if(extension.equals("jpg") || extension.equals("png")){
                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        try {
                            BufferedImage img = ImageIO.read(file);
                            double ratio = (double) img.getWidth() / img.getHeight();
                            return new ImageIcon(img.getScaledInstance(labelIconSize, (int)(labelIconSize / ratio), Image.SCALE_SMOOTH));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    @Override
                    protected void done() {
                        try {
                            label.setIcon(get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
            else {
                label.setIcon(new FileIcon(labelIconSize, extension));
            }
        }else {
//            label.setIcon(getCachedIcon(file, labelIconSize));
            label.setIcon(new ClosedFolderIcon(labelIconSize));
            label.setText(file.getName());
        }





        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);


        button = new JButton();
        button.putClientProperty("JButton.buttonType", "borderless");
        button.setBounds(0, 0, Item.getItemOuterPreferredSize().width, Item.getItemOuterPreferredSize().height);;

        menuButton = new JButton();
        menuButton.setIcon(new FlatDotsIcon());
        menuButton.setBounds(0,5,menuButton.getPreferredSize().width, menuButton.getPreferredSize().height);
        menuButton.putClientProperty("JButton.buttonType", "borderless");
        menuButton.setFocusable(false);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(menuButton, menuButton.getWidth() / 2, menuButton.getHeight() / 2);
            }
        });

        starButton = new JButton();
        starButton.setIcon(new StarIcon());
        starButton.setBounds(Item.getItemOuterPreferredSize().width - (starButton.getPreferredSize().width + 5),5, starButton.getPreferredSize().width, starButton.getPreferredSize().height);
        starButton.putClientProperty("JButton.buttonType", "borderless");
        starButton.setFocusable(false);

        lp.add(button, 1 );
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBounds((Item.getItemOuterPreferredSize().width - Item.getItemPreferredSize().width) / 2, 0, Item.getItemPreferredSize().width, Item.getItemPreferredSize().height);
        lp.add(topPanel, 2);

        topPanel.add(label, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);
        buttons.add(menuButton, BorderLayout.WEST);
        buttons.add(starButton, BorderLayout.EAST);

        tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        tagsPanel.setBorder(new EmptyBorder(5,5,5,5));
        tagsPanel.setOpaque(false);

        topPanel.add(tagsPanel, BorderLayout.SOUTH);
        tagsPanel.setPreferredSize(new Dimension(Item.getItemPreferredSize().width, (int)(Item.getItemPreferredSize().height * 0.25)));
//        for (int j = 0; j < 3; j++) {
////            TagView tmp = new TagView("Architettura");
//////            lists.add(tmp);
////            tagsPanel.add(tmp);
//            addNewTag(new TagView("Architettura"));
//        }

        lp.add(button, 1 );
        lp.add(menuButton, 1);
        lp.add(starButton, 1);

        this.add(lp, BorderLayout.CENTER);
    }

    public static Dimension getItemPreferredSize(){
        int width  = (int) (Main.screenDimension.width * 0.08);
        int height = (int) (width*1.7);
        return new Dimension(width, height);
    }

    public static Dimension getItemOuterPreferredSize(){
        int width = Item.getItemPreferredSize().width;
        int height = Item.getItemPreferredSize().height;
        return new Dimension((int) (width * 1.2), (int) (height * 1.0));
    }

    public static int getIconSize(){
        return (int)(Item.getItemPreferredSize().width*0.7);
    }


    public void addNewTag(TagView tag){
        tagsPanel.add(tag);

        int rowP = 1;
        int pixel = 0;
        int hiddenElements = 0;
        int tagsHigh = ((TagView) tagsPanel.getComponent(0)).getTextDimension().height;
        int panelHeight = tagsPanel.getPreferredSize().height;
        int panelWidth = tagsPanel.getPreferredSize().width;

        TagView lastElem = ((TagView) tagsPanel.getComponent(0));
        for (Component comp : tagsPanel.getComponents()){
            TagView a = (TagView) comp;
            pixel += a.getTextDimension().width;
            if (pixel >= panelWidth) {
                rowP += 1;
                pixel = a.getTextDimension().width;
            }
            if(rowP*tagsHigh >= panelHeight){
                a.setVisible(false);
                if(hiddenElements == 0){
                    lastElem.setIsMore(true);
                }hiddenElements += 1;
            }a.setIsMore(false);
            lastElem = a;
        }
    }


    private Icon getCachedIcon(File file, int iconSize) {
        String extension = getFileExtension(file);
        if (iconCache.containsKey(extension)) {
            return iconCache.get(extension);
        } else if (extension.isEmpty()) {
            Icon icon = mView.getSystemIcon(file, iconSize, iconSize);
            iconCache.put(file.getName(), icon);
            return icon;
        } else {
            Icon icon = mView.getSystemIcon(file, iconSize, iconSize);
            iconCache.put(extension, icon);
            return icon;
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex == -1) ? "" : name.substring(lastIndex + 1);
    }

    public JButton getItemButton(){
        return button;
    }
    public JButton getMenuButton(){
        return menuButton;
    }
    public JButton getStarButton(){
        return starButton;
    }
    public JPopupMenu getPopupMenu(){
        return popupMenu;
    }

    public static boolean isDirectoryFast(File file){
        int ind = file.getPath().lastIndexOf('.');
        if(ind == -1){return true;}
        return false;
    }
}
