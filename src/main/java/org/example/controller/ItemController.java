package org.example.controller;

import org.example.Icons.StarIcon;
import org.example.MItem;
import org.example.Util;
import org.example.model.ExplorerModel;
import org.example.model.ItemModel;
import org.example.view.Item;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ItemController {

    private Item view;
    private ItemModel model;
    private JButton starButton;

    public ItemController(Item view, ItemModel model){
        this.view = view;
        this.model = model;
        initController();
    }

    private void initController() {

        JButton mainButton = view.getItemButton();
        mainButton.addActionListener(event -> {
            mainButton.setSelected(true);
        });

        mainButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        if (view.file.isDirectory()){
    //                        openFolder();
                            model.lastOpenedFolder(view.file);
                        }
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
        });
        starButton = view.getStarButton();
        starButton.addActionListener(this::starClicked);

        model.addPropertyChangeListener(ItemModel.REFRESH_TAGS, this::tagsRefreshed);
        setStarStatus(model.isFavourite());


        setTagsMenu();
        view.newTagButton.addActionListener(this::newTagClicked);

        model.refreshTags();
    }

    private void setTagsMenu(){
        model.tagsMap.forEach((key, value) -> {
            JCheckBoxMenuItem tmp = new JCheckBoxMenuItem(key);
            tmp.addActionListener(this::tagSelected);
            view.getPopupMenu().add(tmp,0);
        });
    }

    private void addTagMenu(String name){
        JCheckBoxMenuItem tmp = new JCheckBoxMenuItem(name);
        tmp.addActionListener(this::tagSelected);
        view.getPopupMenu().add(tmp, 0);
    }

    private void newTagClicked(ActionEvent e){

    }

    private void tagSelected(ActionEvent e){
        String tmp = ((JCheckBoxMenuItem) e.getSource()).getText();
        model.newTagSelected(tmp);
    }

    private void tagsRefreshed(PropertyChangeEvent e){
        List<String> subs = (List<String>) e.getNewValue();

        for (int i = 0; i < view.getPopupMenu().getComponentCount()-2; i++) {
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

}

