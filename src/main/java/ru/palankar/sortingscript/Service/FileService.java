package ru.palankar.sortingscript.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Сервис взаимодействия с файлами
 */
public interface FileService {
    /**
     * Перемещает файл из одной директории в другую
     * @param   file    исходный файл
     * @param   into    конечная директория
     */
    void moveFile(File file, Path into);

    /**
     * Переименовывает переданный в аргументы файл
     * @param   file        исходный файл
     * @param   newName     новое имя файла
     * @param   toUpdate    обновлять ли список
     */
    File renameFile(File file, String newName, boolean toUpdate);

    /**
     * Заменяет файл, уже имеющийся в коллекции на новый
     * @param   oldFile     старый файл, изначально находящийся в коллекции
     * @param   renamed     новый, переименованный файл, заменяющий старый в коллекции
     */
    void updateFiles(File oldFile, File renamed);
}
