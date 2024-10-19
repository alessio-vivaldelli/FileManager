package org.example.Icons;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

import javax.swing.*;
import java.awt.*;

public class PlusIcon extends FlatAbstractIcon {
    public PlusIcon(){
        super(16,16, UIManager.getColor( "FileManager.folderIconColor" ));
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g2) {
        g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );

        g2.fillRoundRect(7,1, 3,14, 4,4);
        g2.fillRoundRect(1,7, 14,3, 4,4);

    }
}
