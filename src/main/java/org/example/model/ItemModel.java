package org.example.model;

import org.example.DatabasesUtil;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemModel implements Model {
    final public static String NEW_ITEM = "new_folder_item";
    final public static String REFRESH_TAGS = "tag_list_refresh";
    final public static String ITEM_SELECTED = "item_selected";
    final public static String NEW_SELECTION_STATUS = "set_the_selection_status_of_item";
    final public static String ITEM_DRAGGING = "item_dragging_event";
    final public static String DRAGGING_FINISHED = "item_dragging_event_finished";


    private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);

    private File file = null;
    public boolean isFavourite = false;
    private List<String> subTags = new ArrayList<>();
    public Map<String, String> tagsMap;

    private File lastOpenedFolder = null;

    public ItemModel(File file){
        this.file = file;
        initModel();
    }

    public void setSelected(boolean newValue){
        propertyChangeSupport.firePropertyChange(ItemModel.NEW_SELECTION_STATUS, null, newValue);
    }

    public void itemSelected(boolean isControlDown){
        propertyChangeSupport.firePropertyChange(ItemModel.ITEM_SELECTED, isControlDown, this);
    }

    public void subscribeToModel(Model e){
        e.addPropertyChangeListener(ExplorerModel.TAG_DELETED, this::tagDeleted);
        e.addPropertyChangeListener(ExplorerModel.DRAGGING_SELECTION_ACTION, this::draggingSelectionDetected);
    }

    private void tagDeleted(PropertyChangeEvent e){

        propertyChangeSupport.firePropertyChange(ExplorerModel.TAG_DELETED, null, e.getNewValue());
    }

    private void initModel(){
        getAllTags();
        refreshTags();

        isFavourite = DatabasesUtil.isFileFavourite(file);
    }

    private void draggingSelectionDetected(PropertyChangeEvent e){
        Rectangle selectionRectangle = (Rectangle) e.getNewValue();
        propertyChangeSupport.firePropertyChange(ExplorerModel.DRAGGING_SELECTION_ACTION, null, selectionRectangle);
    }

    private void getAllTags(){
        tagsMap = DatabasesUtil.getTagsColorMap();
    }

    public void setFavourite(boolean favourite){
        this.isFavourite = favourite;
        if(favourite){
            DatabasesUtil.addFavourite("", file.getPath());
        }else {
            DatabasesUtil.removeFavourite(file.getPath());
        }
    }

    public boolean isFavourite(){return isFavourite;}

    public void refreshTags(){
        subTags = DatabasesUtil.getSubsTags(file);

        propertyChangeSupport.firePropertyChange(ItemModel.REFRESH_TAGS, null, subTags);
    }

    public File getFile(){return this.file;}


    public void newTagSelected(String name){
        DatabasesUtil.newFileTag(name, file.getPath());
    }

    public void removeTag(String name){
        DatabasesUtil.removeTagFromFile(file.getPath(), name);
    }

    public void lastOpenedFolder(File item){
        lastOpenedFolder = item;
        propertyChangeSupport.firePropertyChange(ItemModel.NEW_ITEM, null, item);
    }

    public void dragItem(Point mousePosition, Icon fileIcon){
        propertyChangeSupport.firePropertyChange(ItemModel.ITEM_DRAGGING, fileIcon, mousePosition);
    }

    public void dragFinish(Point mousePosition){
        propertyChangeSupport.firePropertyChange(ItemModel.DRAGGING_FINISHED, null, mousePosition);
    }

    public void stopDrag(){
        propertyChangeSupport.firePropertyChange(ItemModel.ITEM_DRAGGING, null, null);
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
