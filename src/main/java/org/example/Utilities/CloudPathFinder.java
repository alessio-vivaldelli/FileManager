package org.example.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CloudPathFinder {
    private static List<String> cloudsPath;

    public static List<String> getCloudPaths() {

        cloudsPath = new ArrayList<>();

        String userHome = System.getProperty("user.home");

        String oneDrivePath = userHome + "\\OneDrive";
        String googleDrivePath = userHome + "\\Google Drive";
        String dropboxPath = userHome + "\\Dropbox";

        if((new File(oneDrivePath)).exists()){
            cloudsPath.add(oneDrivePath);
//            System.out.println("One drive Path: " + oneDrivePath);
        }
        if((new File(googleDrivePath)).exists()){
            cloudsPath.add(googleDrivePath);
//            System.out.println("Google Drive Path: " + googleDrivePath);
        }
        if((new File(dropboxPath)).exists()){
            cloudsPath.add(dropboxPath);
//            System.out.println("Drop Box Path: " + dropboxPath);
        }
        return cloudsPath;
    }


}