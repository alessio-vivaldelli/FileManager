package org.example;

import org.example.controller.ExplorerController;
import org.example.model.ExplorerModel;
import org.example.view.ExplorerView;

public class MExplorer extends ExplorerView {

    private ExplorerModel model;

    public MExplorer(){
        super();
        model = new ExplorerModel();
        new ExplorerController(this, model);
        getUI().requestFocusInWindow();
    }
}
