package org.example;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.example.controller.MyTabbedPaneController;
import org.example.model.MyTabbedPaneModel;

import javax.swing.*;
import java.awt.*;

import org.example.view.MyTabbedPaneView;

// TODO:
// Typography: https://www.formdev.com/flatlaf/typography/


// FlatLaf customizations like rounded buttons
// https://www.formdev.com/flatlaf/customizing/

// Inspiration:
// https://dribbble.com/shots/14961092-Dropbox-Monochrome-Recent-Files

// Best practices:
// https://softwareengineering.stackexchange.com/questions/39677/what-are-the-best-practices-of-java-swing-development

// GridBag is powerful but tends to be hardcoded with magic numbers
// https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html

// GroupLayout is powerful but complex to handle
// https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html

// Library to have a new layout manager
// http://miglayout.com/


// Codice preso da:
// https://stackoverflow.com/questions/55768162/observer-pattern-on-mvc-for-specific-fields
// https://thephantomprogrammer.blogspot.com/2015/08/compound-patterns.html

public class Main {

    public static Dimension screenDimension;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
//            UIManager.setLookAndFeel(new FlatMacLightLaf());
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

        Util.initDatabaseData();

        JFrame frame = new JFrame("File Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout());

        MyTabbedPaneView myTabbedPaneView = new MyTabbedPaneView();
        MyTabbedPaneModel myTabbedPaneModel = new MyTabbedPaneModel();
        new MyTabbedPaneController(myTabbedPaneView, myTabbedPaneModel);

        frame.add(myTabbedPaneView.getUI(), BorderLayout.CENTER);

        myTabbedPaneModel.addTab(TabPage.EXPLORER);

        frame.setVisible(true);
    }
}