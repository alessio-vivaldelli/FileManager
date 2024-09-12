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

            if (!(new File(appDataPath + "/").mkdirs())){System.out.println("Error on creating database folder");}

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
                 TagColor integer NOT NULL,
                 TaggedElements integer NOT NULL,
                );""".formatted(SQLiteManage.TAG_INFO_TABLE);
        }else if(table.equals(SQLiteManage.TAG_TABLE)){
            sql = """
                CREATE TABLE IF NOT EXISTS %s (
                 id integer PRIMARY KEY,
                 TagID integer NOT NULL,
                 FilePath text NOT NULL,
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

    public void insertRow(String name, String email) {
        String insertSQL = "INSERT INTO users (name, email) VALUES (?,?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
        } catch (SQLException e) {
            System.out.println("Error on insert: " + e.getMessage());
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