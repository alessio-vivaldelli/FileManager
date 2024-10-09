package org.example.Layout;

import org.example.MItem;
import org.example.view.Item;

import java.awt.*;

public class WrapLayout extends FlowLayout {
    private int initialHGap;
    private int initialVGap;

    private int compColons;
    private int compRows;
    private int thisHeight;
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
        thisHeight = thisContainer.getSize().height;
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
            for(Component c : target.getComponents()){
                if((rowDim + c.getPreferredSize().width + getHgap()) >= thisWidth){
                    rowDim = c.getPreferredSize().width;
                    compRows++;
                }else {
                    rowDim += c.getPreferredSize().width + getHgap();
                    compColons++;
                }
                if(rowDim > maxRowDim){maxRowDim = rowDim;}
            }
            Dimension res = new Dimension(maxRowDim, compRows*(target.getComponent(0).getPreferredSize().height + getVgap()));
//            System.out.println(res);
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
    }


    public int getItemHeightFromIndex(int index){
        int row = (index / compColons);
        return (row) * (getVgap() + Item.getItemOuterPreferredSize().height);
    }

    public int getRows(){return compRows;}
}
