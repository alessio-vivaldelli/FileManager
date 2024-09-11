package org.example.model;

import org.example.MItem;
import org.example.view.Item;
import org.example.view.TagView;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemModel implements Model {
    final public static String NEW_ITEM = "new_folder_item";

    private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);
    List<TagView> itemTags = new ArrayList<>();

    private File lastOpenedFolder = null;

    public ItemModel(){
    }

    public void newTag(TagView tag){
//        itemTags.add(tag);
        return;
    }

    public void lastOpenedFolder(File item){
        lastOpenedFolder = item;
        propertyChangeSupport.firePropertyChange(ItemModel.NEW_ITEM, null, item);
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
