package org.example.view;

import org.example.Icons.*;
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
    public TagView tagsPanel;
    private JButton button;
    private JButton tagMenuButton;
    private JButton starButton;
    private JPopupMenu popupMenu;
    public JMenuItem newTagButton;

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
//        JCheckBoxMenuItem m1 = new JCheckBoxMenuItem("Tag1");
//        JCheckBoxMenuItem m2 = new JCheckBoxMenuItem("Tag2");
//        JCheckBoxMenuItem m3 = new JCheckBoxMenuItem("Tag3");
//        popupMenu.addSeparator();
//        newTagButton = new JMenuItem("New Tag");
//        popupMenu.add(newTagButton);
//
//        popupMenu.add(m1); popupMenu.add(m2); popupMenu.add(m3); popupMenu.addSeparator(); popupMenu.add(newTagButton);

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

        tagMenuButton = new JButton();
        tagMenuButton.setIcon(new TagIcon(18));
        tagMenuButton.setBounds(0,5, tagMenuButton.getPreferredSize().width, tagMenuButton.getPreferredSize().height);
        tagMenuButton.putClientProperty("JButton.buttonType", "borderless");
        tagMenuButton.setFocusable(false);

        tagMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(tagMenuButton, tagMenuButton.getWidth() / 2, tagMenuButton.getHeight() / 2);
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
        buttons.add(tagMenuButton, BorderLayout.WEST);
        buttons.add(starButton, BorderLayout.EAST);

        List<String> tmp = new ArrayList<>();
        tagsPanel = new TagView(tmp);
        topPanel.add(tagsPanel, BorderLayout.SOUTH);

        lp.add(button, 1 );
        lp.add(tagMenuButton, 1);
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
    public JButton getTagMenuButton(){
        return tagMenuButton;
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
