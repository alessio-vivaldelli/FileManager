package org.example;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import net.miginfocom.swing.MigLayout;
import org.example.Icons.FlatDotsIcon;
import org.example.Icons.StarIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Item extends JPanel {
    public File file;
    private FileSystemView mView = FileSystemView.getFileSystemView();

    public Item(File f) {
        super();
        this.setLayout(new BorderLayout());
        this.putClientProperty("FlatLaf.style", "arc: 16; background: darken($Panel.background,1%)");
        setPreferredSize(Item.getItemOuterPreferredSize());

        this.file = f;
        initUI();
    }

    private void initUI() {
        JLayeredPane lp = new JLayeredPane();
//        lp.setLayout(new BorderLayout());
        lp.setBounds(0, 0, Item.getItemOuterPreferredSize().width, Item.getItemOuterPreferredSize().height);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("Delete");
        JMenuItem menuItem2 = new JMenuItem("Rename");
        popupMenu.add(menuItem1); popupMenu.add(menuItem2);

        JLabel label = new JLabel();
        label.setBounds((Item.getItemOuterPreferredSize().width - Item.getItemPreferredSize().width)/2, 0, Item.getItemPreferredSize().width, Item.getItemPreferredSize().height);

        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            String extension = file.getPath().substring(i+1);
            if(extension.equals("jpg") || extension.equals("png")){
                try {
                    System.out.println("found image");
                    BufferedImage img = ImageIO.read(file);
//                    getScaledInstance(Item.getItemPreferredSize().width, Item.getItemPreferredSize().width, Image.SCALE_SMOOTH)
                    double ratio = (double) ( (double) img.getWidth() /  (double) img.getHeight());
                    label.setIcon(new  ImageIcon(img.getScaledInstance(Item.getItemPreferredSize().width, (int)(Item.getItemPreferredSize().width/ratio), Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                }
            }
            else {
                label.setIcon(mView.getSystemIcon(file, Item.getItemPreferredSize().width, Item.getItemPreferredSize().width));
            }
        }else {
            label.setIcon(mView.getSystemIcon(file, Item.getItemPreferredSize().width, Item.getItemPreferredSize().width));
        }

        label.setText(file.getName());
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);


        JButton button = new JButton();
        button.putClientProperty("JButton.buttonType", "borderless");
        button.setBounds(0, 0, Item.getItemOuterPreferredSize().width, Item.getItemOuterPreferredSize().height);;

        JButton menuButton = new JButton();
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

        JButton starButton = new JButton();
        starButton.setIcon(new StarIcon());
        starButton.setBounds(Item.getItemOuterPreferredSize().width - (starButton.getPreferredSize().width + 5),5, starButton.getPreferredSize().width, starButton.getPreferredSize().height);
        starButton.putClientProperty("JButton.buttonType", "borderless");
        starButton.setFocusable(false);

        lp.add(label, 2);
        lp.add(button, 2);
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
}
