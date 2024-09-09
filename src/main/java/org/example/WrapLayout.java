package org.example;

import java.awt.*;

public class WrapLayout extends FlowLayout {
    private int initialHGap;
    private int initialVGap;
    final private static int preferredItemsSize = (int)(Main.screenDimension.width * 0.08);

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
        initialHGap = hgap;
        initialVGap = vgap;

    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
//        System.out.println(target.getComponentCount());
        Container thisContainer = target.getParent();
        int wid = thisContainer.getSize().width;

        int targetPrefSize = target.getComponent(0).getPreferredSize().width;
        double hGap = ((double) thisContainer.getSize().width / targetPrefSize);

        int fillGap = (int) ((thisContainer.getSize().width - (targetPrefSize * Math.floor(hGap))) / hGap);

        this.setHgap(fillGap);

        return new Dimension(50, 500);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return new Dimension(0,20);
    }

}
