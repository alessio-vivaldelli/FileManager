package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class GreaterIcon  extends FlatAbstractIcon {

    private double multFactor;
    private int size;

    public GreaterIcon(int size){
        super( size, size, UIManager.getColor( "FileManager.accentColor" ));
        multFactor = size / 16.0;
        this.size = size;
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g2) {
        BasicStroke stroke = new BasicStroke((float) (1.8*multFactor), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND );
        Path2D path = new Path2D.Double();
        g2.setStroke(stroke);
        path.moveTo(5 * multFactor, 4 * multFactor);
        path.lineTo(9.5 * multFactor, 8 * multFactor);
        path.lineTo(5 * multFactor, 12 * multFactor);

        path.transform(AffineTransform.getScaleInstance(multFactor, multFactor));
        path.transform(AffineTransform.getTranslateInstance(0,0));

        g2.draw( path );
    }
}
