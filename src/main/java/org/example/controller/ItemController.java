package org.example.controller;

import org.example.Icons.StarIcon;
import org.example.MItem;
import org.example.Util;
import org.example.model.ExplorerModel;
import org.example.model.ItemModel;
import org.example.view.Item;
import org.example.view.TagView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ItemController {

    private Item view;
    private ItemModel model;
    private JButton starButton;
    private JButton mainButton;

    public ItemController(Item view, ItemModel model){
        this.view = view;
        this.model = model;
        initController();
    }

    private void initController() {

        mainButton = view.getItemButton();


        MouseHandlerClass mouseHandler = new MouseHandlerClass();
        mainButton.addMouseListener(mouseHandler);

        starButton = view.getStarButton();
        starButton.addActionListener(this::starClicked);

        model.addPropertyChangeListener(ItemModel.REFRESH_TAGS, this::tagsRefreshed);
        model.addPropertyChangeListener(ItemModel.NEW_SELECTION_STATUS, this::setSeletionStatus);
        model.addPropertyChangeListener(ExplorerModel.TAG_DELETED, this::tagDeleted);

        setStarStatus(model.isFavourite());

        setTagsMenu();
        model.refreshTags();
    }

    private void setSeletionStatus(PropertyChangeEvent e){
        mainButton.setSelected((boolean) e.getNewValue());
    }

    private void tagDeleted(PropertyChangeEvent e){
        int count = view.getPopupMenu().getComponentCount();
        for (int i = 0; i < count; i++) {
            if( ((JCheckBoxMenuItem) view.getPopupMenu().getComponent(i)).getText().equals(e.getNewValue()) ){
                view.getPopupMenu().remove(i);
                break;
            }
        }
    }

    private void setTagsMenu(){
        view.getPopupMenu().removeAll();
        model.tagsMap.forEach((key, value) -> {
            JCheckBoxMenuItem tmp = new JCheckBoxMenuItem(key);
            tmp.addActionListener(this::tagSelected);
            view.getPopupMenu().add(tmp,0);
        });
    }

    private void tagSelected(ActionEvent e){
        String tmp = ((JCheckBoxMenuItem) e.getSource()).getText();
        boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
        if(selected) {
            view.tagsPanel.addTag(tmp);
            model.newTagSelected(tmp);
        }else {
            view.tagsPanel.removeTag(tmp);
            model.removeTag(tmp);
        }
    }

    private void tagsRefreshed(PropertyChangeEvent e){
        List<String> subs = (List<String>) e.getNewValue();

        view.tagsPanel.setTags(subs); // refresh colored circles below item label

        for (int i = 0; i < view.getPopupMenu().getComponentCount(); i++) {
            JCheckBoxMenuItem tmp = (JCheckBoxMenuItem) view.getPopupMenu().getComponent(i);
            if(subs.contains( tmp.getText())){
                ((JCheckBoxMenuItem) view.getPopupMenu().getComponent(i)).setSelected(true);
            }
        }
    }

    private void starClicked(ActionEvent e){
        ((StarIcon) starButton.getIcon()).toggleStar();
        model.setFavourite(!model.isFavourite());

    }

    private void setStarStatus(boolean status){
        ((StarIcon) starButton.getIcon()).setIsSelected(status);
        model.isFavourite = status;
    }

    private void openFolder(){

        if(view.file == null){return;}

        JPanel fileView_p = (JPanel) view.getParent();

        fileView_p.removeAll();
        fileView_p.repaint();
        fileView_p.revalidate();

        SwingWorker<Void, MItem> worker = new SwingWorker<Void, MItem>() {
            @Override
            public Void doInBackground() {

                for (File a : Arrays.stream(view.file.listFiles()).sorted(new ExplorerModel.CompareItems()).toList())
                {
                    System.out.println("Adding: " + a.getName() + ", to:" + fileView_p.getParent());
                    publish(new MItem(a));
                }
                return null;
            }

            @Override
            protected void process(List<MItem> chunks) {
                for(MItem a : chunks){
                    fileView_p.add(a);
                }
                fileView_p.revalidate();
            }

            @Override
            protected void done() {
                return;
            }
        };
        worker.execute();
    }

    public class MouseHandlerClass implements MouseMotionListener, MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                if (view.file.isDirectory()){
                    model.lastOpenedFolder(view.file);
                }
            }else {
                model.itemSelected(e.isControlDown());
            }
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

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }


}

