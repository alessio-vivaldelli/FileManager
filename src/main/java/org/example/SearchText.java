package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchText extends JLabel {
    private Timer timer;
    private boolean itemExist = true;

    public SearchText(){
        super();
        putClientProperty("FlatLaf.style", "arc: 999; border: 2,10,2,10,#135b76");
        putClientProperty( "FlatLaf.styleClass", "h4" );
        setVisible(false);
        initTimer();
    }

    public void exist(boolean e){
        itemExist = e;
    }

    public void addChar(char charCode){
        this.setText(getText() + charCode);
        if(!itemExist){
            putClientProperty("FlatLaf.style", "arc: 999; border: 2,10,2,10,#F5353C");
            return;
        }
        putClientProperty("FlatLaf.style", "arc: 999; border: 2,10,2,10,#135b76");
    }

    public void reset(){
        this.setText("");
    }

    @Override
    public void setText(String text) {
        super.setText(text);

        if(text.isEmpty()){return;}
        resetAndStartTimer();
    }

    private void initTimer() {
        timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemExist = true;
                setVisible(false);
                setText("");
            }
        });
        timer.setRepeats(false);
    }
    public void resetAndStartTimer() {
        setVisible(true);
        timer.restart();
    }
}
