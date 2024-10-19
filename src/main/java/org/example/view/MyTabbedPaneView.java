package org.example.view;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.extras.*;

public class MyTabbedPaneView {

    JToolBar trailing;
    JPopupMenu popupMenu;
    JButton newTab;
    JTabbedPane tabbedPane;
    JPanel panel;

    public MyTabbedPaneView() {
        initView();
    }

    private void initView() {

        tabbedPane = new JTabbedPane();

        trailing = new JToolBar();
        trailing.setFloatable( true );
//        trailing.setBorder( null );
        newTab = new JButton( new FlatSVGIcon( "icons/plus.svg" ) );
        trailing.add( newTab );

        popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("File Explorer");
        JMenuItem menuItem2 = new JMenuItem("Menu 2");
        JMenuItem menuItem3 = new JMenuItem("Menu 3");
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        popupMenu.add(menuItem3);


        tabbedPane.putClientProperty( "JTabbedPane.trailingComponent", trailing );
        // this.putClientProperty( "JTabbedPane.leadingComponent", trailing );
        tabbedPane.putClientProperty("JTabbedPane.tabAreaInsets", 50);

        tabbedPane.putClientProperty("JTabbedPane.tabClosable", true);
        // tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (BiConsumer<JTabbedPane, Integer>) JTabbedPane::remove);

        tabbedPane.putClientProperty("JTabbedPane.tabsPopupPolicy", "asNeeded");
        tabbedPane.putClientProperty("JTabbedPane.scrollButtonsPolicy", "asNeededSingle");
        tabbedPane.putClientProperty("JTabbedPane.scrollButtonsPlacement", "both");
        tabbedPane.putClientProperty("JTabbedPane.maximumTabWidth", 150);
        tabbedPane.putClientProperty("JTabbedPane.tabWidthMode", "compact");

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.putClientProperty("FlatLaf.styleClass", "h3");

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tabbedPane, BorderLayout.CENTER);
    }

    public JPanel getUI() {
        return panel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JButton getNewTabButton() {
        return newTab;
    }

}
