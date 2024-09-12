package org.example;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static List<File> shortcutFiles;
    public static List<File> favouriteFiles;

    public static Map<String, List<Integer>> filesTagMap;
    public static Map<String, Integer> tagsMap;
    public static Map<String, Integer> tagsColorMap;


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

        sql.closeConnection();
    }

    private static void loadTagInfoData(ResultSet rs){
        tagsMap = new HashMap<>();
        tagsColorMap = new HashMap<>();
        try {
            while (rs.next()){
                int id = rs.getInt("id");
                String TagName = rs.getString("TagName");
                int TagColor = rs.getInt("TagColor");
                int TaggedElements = rs.getInt("TaggedElements");

                tagsMap.put(TagName, id);
                tagsColorMap.put(TagName, TagColor);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void loadTagsData(ResultSet rs){
        filesTagMap = new HashMap<>();

        try {
            while (rs.next()){
                int id = rs.getInt("id");
                int TagID = rs.getInt("TagID");
                String FilePath = rs.getString("FilePath");

                if(filesTagMap.containsKey(FilePath)){
                    List<Integer> tmp = filesTagMap.get(FilePath);
                    tmp.add(TagID);
                    filesTagMap.put(FilePath, tmp);
                }else {
                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(TagID);
                    filesTagMap.put(FilePath, tmp);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void loadFavouritesData(ResultSet rs){

        shortcutFiles = new ArrayList<>();
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
}
