package ru.palankar.sortingscript.Service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.palankar.sortingscript.Model.Directories;
import java.nio.file.Path;

public class DirectoryServiceImpl implements DirectoryService {
    private Logger logger;
    private Directories directories;
    private JSONService jsonService;

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
        jsonService = new JSONServiceImpl();
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

            JSONObject dirJSON = jsonService.getObj(properties);

            setUnsortedDirectory(dirJSON.get("UnsortedDirectory").toString());
            logger.info("Direction property " + dirJSON.get("UnsortedDirectory").toString() + " initialised");
            setSortedDirectory(dirJSON.get("SortedDirectory").toString());
            logger.info("Direction property " + dirJSON.get("SortedDirectory").toString() + " initialised");
        } catch (Exception e) {
            logger.error("PROPERTY FILE directories.json COULD NOT FOUND");
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
