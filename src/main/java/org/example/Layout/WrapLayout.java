package org.example.Layout;

import org.example.MItem;
import org.example.view.Item;

import java.awt.*;

public class WrapLayout extends FlowLayout {
    private int initialHGap;
    private int initialVGap;

    private int compColons;
    private int compRows;
    private Dimension maxDimensions = null;

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
        initialHGap = hgap;
        initialVGap = vgap;
        maxDimensions = new Dimension(-1,-1);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {

        Container thisContainer = target.getParent();
        Container TMPthisContainer = target.getParent();
        if(target.getComponentCount() <= 0){return new Dimension(50, 0);}
        int thisWidth = thisContainer.getSize().width;
        while (thisWidth <= 0){
            thisWidth = TMPthisContainer.getSize().width;
            TMPthisContainer = TMPthisContainer.getParent();
        }

        Dimension dim = target.getComponent(0).getPreferredSize();
        compColons = 0;
        compRows = 0;
        if(!(target.getComponent(0).getClass().equals(MItem.class))){
            this.setHgap(initialHGap);
            int rowDim = 0;
            int maxRowDim = 0;
            compRows++;
            int dbg = 0;
            for(Component c : target.getComponents()){
                if (dbg==0){System.out.println(thisWidth);}
                dbg++;
                if((rowDim + c.getPreferredSize().width + getHgap()) >= thisWidth){
                    rowDim = c.getPreferredSize().width;
                    compRows++;
                }else {
                    rowDim += c.getPreferredSize().width + getHgap();
                    compColons++;
                }
                if(rowDim > maxRowDim){maxRowDim = rowDim;}
            }
//            System.out.println(compRows);
            Dimension res = new Dimension(maxRowDim, compRows*(target.getComponent(0).getPreferredSize().height + getVgap()));

            this.setHgap(initialHGap);
            return res;
        }
        maxDimensions = dim;
        int targetPrefSize = dim.width;
        double maxElem = ((double) thisWidth / targetPrefSize);
        int fillGap = (int) ((thisWidth - (targetPrefSize * Math.floor(maxElem))) / maxElem);
        this.setHgap(fillGap);
        if(!(target.getComponent(0).getClass().equals(MItem.class))){this.setHgap(initialHGap);}

        compColons = (thisWidth / (targetPrefSize + fillGap));
        compRows = (int) Math.ceil((double) target.getComponentCount() / compColons);

        return new Dimension(compColons*(targetPrefSize + fillGap), compRows*(dim.height + getVgap()));
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        if (maxDimensions == null || compRows*compColons == 0 ) {
            return new Dimension(30, 10);
        }
        return preferredLayoutSize(target);
//        return new Dimension((maxDimensions.width + getHgap())*compColons, (int) ((maxDimensions.height*1.1f + getVgap())*compRows));
    }
}
