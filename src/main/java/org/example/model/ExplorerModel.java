package org.example.model;

import org.example.ShortcutItem;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class ExplorerModel implements Model {

    final public static String NEW_SHORTCUT = "new_shortcut";
    final public static String TREE_INIT = "tree_initialized";


    private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);
    private List<ShortcutItem> leftShortcutTabs;

    private static DefaultMutableTreeNode top;
    private static final String windowsTopName = "This PC";

    public ExplorerModel(){
        leftShortcutTabs = new ArrayList<>();
    }

    public void initModel() {
        initTree();

    }

    private void initLeftShortcut(){

    }


    public void initTree(){}


    public void TMinitTree(){


        propertyChangeSupport.firePropertyChange(ExplorerModel.TREE_INIT, null, top);
    }

    public void addLeftShortcut(String path, String iconPath){

        ShortcutItem item = new ShortcutItem(path, iconPath);
        leftShortcutTabs.add(item);

        propertyChangeSupport.firePropertyChange(ExplorerModel.NEW_SHORTCUT, null, item);
    }

    public void showCurrentDirectory(File root){

    }

    public DefaultMutableTreeNode createTree(File temp) {
        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(temp);

        if(! (temp.exists() && temp.isDirectory()))         	//if it is not a directory, return
            return topNode;
        System.out.println("Start");
        fillTree(topNode, temp.getPath());
        System.out.println("Finish");

        return topNode;
    }
    private void fillTree(DefaultMutableTreeNode node, String fileName) {
//		tree.setEnabled(false);

        SwingWorker<Void, DefaultMutableTreeNode> worker = new SwingWorker<Void, DefaultMutableTreeNode>() {
            @Override
            public Void doInBackground() {
                File file = new File(fileName);
                File[] fileList = file.listFiles();
                if (fileList != null) {
                    for (int i = 0; i < fileList.length; i++) {
//                        final DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(fileList[i]);
                        final DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(new File(fileList[i].getPath()){
                            @Override
                            public String toString() {
                                if(getName().equals("")){
                                    return "Giorgio";
                                }
                                return getName();
                            }
                        });

                        if(fileList[i].isDirectory()){
                            publish(tempNode);
//						final String newFileName = fileName + "\\" + fileList[i].getName();
                            String newFileName = fileList[i].getPath();
                            fillTree(tempNode, newFileName);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<DefaultMutableTreeNode> chunks) {
                for (DefaultMutableTreeNode child : chunks) {
                    node.add(child);
                }
            }

            @Override
            protected void done() {
//				tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    @Override
    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }
    @Override
    public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(name, listener);
    }


}
