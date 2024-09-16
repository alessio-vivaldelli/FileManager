package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

import javax.swing.*;
import java.awt.*;

public class CircleIcon extends FlatAbstractIcon {

    private int size = 16;
    private String color;

    public CircleIcon(String color, int size){
        super( size, size, Color.decode(color) );
        this.color = color;
        this.size = size;
    }
    public CircleIcon(String color){
        super( 16, 16, Color.decode(color) );
        this.color = color;
    }
    @Override
    protected void paintIcon(Component c, Graphics2D g) {
        g.fillOval(0,0, size, size);
    }

    public String getColor(){
        return color;
    }
}
