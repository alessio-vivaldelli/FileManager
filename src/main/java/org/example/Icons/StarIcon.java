package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;


// Controllare: FlatFileViewDirectoryIcon
public class StarIcon
        extends FlatAbstractIcon
{
    private int size = 16;
    private int innerRadius = 5;
    private int outerRadius = 10;
    private int numRays = 5;
    private boolean isSelected = false;

    private final Color blueColor = UIManager.getColor( "Actions.Blue" );
    private final Color whiteColor = UIManager.getColor( "Actions.White" );

    public StarIcon() {
        super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
    }
    public StarIcon(boolean isSelected) {
        super( 16, 16, UIManager.getColor( "Actions.Grey" ) );
        this.isSelected = isSelected;
    }
    public StarIcon(int size, int innerRadius, int outerRadius, int numRays) {
        super( size, size, UIManager.getColor( "Actions.Grey" ) );
        this.size = size;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.numRays = numRays;
    }
    public void toggleStar(){
        this.isSelected = !this.isSelected;
    }
    public boolean isSelected(){
        return this.isSelected;
    }
    public void setIsSelected(boolean status){
        this.isSelected = status;
    }

    private static Shape createStar(double centerX, double centerY,
                                    double innerRadius, double outerRadius, int numRays,
                                    double startAngleRad)
    {
        Path2D path = new Path2D.Double();
        double deltaAngleRad = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++)
        {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0)
            {
                relX *= outerRadius;
                relY *= outerRadius;
            }
            else
            {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0)
            {
                path.moveTo(centerX + relX, centerY + relY);
            }
            else
            {
                path.lineTo(centerX + relX, centerY + relY);
            }
        }
        path.closePath();
        
        return path;
    }

    @Override
    protected void paintIcon( Component c, Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
        g.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g.setColor( whiteColor );
        g.setStroke(new BasicStroke(2));
        if(isSelected){
            g.fill(createStar(size / 2, size / 2, innerRadius, outerRadius, numRays, Math.toRadians(-18)));
        }else {
            g.draw(createStar(size / 2, size / 2, innerRadius, outerRadius, numRays, Math.toRadians(-18)));
        }
    }
}