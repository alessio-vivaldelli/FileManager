package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileSystemUtil {

    private static long fileCopyUsingNIOFilesClass(File fileToCopy, File newFile) throws IOException
    {
        Path source = Paths.get(fileToCopy.getPath());
        Path destination = Paths.get(newFile.getPath());
        long start = System.currentTimeMillis();

        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        long end = System.currentTimeMillis();

        return (end-start);
    }

    // TODO: handle when this function is called with file instead folder as source
    public static void folderCopyUsingNIOFilesClass(File folderToCopy, File newDestination) throws IOException
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
                            Files.copy(dir, targetdir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetdir))
                                throw e;
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
