package org.example.model;

import org.example.MExplorer;
import org.example.TabPage;
import org.example.controller.ExplorerController;
import org.example.view.ExplorerView;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


public class MyTabbedPaneModel implements Model {

    final public static String NEW_TAB = "newTab";
    final public static String REMOVE_TAB = "removeTab";

    List<TabPage> pagesContent = new ArrayList<>();

    private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);


    /*
        Ottenere il numero di tab.
        Ottenere il titolo di una tab.
        Ottenere il contenuto di una tab.
        Ottenere l'indice della tab selezionata.
        Impostare la tab selezionata.
    * */

    public MyTabbedPaneModel() {

    }

    public void addTab(String Type){

        TabPage page = null;
        switch (Type){
            case TabPage.EXPLORER:
//                page = new ExplorerView();
//                ExplorerModel explorerModel = new ExplorerModel();
//                new ExplorerController((ExplorerView) page, explorerModel);
                page = new MExplorer();
                break;
            case TabPage.OTHER:
                page = new TabPage("Other");
                break;
            default:
                break;
        }

        pagesContent.add(page);
        propertyChangeSupport.firePropertyChange(NEW_TAB, null, page);
    }

    public void removeTab(int index){
        pagesContent.remove(index);

        propertyChangeSupport.firePropertyChange(REMOVE_TAB, null, index);
    }

    public List<TabPage> getPages(){
        return pagesContent;
    }
    public TabPage getTabToAdd(){
        return pagesContent.get(pagesContent.size()-1);
    }

    public int getTabCount() {
        return pagesContent.size();
    }

    public String getTabTitle(int index) {
        return null;
    }

    public String getTabContent(int index) {
        return null;
    }

    public int getSelectedTabIndex() {
        return 0;
    }

    public void setSelectedTabIndex(int index) {

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

}
