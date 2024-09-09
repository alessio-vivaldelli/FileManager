package org.example;

import javax.swing.*;

public class TabPage {

    final public static String EXPLORER = "File Explorer";
    final public static String OTHER = "null";


    private String Name;

    public TabPage(String name){
        this.Name = name;
    }

    public JPanel getUI() {
        return null;
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
