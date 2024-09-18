package org.example.model;

import org.example.MItem;
import org.example.ShortcutItem;
import org.example.Util;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class ExplorerModel implements Model {

    final public static String NEW_SHORTCUT = "new_shortcut";
    final public static String TREE_INIT = "tree_initialized";
    final public static String NEW_ITEM = "new_folder_item";
    final public static String TAG_DELETED = "tag_deleted_from_explorer";
    final public static String SELECTION_UPDATE = "selection_list_updated";
    final public static String DESELCT_ITEM = "remove_item_selection";
    final public static String REMOVE_ALL_SELECTION = "clear_selection_list";

    private List<ItemModel> selectedItems;
    private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);
    private File openedFolder;


    public ExplorerModel(){
        openedFolder = null;
        selectedItems = new ArrayList<>();
    }

    public void initModel() {

    }

    public void newSelection(ItemModel model , boolean isControlDown){
        if(isControlDown){
            if(selectedItems.contains(model)){
                selectedItems.remove(model);
                propertyChangeSupport.firePropertyChange(ExplorerModel.DESELCT_ITEM, null, model);
            }
            else{ selectedItems.add(model);}
            propertyChangeSupport.firePropertyChange(ExplorerModel.SELECTION_UPDATE, null, selectedItems);
        }
        else{
            propertyChangeSupport.firePropertyChange(ExplorerModel.REMOVE_ALL_SELECTION, null, selectedItems);
            selectedItems = new ArrayList<>();
            selectedItems.add(model);
            propertyChangeSupport.firePropertyChange(ExplorerModel.SELECTION_UPDATE, null, selectedItems);
        }

    }

    public boolean isTagDuplicated(String tagName){
        return Util.getTagsMap().containsKey(tagName);
    }

    // TODO: update listeners like MItem witch has to update popup menu
    public void newTag(String tagName, String tagColor){
        Util.newTag(tagName, tagColor);
    }

    public void deleteTag(String tagName){
        Util.deleteTag(tagName);
        propertyChangeSupport.firePropertyChange(ExplorerModel.TAG_DELETED, null, tagName);
    }

    public void setOpenedFolder(File file){
        openedFolder = file;
    }
    public File getOpenedFolder(){return openedFolder;}

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
//                        System.out.println("working..");
//                        final DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(fileList[i]);
                        final DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(new File(fileList[i].getPath()){
                            @Override
                            public String toString() {
                                return getName();
                            }
                        });

                        int ind = fileList[i].getPath().lastIndexOf('.');
                        if(ind <= 0){
                            publish(tempNode);
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
            }
        };
        worker.execute();
    }

    public void addItem(MItem item){
        propertyChangeSupport.firePropertyChange(ExplorerModel.NEW_ITEM, null, item);
    }

    @Override
    public void subscribeToModel(Model e) {

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

    /**
     * Compare item based on their name. Folder has higher priority
     * than file, as well as special folder (witch starts with a dot)
     */
    public static class CompareItems implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {

            int ind1 = o1.getPath().lastIndexOf('.');
            int ind2 = o2.getPath().lastIndexOf('.');
            if(o1.getName().startsWith(".")){return -1;}
            if(o2.getName().startsWith(".")){return 1;}

            if (ind1 > 0 && ind2 > 0) {
                return o1.getName().compareTo(o2.getName());
            } else if (ind1 <= 0 && ind2 <= 0) {
                return o1.getName().compareTo(o2.getName());
            }else if(ind1 > 0 && ind2 <= 0){
                return 1;
            } else if (ind2 > 0 && ind1 <= 0) {
                return -1;
            }
            return 0;
        }
    }
}
