package org.example.view;

import org.example.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TagView extends JButton {
    private String tag = "Default";

    public TagView(String tag, Color color){
        super();
        putClientProperty( "FlatLaf.style", "small");
        putClientProperty("FlatLaf.style", "roundRect");
        setText(tag);
        setFocusable(false);

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.tag = tag;

    }

}
