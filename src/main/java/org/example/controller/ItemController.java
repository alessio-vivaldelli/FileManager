package org.example.controller;

import org.example.MItem;
import org.example.model.ExplorerModel;
import org.example.model.ItemModel;
import org.example.view.Item;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ItemController {

    private Item view;
    private ItemModel model;

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

