package ru.palankar.sortingscript.Service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.palankar.sortingscript.Model.Directories;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class DirectoryServiceImpl implements DirectoryService {
    private Logger logger;
    private Directories directories;

    public DirectoryServiceImpl() {
        logger = LogManager.getLogger(DirectoryServiceImpl.class);
        directories = Directories.getInstance();
    }

    /**
     * Конструктор, вызываемый, когда требуется мгновенная инициализация
     * @param   properties  путь до property-файла
     */
    public DirectoryServiceImpl(String properties) {
        logger = LogManager.getLogger(DirectoryServiceImpl.class);
        directories = Directories.getInstance();
        init(properties);
    }
    /**
     * Инициалиизация директорий из .properties файла
     * Файл обязательно должен лежать в одной директории со скриптом
     */
    @Override
    public void init(String properties) {
        try {
            logger.info("Initializing properties...");
            Properties prop = new Properties();
            prop.load(new FileInputStream(properties));

            setUnsortedDirectory(prop.getProperty("UnsortedDirectory"));
            logger.info("Direction property " + prop.getProperty("UnsortedDirectory") + " initialised");
            setSortedDirectory(prop.getProperty("SortedDirectory"));
            logger.info("Direction property " + prop.getProperty("SortedDirectory") + " initialised");
        } catch (IOException e) {
            logger.error("PROPERTY FILE directories.properties COULD NOT FOUND");
            System.exit(0);
            // TODO: 30.07.2019 Пока что все коды возврата на 0, потому что логирую. Потом разобраться и подобрать
        }
        logger.info("Initializing complete");
    }

    @Override
    public Path getUnsortedDirectory() {
        return directories.getUnsortedDirectory();
    }

    @Override
    public void setUnsortedDirectory(String firstDirectory) {
        directories.setUnsortedDirectory(firstDirectory);
    }

    @Override
    public Path getSortedDirectory() {
        return directories.getSortedDirectory();
    }

    @Override
    public void setSortedDirectory(String secondDirectory) {
        directories.setSortedDirectory(secondDirectory);
    }
}
