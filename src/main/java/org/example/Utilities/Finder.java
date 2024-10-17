package org.example.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class Finder
        extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private int numMatches = 0;
    private List<File> findedFiles;

    public Finder(String pattern) {
        findedFiles = new ArrayList<>();
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
    }

    public List<File> getFinderFiles(){return findedFiles;}

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numMatches++;
            findedFiles.add(file.toFile());
            System.out.println(file);
        }
    }

    // Prints the total number of
    // matches to standard out.
    public void done() {
        System.out.println("Matched: "
                + numMatches);
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}