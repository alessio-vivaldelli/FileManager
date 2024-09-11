package org.example.view;

import javax.swing.*;
import java.awt.*;

public class TagView extends JButton {
    private String tag = "Default";

    public TagView(String tag, Color color){
        super();
        this.tag = tag;
        initUI();
//        putClientProperty( "FlatLaf.style", "small");
    }

    public TagView(String tag){
        super();
        this.tag = tag;
        initUI();
    }

    private void initUI(){
        setText(tag);
        setMargin(new Insets(0, 0, 0, 0));
        this.putClientProperty("JButton.buttonType", "roundRect");
        setFocusable(false);
    }

    @Override
    public void setText(String arg0) {
        super.setText(arg0);
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth( getText() );
        int height = metrics.getHeight();
        Dimension newDimension =  new Dimension((int) (width*1.5), (int) (height*1.5));
        setPreferredSize(newDimension);
        setBounds(new Rectangle(
                getLocation(), getPreferredSize()));
    }

    public Dimension getTextDimension(){
        FontMetrics metrics = getFontMetrics(getFont());
        int width = metrics.stringWidth( getText() );
        int height = metrics.getHeight();
        Dimension newDimension =  new Dimension((int) (width*1.5), (int) (height*1.5));
        return newDimension;
    }
    public void setIsMore(boolean more){
       if(more){
           setText("......");
       }else {
           setText(tag);
       }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
