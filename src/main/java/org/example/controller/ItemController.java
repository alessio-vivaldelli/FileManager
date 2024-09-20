package org.example.controller;

import org.example.Icons.StarIcon;
import org.example.MItem;
import org.example.model.ExplorerModel;
import org.example.model.ItemModel;
import org.example.view.ExplorerView;
import org.example.view.Item;

import javax.swing.*;
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
        mainButton.addMouseMotionListener(mouseHandler);

        starButton = view.getStarButton();
        starButton.addActionListener(this::starClicked);

        model.addPropertyChangeListener(ItemModel.REFRESH_TAGS, this::tagsRefreshed);
        model.addPropertyChangeListener(ItemModel.NEW_SELECTION_STATUS, this::setSelectionStatus);
        model.addPropertyChangeListener(ExplorerModel.TAG_DELETED, this::tagDeleted);
        model.addPropertyChangeListener(ExplorerModel.TAG_DELETED, this::tagDeleted);

        setStarStatus(model.isFavourite());

        setTagsMenu();
        model.refreshTags();
    }


    private void setSelectionStatus(PropertyChangeEvent e){
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

    public Item getItem(){return view;}

    public class MouseHandlerClass implements MouseMotionListener, MouseListener {
        public boolean isDragging = false;
        public Point startingPoint;

        private boolean isOut = false;
        private Icon fileIcon;

        private final double draggingSensibility = 20;
        private Point initDrag = null;
        private double initDistance;

        private double getDistange(Point a, Point b){return Point.distance(a.x, a.y, b.x, b.y);}

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                if (view.file.isDirectory()){
                    model.lastOpenedFolder(view.file);
                }
            }
            if(SwingUtilities.isLeftMouseButton(e)) {
                model.itemSelected(e.isControlDown());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if(initDrag == null){
                initDrag = e.getPoint();
            }else if(!isDragging){
                initDistance += getDistange(initDrag, e.getPoint());
                initDrag = e.getPoint();
            }
            if(initDistance >= draggingSensibility) {
                if (!isDragging && !isOut) {
                    startingPoint = e.getPoint();
                    isDragging = true;
                    fileIcon = view.getIcon();

                    model.dragItem(e.getLocationOnScreen(), fileIcon);
                } else if (!isOut) {
                    model.dragItem(e.getLocationOnScreen(), fileIcon);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(isDragging){
                isDragging = false;
                model.stopDrag();

                initDrag = null;
                initDistance = 0;

                // notify ExplorerController that dragging is finished
                // used for drag and drop actions
                model.dragFinish(e.getLocationOnScreen());
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

    }
}

