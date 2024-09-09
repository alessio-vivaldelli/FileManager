package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ShortcutItem extends JComponent {
    public String path;
    public String name;
    public String iconPath;

    public ShortcutItem(String path, String iconPath){
        this.path = path;
        this.iconPath = iconPath;
        File file = new File(path);
        this.name = file.getName();
        if(this.name.isEmpty()){
            this.name = path.replace("\\", "").replace(":", "").replace("/", "");
        }

        setPreferredSize(new Dimension(0,0));
    }

    @Override
    public String toString() {
        return this.path;
    }
}
