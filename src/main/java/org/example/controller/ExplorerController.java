package org.example.controller;

import org.example.FileSystemUtil;
import org.example.Layout.WrapLayout;
import org.example.MItem;
import org.example.DatabasesUtil;
import org.example.Utilities.CloudPathFinder;
import org.example.Utilities.Finder;
import org.example.model.ItemModel;
import org.example.ShortcutItem;
import org.example.model.ExplorerModel;
import org.example.view.ExplorerView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.formdev.flatlaf.extras.*;

/**
 * Controller class for the Explorer view.
 * Manages interactions between the ExplorerView and ExplorerModel.
 */
public class ExplorerController {

    ExplorerView view;
    ExplorerModel model;
    JTabbedPane leftShortcut;
    JTree tree;
    private boolean isFocused = true;
    private JFrame frame;
    private List<MItem> itemList;
    private boolean isLoadingFolder = false;
    private List<String> selectedTags;

    private Rectangle draggingRectangle;

    private boolean isDraggingDir = false;
    private boolean isDraggingMulti = false;
    private List<MItem> textFilteredItems;

    private JButton navigationButtons;
    private JTextField navigationSearchField;
    private JTextField navigationPathField;
    private boolean isPathTextMode = false;
    private MouseListener mouseListener;

    /**
     * Constructor for ExplorerController.
     * Initializes the view and model, and sets up the controller.
     *
     * @param view  the ExplorerView instance
     * @param model the ExplorerModel instance
     */
    public ExplorerController(ExplorerView view, ExplorerModel model){
        this.view = view;
        this.model = model;

        itemList = new ArrayList<>();
        selectedTags = new ArrayList<>();
        initController();
        model.initModel();
    }

    /**
     * Initializes the controller by setting up event listeners and refreshing lists.
     */
    private void initController() {

        textFilteredItems = new ArrayList<>();
        draggingRectangle = null;

        leftShortcut = view.getShortcutTabs();
        leftShortcut.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

        model.addPropertyChangeListener(ExplorerModel.NEW_SHORTCUT, this::newShortcutTab);
        model.addPropertyChangeListener(ExplorerModel.TREE_INIT, this::treeInitialized);
        model.addPropertyChangeListener(ExplorerModel.NEW_ITEM, this::newItem);

        model.addPropertyChangeListener(ExplorerModel.SELECTION_UPDATE, this::selectionListUpdate);
        model.addPropertyChangeListener(ExplorerModel.REMOVE_ALL_SELECTION, this::clearSelection);
        model.addPropertyChangeListener(ExplorerModel.DESELECT_ITEM, this::deselectItem);


        tree = view.getTree();
        DefaultMutableTreeNode root = model.createTree(new File("This Computer"));

        //TODO: decidere se tenere il tree
//        for (File file : File.listRoots()){
//            System.out.println(file.getPath());
//            root.add(model.createTree(file));
//        }
//        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        tree.addTreeSelectionListener(this::treeSelectionChange);

        MouseHandlerClass handlerClass = new MouseHandlerClass();

        view.fileView_p.addMouseMotionListener(handlerClass);
        view.fileView_p.addMouseListener(handlerClass);
        view.fileView_p.addFocusListener(handlerClass);
        view.fileView_p.addKeyListener(handlerClass);

        refreshTagsList();
        refreshShortcutList();
        refreshDisksList();
        refreshCloudList();

        view.getNewTagText().addActionListener(this::newTagConfirmed);
        view.getNewTagText().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(model.isTagDuplicated(view.getNewTagText().getText() + e.getKeyChar())){
                    System.out.println("DUPLICATED");
                    view.getNewTagText().setForeground(Color.RED);
                }else {
                    view.getNewTagText().setForeground((Color) UIManager.get("TextField.foreground"));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        navigationPathField = view.getNavigationPathTextField();
        navigationButtons = view.getNavigationButtons();
        navigationSearchField = view.getNavigationSearchField();

        navigationButtons.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleNavigationBarMode();
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

        navigationPathField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER) {
                    File fileText = (new File(navigationPathField.getText()));
                    if(fileText.exists() && fileText.isDirectory()){
                        navigationPathField.setFocusable(false);
                        navigationPathField.setFocusable(true);
                        showFolder(fileText);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        navigationPathField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                toggleNavigationBarMode();
            }
        });

        navigationSearchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    if(navigationSearchField.getText().isEmpty()){return;}
                    Finder finder = new Finder(navigationSearchField.getText());
                    try {
                        Files.walkFileTree(model.getOpenedFolder().toPath(), finder);
                        finder.done();
                        List<File> res = finder.getFinderFiles();
                        showFilesList(res, null);
                        navigationSearchField.setText("");
                        navigationSearchField.setFocusable(false);
                        navigationSearchField.setFocusable(true);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void toggleNavigationBarMode(){
        isPathTextMode = !isPathTextMode;
        if(isPathTextMode){
            navigationButtons.removeAll();
            navigationButtons.setLayout(new BorderLayout());
            navigationButtons.add(navigationPathField, BorderLayout.CENTER);
            navigationPathField.requestFocus(true);
            navigationButtons.revalidate(); navigationButtons.repaint();
            mouseListener = navigationButtons.getMouseListeners()[0];
        }else{
            navigationButtons.removeAll();
            navigationButtons.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
            navigationButtons.revalidate(); navigationButtons.repaint();
            navigationButtons.addMouseListener(mouseListener);
            resetNavigationPathComponents();
            setNavigationPathComponents(model.getOpenedFolder());
        }
    }

    /**
     * Deselects an item.
     *
     * @param e the PropertyChangeEvent containing the item to deselect
     */
    private void deselectItem(PropertyChangeEvent e){
        ((ItemModel) e.getNewValue()).setSelected(false);
    }

    /**
     * Clears the selection of items.
     *
     * @param e the PropertyChangeEvent containing the list of items to clear selection
     */
    private void clearSelection(PropertyChangeEvent e){
        List<ItemModel> l = (List<ItemModel>) e.getNewValue();
        l.forEach(elem -> {
            elem.setSelected(false);
        });
    }

    // TODO: put on screen the number of selected items
    /**
     * Updates the selection list.
     *
     * @param e the PropertyChangeEvent containing the list of selected items
     */
    private void selectionListUpdate(PropertyChangeEvent e){
        List<ItemModel> list = (List<ItemModel>) e.getNewValue();
        if(list != null){
            list.forEach(elem -> {
                elem.setSelected(true);
            });
        }
    }

    /**
     * Confirms the creation of a new tag.
     *
     * @param e the ActionEvent triggered by the new tag text field
     */
    private void newTagConfirmed(ActionEvent e){
        JTextField field = ((JTextField)e.getSource());
        String newTag = field.getText();

        if(model.isTagDuplicated(newTag)){return;}
        field.setText("");
        field.setFocusable(false); field.setFocusable(true);

        model.newTag(newTag, view.getNewTagColor());
        newTag(newTag, view.getNewTagColor());
    }

    /**
     * Refreshes the list of tags.
     */
    private void refreshTagsList(){

        for (Map.Entry<String, String> elem : DatabasesUtil.getTagsColorMap().entrySet()){
            newTag(elem.getKey(), elem.getValue());
        }
    }

    /**
     * Add tag to left panel with filtering feature by clicking it.
     * Object will be added with mouse listener implemented, on 3rd mouse button
     * tag will be deleted
     * @param tagName tag name
     * @param tagColor tag color
     */
    private void newTag(String tagName, String tagColor){
        JToggleButton tmp = view.addTagItem(tagName, tagColor);
        tmp.addActionListener(this::tagFilterChanges);
        tmp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){

                }else if(SwingUtilities.isMiddleMouseButton(e)){
                    view.removeTagElement(tagName);
                    model.deleteTag(tagName);
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

    /**
     * Handles changes in tag filters.
     *
     * @param e the ActionEvent triggered by the tag filter change
     */
    private void tagFilterChanges(ActionEvent e){
        JToggleButton source = (JToggleButton) e.getSource();
        if(source.isSelected()){
            selectedTags.add(source.getText());
        }else {
            selectedTags.remove(source.getText());
        }
        showFilesList(DatabasesUtil.getFilesFromTags(selectedTags), null);
    }

    /**
     * Shows the list of favorite files.
     *
     * @param e the ActionEvent triggered by the favorites button
     */
    private void showFavourites(ActionEvent e){
        showFilesList(DatabasesUtil.getFavouritesData(), "Favourite");
    }

    /**
     * Refreshes the list of shortcuts.
     */
    private void refreshShortcutList(){
        DatabasesUtil.getShortcutData().forEach((elem) -> {
            JButton tmp = view.addShortcutItem(elem);
            tmp.addActionListener(this::handleLeftPanelClicks);
        });
    }

    /**
     * Adds a shortcut item to the view.
     *
     * @param f the file to add as a shortcut item
     */
    private void addShortcutItem(File f){
        JButton tmp = view.addShortcutItem(f);
        tmp.addActionListener(this::handleLeftPanelClicks);
    }

    /**
     * Refreshes the list of disks.
     */
    private void refreshDisksList(){
        for(File file : File.listRoots()){
            JButton tmp = view.addDiskItem(file);
            tmp.addActionListener(this::handleLeftPanelClicks);
        }
    }

    /**
     * Refreshes the list of cloud paths.
     */
    private void refreshCloudList(){
        for(String file : CloudPathFinder.getCloudPaths()){
            JButton tmp = view.addCloudItem(new File(file));
            tmp.addActionListener(this::handleLeftPanelClicks);
        }
    }

    /**
     * Handles clicks on the left panel.
     *
     * @param e the ActionEvent triggered by the left panel click
     */
    private void handleLeftPanelClicks(ActionEvent e){
        String path = e.getSource().toString();
        File f = new File(path);
        if(f.equals(model.getOpenedFolder())){return;}
        if(path.equals("Favourite")){
            showFilesList(DatabasesUtil.getFavouritesData(), "Favourite");
            return;
        }
        if(isLoadingFolder){return;}
        showFolder(f);
    }

    /**
     * Filters items by text.
     *
     * @param filterText the text to filter items by
     * @return true if items are found, false otherwise
     */
    private List<MItem> filterItemsByText(String filterText){
        List<MItem> res = itemList.stream()
                .filter(item -> item.toString().toLowerCase().startsWith(filterText)).toList();
        return res;
    }

    /**
     * Handles the creation of a new item.
     *
     * @param e the PropertyChangeEvent containing the new item
     */
    private void newItem(PropertyChangeEvent e){
        showFolder((File) e.getNewValue());
    }


    /**
     * Handles the creation of a new shortcut tab.
     *
     * @param e the PropertyChangeEvent containing the new shortcut item
     */
    private void newShortcutTab(PropertyChangeEvent e){
        ShortcutItem item = (ShortcutItem) e.getNewValue();


        leftShortcut.addTab(item.name, new FlatSVGIcon( "icons/docs.svg",
                ExplorerView.ICON_SIZE, ExplorerView.ICON_SIZE ), item);
    }

    /**
     * Handles the initialization of the tree.
     *
     * @param e the PropertyChangeEvent containing the new tree root
     */
    private void treeInitialized(PropertyChangeEvent e){
        DefaultTreeModel s = new DefaultTreeModel((DefaultMutableTreeNode) e.getNewValue());
        view.getTree().setModel(s);
    }

    /**
     * Handles changes in tree selection.
     *
     * @param e the TreeSelectionEvent triggered by the tree selection change
     */
    private void treeSelectionChange(TreeSelectionEvent e){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
        File f = (File) (node.getUserObject());
        showFolder(f);
    }

    private void setNavigationPathComponents(File folder){
        navigationPathField.setText(folder.getPath());
        File parent = folder;
        String prt = (parent.getName().isEmpty()) ? parent.getPath() : parent.getName();
        addNavigationPathButton(prt, parent);
        while(parent != null){
            try {
                parent = parent.getParentFile();
                prt = (parent.getName().isEmpty()) ? parent.getPath() : parent.getName();
                addNavigationPathButton(prt, parent);
            } catch (Exception e) {
                break;
            }
        }
        view.checkNavigationDimension();
    }

    private void addNavigationPathButton(String name, File path){
        JButton button = view.addPathButton(name, path);
        if(button == null) {return;}
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(model.getOpenedFolder() != path) {
                    showFolder(path);
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

    private void resetNavigationPathComponents(){
        view.clearPathButtons();
    }

    /**
     * Shows the contents of a folder.
     *
     * @param f the folder to show
     */
    private void showFolder(File f){
        if(f == null){return;}
        resetNavigationPathComponents();
        setNavigationPathComponents(f);

        model.setOpenedFolder(f);
        view.fileView_p.removeAll();
        view.fileView_p.repaint();
        view.fileView_p.revalidate();

        itemList = new ArrayList<>();
        isLoadingFolder = true;

        SwingWorker<Void, MItem> worker = new SwingWorker<Void, MItem>() {
            @Override
            public Void doInBackground() {
                for (File a : Arrays.stream(f.listFiles()).sorted(new ExplorerModel.CompareItems()).toList())
                {
                    if(a == null){continue;}
                    MItem newItem = createMItem(a);
                    publish(newItem);
                }
                return null;
            }

            @Override
            protected void process(List<MItem> chunks) {
                for(MItem a : chunks){
                    view.addItem(a);
                    itemList.add(a);
                }
            }

            @Override
            protected void done() {
                isLoadingFolder = false;
                return;
            }
        };
        worker.execute();
    }

    /**
     * Shows the list of files.
     *
     * @param files the list of files to show
     */
    private void showFilesList(List<File> files, String listName){
        resetNavigationPathComponents();

        model.setOpenedFolder((listName != null) ? new File(listName) : null);
        view.fileView_p.removeAll();
        view.fileView_p.repaint();
        view.fileView_p.revalidate();
        if(files.isEmpty()){return;}


        itemList = new ArrayList<>();
        isLoadingFolder = true;

        SwingWorker<Void, MItem> worker = new SwingWorker<Void, MItem>() {
            @Override
            public Void doInBackground() {
                for (File a : files)
                {
                    if(a == null){continue;}
                    MItem newItem = createMItem(a);
                    publish(newItem);
                }
                return null;
            }

            @Override
            protected void process(List<MItem> chunks) {
                for(MItem a : chunks){
                    view.addItem(a);
                    itemList.add(a);
                }
            }

            @Override
            protected void done() {
                isLoadingFolder = false;
                return;
            }
        };
        worker.execute();
    }

    /**
     * Creates a new MItem for a given file.
     *
     * @param f the file to create an MItem for
     * @return the created MItem
     */
    private MItem createMItem(File f){
        MItem newItem = new MItem(f);
        newItem.getModel().subscribeToModel(model);
        addItemListener(newItem);
        return newItem;
    }

    /**
     * Adds listeners to an MItem.
     *
     * @param item the MItem to add listeners to
     */
    private void addItemListener(MItem item){
        item.getModel().addPropertyChangeListener(ItemModel.NEW_ITEM, this::newItem);
        item.getModel().addPropertyChangeListener(ItemModel.ITEM_SELECTED, this::newSelection);
        item.getModel().addPropertyChangeListener(ItemModel.ITEM_DRAGGING, this::handleItemDragging);
        item.getModel().addPropertyChangeListener(ItemModel.DRAGGING_FINISHED, this::handleDraggingFinish);
    }

    /**
     * Handles item dragging events.
     *
     * This method is triggered when an item is being dragged. It updates the view
     * to reflect the dragging state, including painting the dragged item and checking
     * for overlap with other items.
     *
     * @param e the PropertyChangeEvent containing the dragging information
     */
    private void handleDraggingFinish(PropertyChangeEvent e){
        ItemModel res = model.getDifferenceBetweenSelectedAndDraggingItems();
        Point mousePosition = (Point) e.getNewValue();
        SwingUtilities.convertPointFromScreen(mousePosition ,view.getUI());
        if(res != null){
            System.out.println("Coping: ");
            model.getDraggingItems().forEach(eelem -> System.out.print(eelem.getFile() + ", "));
            System.out.println("In: " + res.getFile());
            FileSystemUtil.moveItems(model.getDraggingItems().stream()
                    .map(ItemModel::getFile).collect(Collectors.toList()), res.getFile());

            refreshView();
        }else if (rectOverlap(new Rectangle(view.shortcutListBar.getX(), view.shortcutListBar.getY(),
                        view.shortcutListBar.getWidth(), view.shortcutListBar.getHeight()),
                        new Rectangle(mousePosition.x, mousePosition.y, 1,1)) && view.isDraggingDir){
            model.getDraggingItems().forEach(elem -> {
                if(!DatabasesUtil.getShortcutData().contains(elem.getFile())) {
                    DatabasesUtil.addShortcut(elem.getFile().getName(), elem.getFile().getPath());
                    addShortcutItem(elem.getFile());
                }
            });
        }

        model.clearSelection();
    }

    /**
     * Refreshes the view by reloading the contents of the currently opened folder.
     *
     * This method triggers an ActionEvent to simulate a click on the left panel,
     * causing the view to refresh and display the contents of the currently opened folder.
     */
    public void refreshView(){
        File f = model.getOpenedFolder();
        ActionEvent e = new ActionEvent((Object) f, -1, "");
        model.setOpenedFolder(null);
        handleLeftPanelClicks(e);
    }

    /**
     * Handles item dragging events.
     *
     * This method is triggered when an item is being dragged. It updates the view
     * to reflect the dragging state, including painting the dragged item and checking
     * for overlap with other items.
     *
     * @param e the PropertyChangeEvent containing the dragging information
     */
    private void handleItemDragging(PropertyChangeEvent e){
        if(e.getNewValue() == null){
            view.stopPainDraggedItem();
        }else {
            if(!view.isDraggingItem) {
                if(!model.getSelectedItems().contains((ItemModel) e.getSource())){model.clearSelection();}
                model.setItemSelected(true, (ItemModel) e.getSource());
                model.setDraggingItems();
                isDraggingDir = !model.getDraggedType();
                isDraggingMulti = model.getDraggingItems().size() >  1;
            }
            Point mousePosition = (Point) e.getNewValue();
            SwingUtilities.convertPointFromScreen(mousePosition, view.getUI());
            view.paintDraggedItem(mousePosition, (Icon) e.getOldValue(), isDraggingDir, isDraggingMulti);
            Point test = SwingUtilities.convertPoint(view.getUI(), mousePosition, view.fileView_p);
            checkOverlapInDragItem(new Rectangle(test.x, test.y, 1,1));
        }
    }
    // can be used also for item draggin by putting rectangle to -> mouse.x, mouse.y, 1, 1
    private void checkOverlapInDragItem(Rectangle selectionRect){
        for (int i = 0; i < view.fileView_p.getComponentCount(); i++) {
            MItem item = (MItem) view.fileView_p.getComponent(i);
            Rectangle itemRec = new Rectangle(item.getX(), item.getY(), item.getWidth(), item.getHeight());
            if(rectOverlap(selectionRect, itemRec)){
                model.setItemSelected(true,(ItemModel) item.getModel());
            }else if(!model.getDraggingItems().contains((ItemModel) item.getModel())){
                model.setItemSelected(false, (ItemModel) item.getModel());
            }
        }
    }
    // can be used also for item draggin by putting rectangle to -> mouse.x, mouse.y, 1, 1
    private void checkOverlapInDragSelection(Rectangle selectionRect){
        for (int i = 0; i < view.fileView_p.getComponentCount(); i++) {
            MItem item = (MItem) view.fileView_p.getComponent(i);
            Rectangle itemRec = new Rectangle(item.getX(), item.getY(), item.getWidth(), item.getHeight());
            if(rectOverlap(selectionRect, itemRec)){
                model.setItemSelected(true,(ItemModel) item.getModel());
            }else{
                model.setItemSelected(false, (ItemModel) item.getModel());
            }
        }
    }

    /**
     * Handles new item selection.
     *
     * @param e the PropertyChangeEvent containing the new selection
     */
    private void newSelection(PropertyChangeEvent e){
        boolean isControlDown = (boolean) e.getOldValue();
        ItemModel fileModel = (ItemModel) e.getNewValue();
        model.newSelection(fileModel, isControlDown);
    }

    private static boolean rectOverlap(Rectangle z, Rectangle r){
        return z.x < r.x + r.width && z.x + z.width > r.x &&
                z.y < r.y + r.height && z.y + z.height > r.y;
    }

    public void focusToSelectionIndex(int index){
        if(index == -1){return;}
        // Focus scroll bar to selected item
        int heightScroll = ( (WrapLayout) view.fileView_p.getLayout()).getItemHeightFromIndex(index);
        view.fileView.getVerticalScrollBar().setValue(heightScroll);
    }

    private int getIndexFromMItem(MItem item){
        return Arrays.asList(view.fileView_p.getComponents()).indexOf(item);
    }


    private MItem getMItemFromSelectionIndex(int index){
        if(model.getSelectedItems().isEmpty()){
            return (MItem) view.fileView_p.getComponent(0);
        }
        for (int i = 0; i < view.fileView_p.getComponentCount(); i++){
            MItem item = (MItem) view.fileView_p.getComponent(i);
            if(((ItemModel) item.getModel()) == model.getSelectedItems().get(index)){
                return item;
            }
        }
        return null;
    }

    /**
     * Inner class to handle mouse events.
     */
    public class MouseHandlerClass implements MouseMotionListener, MouseListener, FocusListener, KeyListener {
        public boolean isDragging = false;
        public Point startingPoint;

        private boolean isOut = false;

        public MouseHandlerClass(){}

        @Override
        public void mouseDragged(MouseEvent e) {
            if(!isDragging && !isOut){
                startingPoint = e.getPoint();
                isDragging = true;
                model.clearSelection(); // clear file selection before when dragging is detected
            }
            else if(!isOut) {
                int x, y, width, height;
                x = startingPoint.x;
                y = startingPoint.y;
                width = e.getPoint().x - startingPoint.x;
                height = e.getPoint().y - startingPoint.y;
                if(width < 0 && height  > 0){
                    width = Math.abs(width);
                    x = startingPoint.x-width;
                }else if(width < 0 && height < 0){
                    height = Math.abs(height);
                    y = startingPoint.y-height;
                    width = Math.abs(width);
                    x = startingPoint.x-width;
                }else if(width > 0 && height  < 0){
                    height = Math.abs(height);
                    y = startingPoint.y-height;
                }
                Rectangle drawRec = new Rectangle(x,y,width,height);
                view.drawRectangle(drawRec);

                checkOverlapInDragSelection(drawRec);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            model.clearSelection();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            view.fileView_p.requestFocusInWindow();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(isDragging){
                isDragging = false;
                view.stopDawRectangle();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            isOut = false;
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {
//            model.clearSelection();
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();

            switch (keyChar){
                case KeyEvent.VK_ENTER:
                    ItemModel itemModel = model.getSelectedItems().get(0);
                    if(itemModel != null) {
                        if(itemModel.getFile().isDirectory()){
                            showFolder(itemModel.getFile());
                        }else {
                            try {
                                Desktop.getDesktop().open(itemModel.getFile());
                            } catch (IOException ex) {
                                System.out.println("Error on opening file:");
                                ex.printStackTrace();
                            }
                        }
                    }
                    model.clearSelection();
                    return;
                case KeyEvent.VK_BACK_SPACE:
                    Path opened = model.getOpenedFolder().toPath();
                    showFolder(opened.getParent().toFile());
                    return;

                case KeyEvent.VK_KP_DOWN:
                    System.out.println("Down");
                    return;

                case KeyEvent.VK_SPACE:
                    if(model.getSelectedItems().size() == 1 && textFilteredItems != null) {
                        int index = textFilteredItems.indexOf(textFilteredItems.stream()
                                .filter(elem -> ((ItemModel) elem.getModel()).getFile()
                                        .equals(model.getSelectedItems().get(0).getFile())).toList().get(0));

                        if(index == -1){return;}
                        ItemModel newSelection = (ItemModel) textFilteredItems.get((index+1) % textFilteredItems.size())
                                    .getModel();
                        if(newSelection == null){return;}
                        model.clearSelection();
                        model.setItemSelected(true, newSelection);
                        focusToSelectionIndex(
                                getIndexFromMItem(textFilteredItems.get((index+1) % textFilteredItems.size())));
                    }
                    return;
                case KeyEvent.VK_ESCAPE:
                    view.searchText.reset();
            }

            if((keyChar < 'a' || keyChar > 'z') &&
                    (keyChar < 'A' || keyChar > 'Z') &&
                    (keyChar < '0' || keyChar > '9')){
                return;
            }

            textFilteredItems = filterItemsByText(view.searchText.getText().toLowerCase() + keyChar);

            view.searchText.exist(!textFilteredItems.isEmpty());
            view.searchText.addChar(keyChar);
            if(textFilteredItems.isEmpty()){
                return;
            }

            focusToSelectionIndex(getIndexFromMItem(textFilteredItems.get(0)));
            model.clearSelection();
            model.setItemSelected(true,(ItemModel) textFilteredItems.get(0).getModel());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() >= 37 && e.getKeyCode() <= 40){
                int oldIndex = getIndexFromMItem(getMItemFromSelectionIndex(0));
                int newIndex = ((WrapLayout) view.fileView_p.getLayout()).
                        getNewIndexBasedOnDirection(oldIndex, e.getKeyCode());
                if(newIndex == -1){return;}
                model.clearSelection();
                model.setItemSelected(true,(ItemModel) ((MItem) view.fileView_p.getComponent(newIndex)).getModel());
                focusToSelectionIndex(newIndex);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }


}
