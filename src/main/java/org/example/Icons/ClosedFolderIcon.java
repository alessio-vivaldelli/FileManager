package org.example.Icons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.UIManager;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "directory" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class ClosedFolderIcon
        extends FlatAbstractIcon
{
    private Path2D path;
    private static double multFactor;
    private int size;

    public ClosedFolderIcon(int size) {
        super( 128, 128, UIManager.getColor( "Objects.Grey" ) );
        multFactor = size / 16.0;
        this.size = size;
    }

    @Override
    protected void paintIcon( Component c, Graphics2D g ) {
		/*
			<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
			  <path fill="none" stroke="#6E6E6E" d="M13,13.5 L3,13.5 C2.17157288,13.5 1.5,12.8284271 1.5,12 L1.5,4 C1.5,3.17157288 2.17157288,2.5 3,2.5 L6.29289322,2.5 C6.42550146,2.5 6.55267842,2.55267842 6.64644661,2.64644661 L8.5,4.5 L8.5,4.5 L13,4.5 C13.8284271,4.5 14.5,5.17157288 14.5,6 L14.5,12 C14.5,12.8284271 13.8284271,13.5 13,13.5 Z"/>
			</svg>
		*/

        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

        if( path == null )
            path = createFolderPath(size);
        g.setStroke(new BasicStroke(4));
        g.setColor(UIManager.getColor( "Objects.Grey" ));
        g.fill(path);
//        g.draw( path );
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
        a.transform(AffineTransform.getScaleInstance(multFactor, multFactor));
        a.transform(AffineTransform.getTranslateInstance(size/4,size/2 - 16));
        return a;
    }
}
