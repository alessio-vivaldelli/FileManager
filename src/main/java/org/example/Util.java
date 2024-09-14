package org.example;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public final class Util {

    private static List<File> shortcutFiles;
    private static List<File> favouriteFiles;
    private static Map<String, List<Integer>> filesTagMap;
    private static Map<Integer, List<String>> tagIdPathMap;
    private static Map<String, Integer> tagsMap;
    private static Map<String, String> tagsColorMap;
    private static int maxTagID = 0;


    public static void initDatabaseData(){
        SQLiteManage sql = new SQLiteManage();
        shortcutFiles = new ArrayList<>();


        if(!(sql.checkTableExist(SQLiteManage.TAG_INFO_TABLE))){
            sql.createNewTable(SQLiteManage.TAG_INFO_TABLE);
        }
        if(!(sql.checkTableExist(SQLiteManage.TAG_TABLE))){
            sql.createNewTable(SQLiteManage.TAG_TABLE);
        }
        if(!(sql.checkTableExist(SQLiteManage.FAVOURITES_TABLE))){
            sql.createNewTable(SQLiteManage.FAVOURITES_TABLE);
        }

        loadTagInfoData(sql.getTableContent(SQLiteManage.TAG_INFO_TABLE));
        loadTagsData(sql.getTableContent(SQLiteManage.TAG_TABLE));
        loadFavouritesData(sql.getTableContent(SQLiteManage.FAVOURITES_TABLE));

        if(shortcutFiles.isEmpty()){
            initShortcuts(sql);
        }

        sql.closeConnection();
    }

    private static void loadTagInfoData(ResultSet rs){
        tagsMap = new HashMap<>();
        tagsColorMap = new HashMap<>();
        try {
            while (rs.next()){
                int id = rs.getInt("id");
                String TagName = rs.getString("TagName");
                String TagColor = rs.getString("TagColor");
                int TaggedElements = rs.getInt("TaggedElements");
                maxTagID = id;
                tagsMap.put(TagName, id);
                tagsColorMap.put(TagName, TagColor);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void loadTagsData(ResultSet rs){
        filesTagMap = new HashMap<>();
        tagIdPathMap = new HashMap<>();

        try {
            while (rs.next()){
                int id = rs.getInt("id");
                int TagID = rs.getInt("TagID");
                String FilePath = rs.getString("FilePath");

                if(filesTagMap.containsKey(FilePath)){
                    List<Integer> tmp = filesTagMap.get(FilePath);
                    tmp.add(TagID);
                    filesTagMap.put(FilePath, tmp);
                }
                else {
                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(TagID);
                    filesTagMap.put(FilePath, tmp);
                }

                if(tagIdPathMap.containsKey(TagID)){
                    List<String> tmp = tagIdPathMap.get(TagID);
                    tmp.add(FilePath);
                    tagIdPathMap.put(TagID, tmp);
                }
                else {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(FilePath);
                    tagIdPathMap.put(TagID, tmp);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void loadFavouritesData(ResultSet rs){

        shortcutFiles = new ArrayList<>();
        File file = new File("Favourite"){
            @Override
            public String getName() {
                return "Favourite";
            }
        };
        shortcutFiles.add(file);
        favouriteFiles = new ArrayList<>();

        try {
            while (rs.next()){
//                int id = rs.getInt("id");
                boolean isShortcut = rs.getBoolean("isShortcut");
//                String DisplayName = rs.getString("DisplayName");
                String FolderPath = rs.getString("FolderPath");

                if(isShortcut){
                    shortcutFiles.add(new File(FolderPath));
                }else {
                    favouriteFiles.add(new File(FolderPath));
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void initShortcuts(SQLiteManage sql){

        List<File> defaultDir = new ArrayList<>();
        FileSystemView fsv = FileSystemView.getFileSystemView();

        File desktop = fsv.getHomeDirectory();
        if(desktop.exists()){defaultDir.add(desktop);}
        File documents = fsv.getDefaultDirectory();
        if(documents.exists()){defaultDir.add(documents);}
        File downloads = new File(System.getProperty("user.home") + "/Downloads");
        if(downloads.exists()){defaultDir.add(downloads);}
        File pictures = new File(System.getProperty("user.home") + "/Pictures");
        if(pictures.exists()){defaultDir.add(pictures);}

        defaultDir.forEach(file -> {
            sql.insertRowIntoFavourites(true, file.getName(), file.getPath());
        });

    }

    public static List<String> getSubsTags(File file){
        List<Integer> r = filesTagMap.get(file.getPath());
        List<String> res = new ArrayList<>();
        if(r == null){return res;}
        r.forEach( (id) -> {
            res.add(getTagStringFromID(id));
        });

        return res;
    }

    public static boolean isFileFavourite(File file){return favouriteFiles.contains(file);}

    public static void addShortcut(String displayName, String filePath){
        SQLiteManage sql = new SQLiteManage();
        shortcutFiles.add(new File(filePath));
        sql.insertRowIntoFavourites(true, "", filePath);
        sql.closeConnection();
    }

    public static void removeShortcut(String displayName, String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeFromFavouriteTable(filePath, true);
        sql.closeConnection();
    }

    public static void removeFavourite(String displayName, String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeFromFavouriteTable(filePath, false);
        sql.closeConnection();
    }

    public static void removeTagFromFile(String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeTagFromFile(filePath);
        sql.closeConnection();
    }

    public static void addFavourite(String displayName, String filePath){
        SQLiteManage sql = new SQLiteManage();
        favouriteFiles.add(new File(filePath));
        sql.insertRowIntoFavourites(false, "", filePath);
        sql.closeConnection();
    }

    public static void  newTag(String TagName){
        SQLiteManage sql = new SQLiteManage();
        maxTagID++;
        String TagColor = generateNewColor();
        sql.insertRowIntoTagInfo(TagName, TagColor, -1);
        tagsMap.put(TagName, maxTagID);
        tagsColorMap.put(TagName, TagColor);
        sql.closeConnection();
    }

    public static void newFileTag(String TagName, String FilePath){
        SQLiteManage sql = new SQLiteManage();

        sql.insertRowIntoTag(getTagIdFromString(TagName), FilePath);

        int TagID = getTagIdFromString(TagName);

        if(filesTagMap.containsKey(FilePath)){
            List<Integer> tmp = filesTagMap.get(FilePath);
            tmp.add(getTagIdFromString(TagName));
            filesTagMap.put(FilePath, tmp);
        }else {
            List<Integer> tmp = new ArrayList<>();
            tmp.add(getTagIdFromString(TagName));
            filesTagMap.put(FilePath, tmp);
        }

        if(tagIdPathMap.containsKey(TagID)){
            List<String> tmp = tagIdPathMap.get(TagID);
            tmp.add(FilePath);
            tagIdPathMap.put(TagID, tmp);
        }
        else {
            List<String> tmp = new ArrayList<>();
            tmp.add(FilePath);
            tagIdPathMap.put(TagID, tmp);
        }

        sql.closeConnection();
    }

    public static String getTagStringFromID(int tagID){
        String res = null;
        for (Map.Entry<String, Integer> entry : tagsMap.entrySet()) {
            if (entry.getValue() == tagID) {
                res = entry.getKey();
                break;
            }
        }
        return res;
    }

    public static int getTagIdFromString(String tag){return tagsMap.get(tag);}

    public static boolean tagExist(String tagName){return tagsMap.containsKey(tagName);}

    public static String getColorFromTag(String tag){return tagsColorMap.get(tag);}

    public static List<File> getShortcutData(){return shortcutFiles;}

    public static List<File> getFavouritesData(){return favouriteFiles;}

    private static boolean colorExist(String color) {
        for(Map.Entry<String, String> val : tagsColorMap.entrySet()){
            if(val.getValue().equals(color)){return true;}
        }return false;
    }

    public static String generateNewColor(){
        Random rand = new Random();

        int base = 127; // 50% brightness
        int r = base + rand.nextInt(128);
        int g = base + rand.nextInt(128);
        int b = base + rand.nextInt(128);

        Color pastelColor = new Color(r, g, b);

        while (!(colorExist(colorToHEX(pastelColor)))){
            r = base + rand.nextInt(128);
            g = base + rand.nextInt(128);
            b = base + rand.nextInt(128);
            pastelColor = new Color(r, g, b);
        }

        return colorToHEX(pastelColor);
    }

    public static String colorToHEX(Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static Map<String, List<Integer>> getFilesTagMap(){return filesTagMap;}

    public static Map<String, Integer> getTagsMap(){return tagsMap;}

    public static Map<String, String> getTagsColorMap() {return tagsColorMap;}

    public static List<File> getFilesFromTags(List<String> selectedTags){
        Set<File> res = new HashSet<>();
        selectedTags.forEach((tag) -> {
            List<String> list = tagIdPathMap.get(getTagIdFromString(tag));
            if(list != null) {
                for (String path : list) {
                    File f = new File(path);
                    res.add(f);
                }
            }
        });

        return res.stream().toList();
    }
}
