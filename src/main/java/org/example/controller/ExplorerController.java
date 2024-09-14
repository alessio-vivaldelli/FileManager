package org.example.controller;

import org.example.MItem;
import org.example.Util;
import org.example.Utilities.CloudPathFinder;
import org.example.model.ItemModel;
import org.example.ShortcutItem;
import org.example.model.ExplorerModel;
import org.example.view.ExplorerView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.formdev.flatlaf.extras.*;


public class ExplorerController {

    ExplorerView view;
    ExplorerModel model;
    JTabbedPane leftShortcut;
    JTree tree;
    private boolean isFocused = true;
    private JFrame frame;
    private List<MItem> itemList;
    private boolean isLoadingFolder = false;

    public ExplorerController(ExplorerView view, ExplorerModel model){
        this.view = view;
        this.model = model;

        itemList = new ArrayList<>();

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

        //TODO: decidere se tenere il tree
//        for (File file : File.listRoots()){
//            System.out.println(file.getPath());
//            root.add(model.createTree(file));
//        }
//        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        tree.addTreeSelectionListener(this::treeSelectionChange);

        view.fileView_p.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("CNDUSAIFLH");
                view.fileView_p.requestFocusInWindow();
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
        view.fileView_p.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("Focus IN");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Focus OUT");

            }
        });
        view.fileView_p.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

                if((e.getKeyChar() < 'a' || e.getKeyChar() > 'z') &&
                        (e.getKeyChar() < 'A' || e.getKeyChar() > 'Z') &&
                        (e.getKeyChar() < '0' || e.getKeyChar() > '9')){
                    return;
                }

                boolean res = filterItemsByText(view.searchText.getText().toLowerCase() + e.getKeyChar());
                view.searchText.exist(res);
                view.searchText.addChar(e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        refreshTagsList();
        refreshShortcutList();
        refreshDisksList();
        refreshCloudList();
    }

    // TODO: handle multiple tag selection
    private void refreshTagsList(){
        for (Map.Entry<String, String> elem : Util.getTagsColorMap().entrySet()){
            JToggleButton tmp = view.addTagItem(elem.getKey(), elem.getValue());
            tmp.addActionListener(this::tagFilterChanges);
        }
    }

    private void tagFilterChanges(ActionEvent e){
        JToggleButton source = (JToggleButton) e.getSource();
        System.out.println(source.getText() + " is: " + source.isSelected());
    }

    private void refreshShortcutList(){
        Util.getShortcutData().forEach((elem) -> {
            JButton tmp = view.addShortcutItem(elem);
            tmp.addActionListener(this::handleLeftPanelClicks);
        });
    }

    private void refreshDisksList(){
        for(File file : File.listRoots()){
            JButton tmp = view.addDiskItem(file);
            tmp.addActionListener(this::handleLeftPanelClicks);
        }
    }

    private void refreshCloudList(){
        for(String file : CloudPathFinder.getCloudPaths()){
            JButton tmp = view.addCloudItem(new File(file));
            tmp.addActionListener(this::handleLeftPanelClicks);
        }
    }

    private void handleLeftPanelClicks(ActionEvent e){
        String path = e.getSource().toString();
        if(path.equals("Favourite")){return;}
        File f = new File(path);
        if(f.equals(model.getOpenedFolder())){return;}
        if(isLoadingFolder){return;}
        showFolder(f);
    }

    private boolean filterItemsByText(String filterText){
        List<MItem> res = itemList.stream().filter(item -> item.toString().toLowerCase().startsWith(filterText)).toList();
        System.out.println(res);
        return !res.isEmpty();
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
        model.setOpenedFolder(f);
        view.fileView_p.removeAll();
        view.fileView_p.repaint();
        view.fileView_p.revalidate();

        itemList = new ArrayList<>();
        isLoadingFolder = true;

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
                    itemList.add(a);
                }
            }

            @Override
            protected void done() {
                isLoadingFolder = false;
                return;
            }
        };
        worker.execute();
    }

    private void addItemListener(MItem item){
        item.getModel().addPropertyChangeListener(ItemModel.NEW_ITEM, this::newItem);
    }

}
