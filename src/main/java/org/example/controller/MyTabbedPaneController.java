package org.example.controller;

import org.example.TabPage;
import org.example.model.MyTabbedPaneModel;
import org.example.view.MyTabbedPaneView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

public class MyTabbedPaneController {

    MyTabbedPaneView view;
    MyTabbedPaneModel model;

    JTabbedPane tabbed;
    JPopupMenu popupMenu;
    JButton newTab;

    public MyTabbedPaneController(MyTabbedPaneView view, MyTabbedPaneModel model) {
        this.view = view;
        this.model = model;

        initController();
    }

    protected void initController(){
        tabbed = view.getTabbedPane();
        newTab = view.getNewTabButton();
        popupMenu = view.getPopupMenu();

        for (MenuElement subElement : popupMenu.getSubElements()) {
            String t = ((JMenuItem) subElement.getComponent()).getText();
            ((JMenuItem) subElement.getComponent()).addActionListener(this::subMenuListener);
        }

        newTab.addActionListener(this::newTabPressed);

        tabbed.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) this::removeTab);

        model.addPropertyChangeListener(MyTabbedPaneModel.NEW_TAB, this::addTab);
        model.addPropertyChangeListener(MyTabbedPaneModel.REMOVE_TAB, this::removeTab);
    }

    private void removeTab(int index){
        if (model.getTabCount() <= 1){return;}
        model.removeTab(index);
    }

    protected void newTabPressed(ActionEvent e)
    {
        popupMenu.show(newTab, newTab.getWidth() / 2, newTab.getHeight() / 2);;
    }

    protected void addTab(PropertyChangeEvent l){
        TabPage tab = (TabPage) l.getNewValue();
        tabbed.addTab(tab.toString(), tab.getUI());
    }

    protected void removeTab(PropertyChangeEvent l){
        tabbed.remove((int) l.getNewValue());
    }


    protected  void subMenuListener(ActionEvent l)
    {
        switch (((JMenuItem) l.getSource()).getText()){
            case "File Explorer":
                System.out.println("Add Explorer Tab");
                model.addTab(TabPage.EXPLORER);
                break;
            case "Test":
                System.out.println("Test");
                break;
            default:
                break;
        }
    }
}
