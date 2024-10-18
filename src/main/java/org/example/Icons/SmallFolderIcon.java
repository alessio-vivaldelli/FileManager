package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class SmallFolderIcon
        extends FlatAbstractIcon
{
    private Path2D path;
    private static double multFactor;
    private int size;

    public SmallFolderIcon(int size) {
        super( size, size, UIManager.getColor( "FileManager.accentColor" ) );
        multFactor = size / 16.0;
        this.size = size;
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        if( path == null )
            path = createFolderPath(size);
        g.setStroke(new BasicStroke(1));
        g.draw(path);
    }

    static Path2D createFolderPath(int size) {
        double arc = 1.5;
        double arc2 = 0.5;
        Path2D a =  FlatUIUtils.createPath(
                // bottom-right
                14.5,13.5-arc,  FlatUIUtils.QUAD_TO, 14.5,13.5, 14.5-arc,13.5,
                // bottom-left
                1.5+arc,13.5,   FlatUIUtils.QUAD_TO, 1.5,13.5,  1.5,13.5-arc,
                // top-left
                1.5,2.5+arc,    FlatUIUtils.QUAD_TO, 1.5,2.5,   1.5+arc,2.5,
                // top-mid-left
                6.5-arc2,2.5,   FlatUIUtils.QUAD_TO, 6.5,2.5,   6.5+arc2,2.5+arc2,
                // top-mid-right
                8.5,4.5,
                // top-right
                14.5-arc,4.5,   FlatUIUtils.QUAD_TO, 14.5,4.5,  14.5,4.5+arc );

//        a.transform(AffineTransform.getScaleInstance(multFactor, multFactor));
//        a.transform(AffineTransform.getTranslateInstance(size/4,size/2 - 16));
        return a;
    }
}