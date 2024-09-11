package org.example.controller;

import org.example.MItem;
import org.example.model.ItemModel;
import org.example.view.Item;
import org.example.ShortcutItem;
import org.example.model.ExplorerModel;
import org.example.view.ExplorerView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.formdev.flatlaf.extras.*;


public class ExplorerController {

    ExplorerView view;
    ExplorerModel model;
    JTabbedPane leftShortcut;
    JTree tree;
    private boolean isFocused = true;
    private JFrame frame;


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
        model.addPropertyChangeListener(ExplorerModel.NEW_ITEM, this::newItem);

        tree = view.getTree();
        DefaultMutableTreeNode root = model.createTree(new File("This Computer"));

        for (File file : File.listRoots()){
            System.out.println(file.getPath());
            root.add(model.createTree(file));
        }
        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        tree.addTreeSelectionListener(this::treeSelectionChange);


        new SwingWorker<Void, Void>() {
            JFrame topFrame;
            @Override
            protected Void doInBackground() throws Exception {

                do {
                    topFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, view.innerPanel);
                } while (topFrame == null);
                return null;
            }

            @Override
            protected void done() {
                setFrame(topFrame);
            }
        }.execute();


    }

    private void setFrame(JFrame frame) {
        this.frame  = frame;
    }

    private void newItem(PropertyChangeEvent e){
        showFolder((File) e.getNewValue());
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
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
        File f = (File) (node.getUserObject());
        showFolder(f);
    }

    private void showFolder(File f){
        if(f == null){return;}
        view.fileView_p.removeAll();
        view.fileView_p.repaint();
        view.fileView_p.revalidate();

        SwingWorker<Void, MItem> worker = new SwingWorker<Void, MItem>() {
            @Override
            public Void doInBackground() {
                for (File a : Arrays.stream(f.listFiles()).sorted(new ExplorerModel.CompareItems()).toList())
                {
                    if(a == null){continue;}
                    MItem newItem = new MItem(a);
                    addItemListener(newItem);
                    publish(newItem);
                }
                return null;
            }

            @Override
            protected void process(List<MItem> chunks) {
                for(MItem a : chunks){
                    view.addItem(a);
                }
            }

            @Override
            protected void done() {
                return;
            }
        };
        worker.execute();
    }

    private void addItemListener(MItem item){
        item.getModel().addPropertyChangeListener(ItemModel.NEW_ITEM, this::newItem);
    }

}
