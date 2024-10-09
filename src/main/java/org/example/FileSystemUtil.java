package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileSystemUtil {

    public static void copyFolder(File folderSrc, File folderDest) throws IOException {
        folderCopyUsingNIOFilesClass(folderSrc, folderDest);
    }

    public static boolean deleteItem(File item){
        boolean deleteRes;
        if(item.isDirectory()){
            deleteRes = deleteFolder(item);
        }else {
            deleteRes = item.delete();
        }
        System.out.println("Result of deleting: " + deleteRes);
        return deleteRes;
    }

    private static boolean deleteFolder(File folder) {
    if (folder.isDirectory()) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFolder(file);
            }
        }
    }
    return folder.delete();
}

    public static void moveItems(List<File> items, File destination){
        List<File> fileToDelete = new ArrayList<>();

        for(File file : items){
            if(file.isDirectory()) {
                try {
                    folderCopyUsingNIOFilesClass(file, destination);
                    fileToDelete.add(file);
                } catch (IOException e) {
                    System.out.println("Error on coping: " + file);
                    e.printStackTrace();
                }
            }else {
                try {
                    fileCopyUsingNIOFilesClass(file, destination);
                    fileToDelete.add(file);
                } catch (IOException e) {
                    System.out.println("Error on item coping");
                    e.printStackTrace();
                }
            }
        }
        for (File item : fileToDelete){
            deleteItem(item);
        }
    }

    // TODO: handle duplicated names in copy
    public static void copyItems(ArrayList<File> items, File destination){

        for(File file : items){
            if(file.isDirectory()) {
                try {
                    folderCopyUsingNIOFilesClass(file, destination);
                } catch (IOException e) {
                    System.out.println("Error on coping: " + file);
                    e.printStackTrace();
                }
            }else {
                try {
                    fileCopyUsingNIOFilesClass(file, destination);
                } catch (IOException e) {
                    System.out.println("Error on item coping");
                    e.printStackTrace();
                }
            }
        }
    }

    private static void fileCopyUsingNIOFilesClass(File fileSrc, File fileDest) throws IOException {
        Path copied =  Paths.get(fileDest.getPath()).resolve(fileSrc.getName());
        Path originalPath = Paths.get(fileSrc.getPath());
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    }


    private static void folderCopyUsingNIOFilesClass(File folderToCopy, File newDestination) throws IOException
    {
        Path source = Paths.get(folderToCopy.getPath());

        File dest = new File(newDestination.getPath() + File.separator + folderToCopy.getName());
        if(!(dest.exists())) {
            if (!(dest.mkdirs())) {
                throw new IOException("Error on creating destination folder");
            }
        }
        Path target = Paths.get(dest.getPath());

        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Path targetdir = target.resolve(source.relativize(dir));
                        try {
                            // If the file/folder already exist, first delete old one then copy
                            if((targetdir.toFile()).exists()){ deleteItem(targetdir.toFile()); }
                            Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetdir))
                                throw new IOException("Error on file");
                        }
                        return CONTINUE;
                    }
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Files.copy(file, target.resolve(source.relativize(file)));
                        return CONTINUE;
                    }
                });
    }
}
