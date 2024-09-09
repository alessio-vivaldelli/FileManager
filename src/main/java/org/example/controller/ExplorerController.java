package org.example.controller;

import org.example.ShortcutItem;
import org.example.model.ExplorerModel;
import org.example.view.ExplorerView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Map;
import com.formdev.flatlaf.extras.*;


public class ExplorerController {

    ExplorerView view;
    ExplorerModel model;
    JTabbedPane leftShortcut;
    JTree tree;


    public ExplorerController(ExplorerView view, ExplorerModel model){
        this.view = view;
        this.model = model;

        initController();
        model.initModel();
    }

    private void initController() {

        leftShortcut = view.getShortcutTabs();
        leftShortcut.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(leftShortcut.getComponentAt(leftShortcut.getSelectedIndex()).toString());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        model.addPropertyChangeListener(ExplorerModel.NEW_SHORTCUT, this::newShortcutTab);
        model.addPropertyChangeListener(ExplorerModel.TREE_INIT, this::treeInitialized);

        tree = view.getTree();
        DefaultMutableTreeNode root = model.createTree(new File("This Computer"));

        for (File file : File.listRoots()){
            System.out.println(file.getPath());
            root.add(model.createTree(file));
        }
        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        tree.addTreeSelectionListener(this::treeSelectionChange);
    }

    private void newShortcutTab(PropertyChangeEvent e){
        ShortcutItem item = (ShortcutItem) e.getNewValue();


        leftShortcut.addTab(item.name, new FlatSVGIcon( "icons/docs.svg", ExplorerView.ICON_SIZE, ExplorerView.ICON_SIZE ), item);
    }

    private void treeInitialized(PropertyChangeEvent e){
        DefaultTreeModel s = new DefaultTreeModel((DefaultMutableTreeNode) e.getNewValue());
        view.getTree().setModel(s);
    }

    private void treeSelectionChange(TreeSelectionEvent e){

    }
}
