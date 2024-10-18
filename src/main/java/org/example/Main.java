package org.example;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.example.controller.MyTabbedPaneController;
import org.example.model.MyTabbedPaneModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.example.view.MyTabbedPaneView;

// TODO:
// Typography: https://www.formdev.com/flatlaf/typography/


// FlatLaf customizations like rounded buttons
// https://www.formdev.com/flatlaf/customizing/

// Inspiration:
// https://dribbble.com/shots/14961092-Dropbox-Monochrome-Recent-Files

// Best practices:
// https://softwareengineering.stackexchange.com/questions/39677/what-are-the-best-practices-of-java-swing-development

public class Main {

    public static Dimension screenDimension;

    public static void main(String[] args) {
        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


        screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        // Sey Style: https://www.formdev.com/flatlaf/components/tree/
        UIManager.put("Tree.showDefaultIcons", true);
        UIManager.put("Tree.leafIcon", (Icon) UIManager.get("FileView.directoryIcon"));
        UIManager.put("Tree.paintLines", true);
        UIManager.put("Tree.scrollsOnExpand", true);
        UIManager.put("ScrollPane.border", false);
        UIManager.put("Tree.background", Color.TRANSLUCENT);
        UIManager.put( "TextComponent.arc", 30 );

        DatabasesUtil.initDatabaseData();

//        try {
//            FileSystemUtil.copyFolder(new File("C:\\Users\\aless\\Downloads\\test\\souce"), new File("C:\\Users\\aless\\Downloads\\test\\c"));
//        } catch (IOException e) {
//            System.out.println("Error on copy");
//        }
//        ArrayList<File> files = new ArrayList<>();
//        files.add(new File("C:\\Users\\aless\\Downloads\\test\\souce")); files.add(new File("C:\\Users\\aless\\Downloads\\test\\itemTest.txt"));
//        FileSystemUtil.copyItems(files, new File("C:\\Users\\aless\\Downloads\\test\\c"));

        JFrame frame = new JFrame("File Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout());

        MyTabbedPaneView myTabbedPaneView = new MyTabbedPaneView();
        MyTabbedPaneModel myTabbedPaneModel = new MyTabbedPaneModel();
        new MyTabbedPaneController(myTabbedPaneView, myTabbedPaneModel);

        frame.add(myTabbedPaneView.getUI(), BorderLayout.CENTER);

        myTabbedPaneModel.addTab(TabPage.EXPLORER);

        URL url = Main.class.getClassLoader().getResource("icona_2.png");
        if (url != null) {
            Toolkit kit = Toolkit.getDefaultToolkit();
            Image img = kit.createImage(url);
            frame.setIconImage(img);
        }
        frame.setVisible(true);
    }
}