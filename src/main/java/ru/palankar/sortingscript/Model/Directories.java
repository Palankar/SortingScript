package ru.palankar.sortingscript.Model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Директории, по которым перемещаются файлы.
 * Задаются через .properties файл.
 */
public class Directories {
    private static Directories instance;
    private String unsortedDirectory;
    private String sortedDirectory;

    private Directories() { }

    public static Directories getInstance() {
        if (instance == null) {
            instance = new Directories();
        }
        return instance;
    }

    public Path getUnsortedDirectory() {
        return Paths.get(unsortedDirectory);
    }

    public void setUnsortedDirectory(String unsortedDirectory) {
        this.unsortedDirectory = unsortedDirectory;
    }

    public Path getSortedDirectory() {
        return Paths.get(sortedDirectory);
    }

    public void setSortedDirectory(String sortedDirectory) {
        this.sortedDirectory = sortedDirectory;
    }

}
