package org.example;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;

public final class DatabasesUtil {

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
        List<String> toDelete = new ArrayList<>();

        try {
            while (rs.next()){
                int id = rs.getInt("id");
                int TagID = rs.getInt("TagID");
                String FilePath = rs.getString("FilePath");
                if(!(new File(FilePath)).exists()){
                    toDelete.add(FilePath);
                    continue;
                }
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
        for(String s : toDelete){
            removeFileFromTables(s);
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
        List<String> toDelete = new ArrayList<>();

        try {
            while (rs.next()){
//                int id = rs.getInt("id");
                boolean isShortcut = rs.getBoolean("isShortcut");
//                String DisplayName = rs.getString("DisplayName");
                String FolderPath = rs.getString("FolderPath");
                if(!(new File(FolderPath)).exists()){
                    toDelete.add(FolderPath);
                    continue;
                }
                if(isShortcut){
                    shortcutFiles.add(new File(FolderPath));
                }else {
                    favouriteFiles.add(new File(FolderPath));
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        for(String s : toDelete){
            removeFileFromTables(s);
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

    private static void removeFileFromTables(String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeFile(filePath);
        sql.closeConnection();
    }

    public static void removeShortcut(String displayName, String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeFromFavouriteTable(filePath, true);
        sql.closeConnection();
    }

    public static void removeFavourite(String filePath){
        SQLiteManage sql = new SQLiteManage();
        sql.removeFromFavouriteTable(filePath, false);
        favouriteFiles.remove(new File(filePath));
        sql.closeConnection();
    }

    public static void removeTagFromFile(String filePath, String tag){
        SQLiteManage sql = new SQLiteManage();
        sql.removeTagFromFile(filePath, getTagIdFromString(tag));

        List<Integer> tmp = filesTagMap.get(filePath);
        tmp.remove(tmp.indexOf(getTagIdFromString(tag)));
        if(tmp.isEmpty()){filesTagMap.remove(filePath);}
        else {filesTagMap.put(filePath, tmp);}

        List<String> tmp2 = tagIdPathMap.get(getTagIdFromString(tag));
        tmp2.remove(filePath);
        if(tmp2.isEmpty()){tagIdPathMap.remove(getTagIdFromString(tag));}
        else {tagIdPathMap.put(getTagIdFromString(tag), tmp2);}

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

    public static void  newTag(String TagName, String TagColor){
        SQLiteManage sql = new SQLiteManage();
        maxTagID++;
        sql.insertRowIntoTagInfo(TagName, TagColor, -1);
        tagsMap.put(TagName, maxTagID);
        tagsColorMap.put(TagName, TagColor);
        sql.closeConnection();
    }

    public static void deleteTag(String TagName){
        SQLiteManage sql = new SQLiteManage();
        sql.deleteTag(TagName);

        loadTagInfoData(sql.getTableContent(SQLiteManage.TAG_INFO_TABLE));
        loadTagsData(sql.getTableContent(SQLiteManage.TAG_TABLE));

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

        int base = 64; // 50% brightness
        int r = base + rand.nextInt(190);
        int g = base + rand.nextInt(190);
        int b = base + rand.nextInt(190);

        Color pastelColor = new Color(r, g, b);

        while ((colorExist(colorToHEX(pastelColor)))){
            r = base + rand.nextInt(190);
            g = base + rand.nextInt(190);
            b = base + rand.nextInt(190);
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
