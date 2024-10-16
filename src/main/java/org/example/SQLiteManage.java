package org.example;

import java.io.File;
import java.sql.*;


public class SQLiteManage {

    public static final String TAG_TABLE = "tag";
    public static final String TAG_INFO_TABLE = "tag_info";
    public static final String FAVOURITES_TABLE = "favourite_table";

    private Connection connection;

    public SQLiteManage() {
        connect();
    }

    private void connect() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String userHome = System.getProperty("user.home");
            String appDataPath;

            if (os.contains("win")) {
                appDataPath = System.getenv("APPDATA") + "/FileManager/";
            } else if (os.contains("mac")) {
                appDataPath = userHome + "/Library/Application Support/FileManager/";
            } else {
                appDataPath = userHome + "/.local/share/FileManager/";
            }

            File dbFolder = new File(appDataPath + "/");
            if(!(dbFolder.exists())) {
                if (!(dbFolder.mkdirs())) {
                    System.out.println("Error on creating database folder");
                }
            }

            String url = String.format("jdbc:sqlite:%s/main_database.sqlite", appDataPath);
            connection = DriverManager.getConnection(url);

            System.out.println("Connessione stabilita.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createNewTable(String table) {
        String sql = null;
        if(table.equals(SQLiteManage.TAG_INFO_TABLE)){
        sql = """
                CREATE TABLE IF NOT EXISTS %s (
                 id integer PRIMARY KEY,
                 TagName text NOT NULL,
                 TagColor text NOT NULL,
                 TaggedElements integer NOT NULL
                );""".formatted(SQLiteManage.TAG_INFO_TABLE);
        }else if(table.equals(SQLiteManage.TAG_TABLE)){
            sql = """
                CREATE TABLE IF NOT EXISTS %s (
                 id integer PRIMARY KEY,
                 TagID integer NOT NULL,
                 FilePath text NOT NULL
                );""".formatted(SQLiteManage.TAG_TABLE);
        }else if(table.equals(SQLiteManage.FAVOURITES_TABLE)){
            sql = """
                CREATE TABLE IF NOT EXISTS %s (
                 id integer PRIMARY KEY,
                 isShortcut Boolean NOT NULL,
                 DisplayName text,
                 FolderPath text NOT NULL
                );""".formatted(SQLiteManage.FAVOURITES_TABLE);
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabella creata.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertRowIntoFavourites(boolean isShortcut, String displayName, String filePath) {
        String insertSQL = "INSERT INTO %s (isShortcut, DisplayName, FolderPath) VALUES (?,?,?);".formatted(SQLiteManage.FAVOURITES_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setBoolean(1, isShortcut);
            preparedStatement.setString(2, displayName);
            preparedStatement.setString(3, filePath);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on insert favourite row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void insertRowIntoTagInfo(String TagName, String TagColor, int TaggedElements) {
        String insertSQL = "INSERT INTO %s (TagName, TagColor, TaggedElements) VALUES (?,?,?)".formatted(SQLiteManage.TAG_INFO_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, TagName);
            preparedStatement.setString(2, TagColor);
            preparedStatement.setInt(3, TaggedElements);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on insert favourite row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void insertRowIntoTag(int TagID, String FilePath) {
        String insertSQL = "INSERT INTO %s (TagID, FilePath) VALUES (?,?);\n".formatted(SQLiteManage.TAG_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, TagID);
            preparedStatement.setString(2, FilePath);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on insert favourite row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void removeFile(String filePath){
        String insertSQL = "DELETE FROM %s WHERE FilePath = ?;".formatted(SQLiteManage.TAG_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, filePath);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on delete file path:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
        insertSQL = "DELETE from %s where  FolderPath = ?;".formatted(SQLiteManage.FAVOURITES_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, filePath);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on delete file path:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void removeFromFavouriteTable(String filePath, boolean isShortcut){
        String insertSQL = "DELETE FROM %s WHERE FolderPath = ? AND isShortcut = ?;".formatted(SQLiteManage.FAVOURITES_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, filePath);
            preparedStatement.setBoolean(2, isShortcut);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on insert favourite row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void removeTagFromFile(String filePath, int tagID){
        String insertSQL = "DELETE FROM %s WHERE FilePath = ? AND TagID = ?;".formatted(SQLiteManage.TAG_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, filePath);
            preparedStatement.setInt(2, tagID);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on deleting row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
    }

    public void deleteTag(String tagName){
        String insertSQL = "DELETE FROM %s WHERE TagName = ?;".formatted(SQLiteManage.TAG_INFO_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, tagName);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on deleting row:\n" + insertSQL);
            System.out.println(e.getMessage());
        }
        String insertSQL_2 = "DELETE FROM %s WHERE TagID = ?;".formatted(SQLiteManage.TAG_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL_2)) {
            preparedStatement.setInt(1, DatabasesUtil.getTagIdFromString(tagName));
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error on deleting row:\n" + insertSQL_2);
            System.out.println(e.getMessage());
        }
    }
    public boolean checkTableExist(String name) {
        String checkSQL = "SELECT * FROM sqlite_master WHERE type='table' AND name=?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkSQL)) {
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error on checkTableExist: " + e.getMessage());
        }
        return false;
    }

    public ResultSet getTableContent(String table){
        String sql = "SELECT * FROM %s".formatted(table);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}