package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;
import javax.swing.*;
import java.awt.*;


// Controllare: FlatFileViewDirectoryIcon
public class FlatDotsIcon extends FlatAbstractIcon {
    private final Color whiteColor = UIManager.getColor( "Actions.White" );
    private int size = 16;
    public FlatDotsIcon(int size) {
        super( size, size, UIManager.getColor( "Actions.Grey" ) );
        this.size = 16;
    }
    public FlatDotsIcon() {
        super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
    }
    @Override
    protected void paintIcon( Component c, Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );

        g.setColor( whiteColor );
        g.fillOval(5, 0, 4,4);
        g.fillOval(5, 7, 4,4);
        g.fillOval(5, 14, 4,4);
    }
}
