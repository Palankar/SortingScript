package ru.palankar.sortingscript.Service;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.palankar.sortingscript.Model.JSONList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WinCmdFileService extends CommandServiceImpl implements FileService {
    private Logger logger = LogManager.getLogger(WinCmdFileService.class);
    private JSONList jsonList = JSONList.getInstance();
    private JSONService jsonService = new JSONServiceImpl();
    private DirectoryService dirService = new DirectoryServiceImpl();
    // TODO: 31.07.2019 Переделать хардкод, когда дадут вызов антивируса
    private static final String KAVResult = "0";    //Что вернет антивирь


    public WinCmdFileService(Path directory) {
        fillArray(directory);
    }

    /**
     * Перемещает набор файлов из одной директории в другую
     * @param   files   исходная коллекция файлов
     * @param   from    начальная директория
     * @param   into    конечная директория
     */
    @Override
    public void moveFiles(List<File> files, Path from, Path into) {
        logger.info("Moving files from " + from.toString() + " to " + into.toString());

        List<File> movedFiles = new ArrayList<>();
        for (File file : files) {
            File renamed = renameFile(file, FilenameUtils.getName(file.getName()) + ".part", true);
            logger.info("Moving " + renamed.getName() + "...");

            for (File fileIn : into.toFile().listFiles()) {
                if (fileIn.getName().equals(file.getName())) {
                    renameFile(fileIn, FilenameUtils.getName(fileIn.getName()) + ".part", false);
                }
            }

            runCmd("move \"" + renamed.getPath() + "\" \"" + into.toString() + "\"");

            File movedStart = new File(into.toString() + "\\" + FilenameUtils.getName(renamed.getName()));
            updateFiles(renamed, movedStart);
            File movedFinal = renameFile(movedStart, FilenameUtils.getBaseName(renamed.getName()), true);
            movedFiles.add(movedFinal);
        }

        if (isFilesExists(movedFiles))
            logger.info("Moving files complete");
        else
            logger.error("MOVED FILES DOES NOT EXISTS");
    }

    /**
     * Перемещает файл из одной директории в другую
     * @param   file    исходный файл
     * @param   from    начальная директория
     * @param   into    конечная директория
     */
    @Override
    public void moveFile(File file, Path from, Path into) {
        logger.info("Moving file from " + from.toString() + " to " + into.toString());

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
     * Сохраняет переданный файл в указанную директорию, предварительно добавив к его имени
     * расширение .part во избежание обращения в процессе сохранения со стороны других программ.
     * По завершению процесса сохранения приставка .part удаляется
     * @param   file    файл для сохранения
     * @param   into    директория сохранения
     */
    @Override
    public void saveFile(File file, Path into) {
        logger.info("Start saving " + file.getName() + " to " + into.toString());
        File renamed = renameFile(file, FilenameUtils.getName(file.getName()) + ".part", true);
        logger.info("Coping " + renamed.getPath() + " to " + into.toString() + "...");

        runCmd("copy \"" + renamed.getPath() + "\" \"" + into.toString() + "\"");

        File savedStart = new File(into.toString() + "\\" + FilenameUtils.getName(renamed.getName()));
        File savedFinal = renameFile(savedStart, FilenameUtils.getBaseName(renamed.getName()), true);
        if (savedFinal.exists())
            logger.info("Saving correct");
        else
            logger.error("SAVED FILE DOES NOT EXISTS");
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
        /*if (!oldFile.getName().contains(".json")) {
            for (String key : jsonToUserFileMap.getMap().keySet()) {
                String val = oldFile.getName().split("[.]")[0];
                if (key.contains(val))
                    jsonToUserFileMap.getMap().replace(key, newFile);
            }
            if (!Collections.replaceAll(userFilesList.getList(), oldFile, newFile))
                logger.warn("Failed to update files");
        } else*/ {
            if (oldFile.getName().contains(".json"))
                jsonService.updateJSON(oldFile, newFile);
        }
    }

    /**
     * Проверка антивирусом. Пока что чисто фиктивная
     * @param   files   коллекция, содержащая файлы на проверку антивирусом
     * @return  <code>true</code> при нахождении вируса, иначе <code>false</code>
     */
    @Override
    public boolean checkByAntivirus(Map<String, File> files) {
        logger.info("Anti-virus scanning...");

        for (File file : files.values()) {
            File renamed = renameFile(file, file.getName() + ".checking", true);
            logger.info("Scanning " + renamed);


            if(!KAVResult.equals("0")) {
                logger.warn("VIRUS DETECTED IN FILE " + file);

                //Ищет json к файлу. попробовать вынести в отдельный метод?
                File jsonToFile = null;
                for (File json : jsonList.getList()) {
                    if (json.getName().contains(FilenameUtils.getBaseName(file.getName())))
                        jsonToFile = json;
                }

                jsonService.putParam(jsonToFile, "AntivirusScanResult", KAVResult);

                File errorsDir;
                if (!(errorsDir = new File(dirService.getSortedDirectory().toString() + "\\Errors")).exists()) {
                    if (!errorsDir.mkdir())
                        logger.error("Failed to create directory " + errorsDir.getName());
                    moveFile(jsonToFile, dirService.getUnsortedDirectory(), errorsDir.toPath());
                } else {
                    moveFile(jsonToFile, dirService.getUnsortedDirectory(), errorsDir.toPath());
                }

                renameFile(renamed, FilenameUtils.removeExtension(renamed.getName()), true);
            } else {
                renameFile(renamed, FilenameUtils.removeExtension(renamed.getName()), true);
            }

        }
        logger.info("Scanning complete");

        return !KAVResult.equals("0");
    }

    /**
     * Удаляет переданный в аргументы файл
     * @param   file    удаляемый файл
     */
    @Override
    public void deleteFile(File file) {
        logger.info("Deleting " + file.getName());

        runCmd("del \"" + file.getPath() + "\"");

        logger.info("File " + file.getName() + " deleted");
    }

    /**
     * Проверяет набор файлов на существование
     * @param   files   проверяемая коллекция файлов
     * @return  <code>true</code>, если каждый файл существует, иначе <code>false</code>
     */
    @Override
    public boolean isFilesExists(List<File> files) {
        boolean isExist = true;

        for (File file : files) {
            if (!file.exists())
                isExist = false;
        }

        return isExist;
    }

    private void fillArray(Path directory) {
        File[] files = directory.toFile().listFiles();

        assert files != null : "Files array is nullable";  // TODO: 01.08.2019 разберись с assert
        if (files.length > 0) {
            List<File> jsons = filterArray(files);
            //findPairs(jsons, directory);

            for (File json : jsons) {
                //String jsonName = FilenameUtils.getName(json.getName());
                //if (jsonToUserFileMap.getMap().containsKey(jsonName))
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

    private void findPairs(List<File> jsons, Path directory) {
        // TODO: 28.07.2019 попробовать провести поиск по директории с помощью dir и перехвата данных с консоли
        /*
        try {
            List<Path> files = Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toFile().getName().endsWith(".json"))
                    .collect(Collectors.toCollection(ArrayList::new));

            for (Path file : files) {
                for (File json : jsons) {
                    if (file.toFile().getName().contains(
                            FilenameUtils.getBaseName(json.getName()))) {
                        jsonToUserFileMap.getMap().put(json.getName(), file.toFile());
                        userFilesList.getList().add(file.toFile());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("JSON/File pair search error");
        }
        */
    }
}
