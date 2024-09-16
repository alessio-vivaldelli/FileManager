package org.example.view;

import org.example.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TagView extends JPanel {
    private List<String> tagsColor;
    private int circleSize = 16;
    private int overlap = 7;

    public TagView(List<String> tags){
        super();
        setPreferredSize(new Dimension(200, (int) (circleSize*1.5f)));
        setTags(tags);
        initUI();
    }

    public TagView(){
        super();
        setPreferredSize(new Dimension(200, (int) (circleSize*1.5f)));
        setTags(new ArrayList<>());
        initUI();
    }

    public void setTags(List<String> tags){
        tagsColor = new ArrayList<>();
        tags.forEach((tag) -> {
            tagsColor.add(Util.getColorFromTag(tag));
        });
    }
    public void addTag(String tag){
        tagsColor.add(Util.getColorFromTag(tag));
    }

    public void removeTag(String tag){
        tagsColor.remove(Util.getColorFromTag(tag));
    }

    private void initUI(){
        setFocusable(false);
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int len = tagsColor.size();
        int x = ((this.getSize().width/2 - ((len*(circleSize-overlap)))/2));
        int y = this.getSize().height/2 - (circleSize/2);

        for (int i = 0; i < tagsColor.size(); i++) {
            g.setColor(Color.decode(tagsColor.get(i)));
            g.fillOval(x, y, circleSize, circleSize);
            x += circleSize-overlap;
        }
    }
}
