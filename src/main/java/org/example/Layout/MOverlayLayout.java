package org.example.Layout;

import javax.swing.*;
import java.awt.*;

public class MOverlayLayout extends OverlayLayout {
    /**
     * Constructs a layout manager that performs overlay
     * arrangement of the children.  The layout manager
     * created is dedicated to the given container.
     *
     * @param target the container to do layout against
     */
    public MOverlayLayout(Container target) {
        super(target);
        this.target = target;
    }

    public void layoutContainer(Container target) {
        checkContainer(target);
        checkRequests();

        int nChildren = target.getComponentCount();
        int[] xOffsets = new int[nChildren];
        int[] xSpans = new int[nChildren];
        int[] yOffsets = new int[nChildren];
        int[] ySpans = new int[nChildren];

        // determine the child placements
        Dimension alloc = target.getSize();
        Insets in = target.getInsets();
        alloc.width -= in.left + in.right;
        alloc.height -= in.top + in.bottom;
        SizeRequirements.calculateAlignedPositions(alloc.width, xTotal,
                xChildren, xOffsets,
                xSpans);
        SizeRequirements.calculateAlignedPositions(alloc.height, yTotal,
                yChildren, yOffsets,
                ySpans);

        int prefIndex = ySpans.length - 1;
        int maxY = ySpans[prefIndex];
        int maxX = xSpans[prefIndex];

        for (int i = 0; i < nChildren; i++) {
            Component c = target.getComponent(i);
//            c.setBounds(in.left + xOffsets[i], in.top + yOffsets[i],
//                    xSpans[i], ySpans[i]);
            int spanX = (i<=prefIndex) ? xSpans[i] : c.getPreferredSize().width;
            int spanY = (i<=prefIndex) ? ySpans[i] : c.getPreferredSize().height;
            int x = (int) (in.left + (maxX * c.getAlignmentX()));
            int y = (int) (in.top + (maxY * c.getAlignmentY()));
            if(c.getAlignmentX() == 1.0f){
                x -= spanX;
            }if(c.getAlignmentY() == 1.0f){
                y -= spanY;
            }
            c.setBounds(x, y,
                    spanX, spanY);
        }
    }

    void checkContainer(Container target) {
        if (this.target != target) {
            throw new AWTError("OverlayLayout can't be shared");
        }
    }

    void checkRequests() {
        if (xChildren == null || yChildren == null) {
            // The requests have been invalidated... recalculate
            // the request information.
            int n = target.getComponentCount();
            xChildren = new SizeRequirements[n];
            yChildren = new SizeRequirements[n];
            for (int i = 0; i < n; i++) {
                Component c = target.getComponent(i);
                Dimension min = c.getMinimumSize();
                Dimension typ = c.getPreferredSize();
                Dimension max = c.getMaximumSize();
                if(c.getAlignmentX() == 0.0f && c.getAlignmentY() == 0.0f) {
                    xChildren[i] = new SizeRequirements(min.width, typ.width,
                            max.width,
                            c.getAlignmentX());
                    yChildren[i] = new SizeRequirements(min.height, typ.height,
                            max.height,
                            c.getAlignmentY());
                }else {
                    xChildren[i] = new SizeRequirements(min.width, typ.width,
                            max.width,
                            0.0f);
                    yChildren[i] = new SizeRequirements(min.height, typ.height,
                            max.height,
                            0.0f);
                }
            }

            xTotal = SizeRequirements.getAlignedSizeRequirements(xChildren);
            yTotal = SizeRequirements.getAlignedSizeRequirements(yChildren);
        }
    }

    private Container target;
    private SizeRequirements[] xChildren;
    private SizeRequirements[] yChildren;
    private SizeRequirements xTotal;
    private SizeRequirements yTotal;
}
