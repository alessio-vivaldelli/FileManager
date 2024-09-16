package org.example;

import org.example.controller.ItemController;
import org.example.model.ItemModel;
import org.example.model.Model;
import org.example.view.Item;
import org.example.view.TagView;

import java.io.File;

public class MItem extends Item {
    private ItemModel model;

    public MItem(File file){
        super(file);
        init();
    }

    private void init(){
        setModel(new ItemModel(file));
        new ItemController(this, this.model);
    }

    public void setModel(ItemModel model){
        this.model = model;
    }
    public Model getModel(){
        return model;
    }



    @Override
    public String toString() {
        return file.getName();
    }
}
