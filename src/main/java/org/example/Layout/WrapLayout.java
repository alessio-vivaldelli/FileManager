package org.example.Layout;

import org.example.view.Item;

import java.awt.*;

public class WrapLayout extends FlowLayout {
    private int initialHGap;
    private int initialVGap;

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
        initialHGap = hgap;
        initialVGap = vgap;
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {

        Container thisContainer = target.getParent();
        if(target.getComponentCount() <= 0){return new Dimension(50, 0);}
        int thisWidth = thisContainer.getSize().width;

        int targetPrefSize = Item.getItemOuterPreferredSize().width;
        double maxElem = ((double) thisWidth / targetPrefSize);
        int fillGap = (int) ((thisWidth - (targetPrefSize * Math.floor(maxElem))) / maxElem); // (spazio che avanza) /

        this.setHgap(fillGap);

        Dimension itemSize = Item.getItemOuterPreferredSize();
        int componentCount = target.getComponentCount();

        return new Dimension(50, ((int) Math.ceil(componentCount / Math.floor(maxElem))) * (itemSize.height+getVgap()));
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return new Dimension(0,20);
    }
}
