package ru.palankar.sortingscript.Service;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.palankar.sortingscript.Model.JSONList;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WinCmdFileService extends CommandServiceImpl implements FileService {
    private Logger logger = LogManager.getLogger(WinCmdFileService.class);
    private JSONList jsonList = JSONList.getInstance();
    private JSONService jsonService = new JSONServiceImpl();

    public WinCmdFileService(Path directory) {
        fillArray(directory);
    }

    /**
     * Перемещает файл из одной директории в другую
     * @param   file    исходный файл
     * @param   into    конечная директория
     */
    @Override
    public void moveFile(File file, Path into) {
        logger.info("Moving file from " + FilenameUtils.getFullPath(file.getPath()) + " to " + into.toString());

        File renamed = renameFile(file, FilenameUtils.getName(file.getName()) + ".part", true);
        logger.info("Moving " + renamed.getName() + "...");

        for (File fileIn : into.toFile().listFiles()) {
            if (fileIn.getName().equals(file.getName())) {
                renameFile(fileIn, FilenameUtils.getName(fileIn.getName()) + ".part", false);
            }
        }

        runCmd("move /Y \"" + renamed.getPath() + "\" \"" + into.toString() + "\"");

        File movedStart = new File(into.toString() + "\\" + FilenameUtils.getName(renamed.getName()));
        updateFiles(renamed, movedStart);
        File movedFinal = renameFile(movedStart, FilenameUtils.getBaseName(renamed.getName()), true);

        if (movedFinal.exists())
            logger.info("Moving files complete");
        else
            logger.error("MOVED FILES DOES NOT EXISTS");
    }

    /**
     * Использует для переименования команду cmd: 'rename fileName newFileName'
     * @param   file        исходный файл
     * @param   newName     новое имя файла
     * @param   toUpdate    обновлять ли список
     * @return  создает File с новым именем по пути старого и возвращает его
     */
    @Override
    public File renameFile(File file, String newName, boolean toUpdate) {
        runCmd("rename \"" + file.getPath() + "\" " + newName);

        File renamed = new File(FilenameUtils.getFullPath(file.getPath()) + newName);

        if (renamed.exists()) {
            if (toUpdate)
                updateFiles(file, renamed);
        } else {
            logger.error("FILE " + renamed.getName() + " DOES NOT EXISTS");
            return null;
        }

        return renamed;
    }

    /**
     * Обновляет файлы в коллекциях jsonToUserFileMap и userFilesList и
     * JSON в коллекции jsonList
     * @param   oldFile     старый файл, изначально находящийся в коллекции
     * @param   newFile     новый файл, заменяющий старый в коллекции
     */
    @Override
    public void updateFiles(File oldFile, File newFile) {
        if (oldFile.getName().contains(".json"))
                jsonService.updateJSON(oldFile, newFile);
    }

    private void fillArray(Path directory) {
        File[] files = directory.toFile().listFiles();

        assert files != null : "Files array is nullable";
        if (files.length > 0) {
            List<File> jsons = filterArray(files);

            for (File json : jsons) {
                jsonList.getList().add(json);
            }
        }
    }

    private List<File> filterArray(File[] files) {
        ArrayList<File> jsons = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith(".json"))
                jsons.add(file);
        }

        if (jsons.size() == 0)
            logger.info("JSON files missing");

        return jsons;
    }
}
