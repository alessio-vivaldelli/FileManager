package org.example.Icons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import javax.swing.UIManager;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

/**
 * "file" icon for {@link javax.swing.JFileChooser}.
 *
 * @uiDefault Objects.Grey						Color
 *
 * @author Karl Tauber
 */
public class FileIcon
        extends FlatAbstractIcon
{
    private Path2D path;
    private static double multFactor;
    private int size;
    private String extension;

    public FileIcon(int size, String extension) {
            super( 128, 128, UIManager.getColor( "FileManager.fileIconColor" ) );
        multFactor = size / 16.0;
        this.size = size;
        this.extension = extension;
    }

    @Override
    protected void paintIcon( Component c, Graphics2D g ) {

        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        g.setStroke( new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

        if( path == null ) {
            double arc = 1.5;
            path = FlatUIUtils.createPath( false,
                    // top-left
                    2.5,1.5+arc,    FlatUIUtils.QUAD_TO, 2.5,1.5,   2.5+arc,1.5,
                    // top-right
                    8.8,1.5, 13.5,6.2,
                    // bottom-right
                    13.5,14.5-arc,  FlatUIUtils.QUAD_TO, 13.5,14.5, 13.5-arc,14.5,
                    // bottom-left
                    2.5+arc,14.5,   FlatUIUtils.QUAD_TO, 2.5,14.5,  2.5,14.5-arc,
                    FlatUIUtils.CLOSE_PATH,

                    FlatUIUtils.MOVE_TO, 8.5,2,
                    8.5,6.5-arc,    FlatUIUtils.QUAD_TO, 8.5,6.5,   8.5+arc,6.5,
                    13,6.5 );
            path.transform(AffineTransform.getScaleInstance(multFactor, multFactor));
            path.transform(AffineTransform.getTranslateInstance(size/4,size/2 - 16));
        }

        int iconX_start = size/2;
        int iconX_end = size/2 + size/2;
        int x_center = iconX_start + ((iconX_end - iconX_start)/2);
        int initFontSize = 20;
//        String extension = "C";
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int width = g.getFontMetrics(g.getFont()).stringWidth(extension);

        while (width > (iconX_end-iconX_start)){
            g.setFont(new Font("Arial", Font.BOLD, g.getFont().getSize()-1));
            width = g.getFontMetrics(g.getFont()).stringWidth(extension);
        }

        int text_start = x_center - (width/2);

        g.draw( path );
        g.drawString(extension, text_start,size);

    }
}
