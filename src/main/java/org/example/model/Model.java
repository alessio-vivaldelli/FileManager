package org.example.model;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public interface Model {

    public void subscribeToModel(Model e);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String name, PropertyChangeListener listener);

    public void removePropertyChangeListener(String name, PropertyChangeListener listener);
}
