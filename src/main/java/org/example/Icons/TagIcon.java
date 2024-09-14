package org.example.Icons;

import java.awt.*;
import java.awt.geom.*;
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
public class TagIcon
        extends FlatAbstractIcon
{

    private static double multFactor;
    private int size;


    public TagIcon(int size) {
        super( size, size, UIManager.getColor( "Action.Grey" ) ); // UIManager.getColor( "Objects.Grey" )
        multFactor = size / 16.0;
        this.size = size;
    }

    @Override
    protected void paintIcon( Component c, Graphics2D g ) {

        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

//        g.setColor(UIManager.getColor( "Objects.Grey" ));
        BasicStroke stroke = new BasicStroke((float) (1.8*multFactor), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND );
        Path2D path;
        g.setStroke(stroke);
        path = FlatUIUtils.createPath(8,0.5,        3,5.23,          3,16,         13,16,        13,5.23);

        path.append(new Ellipse2D.Float(9 - 2, 8 - 2, 2, 2), false);

        path.transform(AffineTransform.getScaleInstance(multFactor, multFactor));
        path.transform(AffineTransform.getTranslateInstance(0,0));
        path.transform(AffineTransform.getRotateInstance(Math.toRadians(-45), size/2, size/2));

        g.draw( path );
//        g.setColor(UIManager.getColor( "Objects.Grey"));
    }

}
