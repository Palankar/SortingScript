package ru.palankar.sortingscript.Service;

import java.nio.file.Path;

public interface DirectoryService {

    /**
     * Инициалиизация директорий из .json файла
     * Файл обязательно должен лежать в одной директории со скриптом
     * @param   properties  путь к файлу с указаниями директорий
     */
    void init(String properties);

    /**
     * Получить путь неотсортированной директории
     * @return  стартовая директория в формате Path
     */
    Path getUnsortedDirectory();

    /**
     * Задать путь неотсортированной директории
     * @param   UnsortedDirectory  путь неотсортированной директории
     */
    void setUnsortedDirectory(String UnsortedDirectory);

    /**
     * Получить путь директории с отсортированными файлами
     * @return  директория с отсортированными файлами в формате Path
     */
    Path getSortedDirectory();

    /**
     * Задать путь директории с отсортированными файлами
     * @param   SortedDirectory путь директории с отсортированными файлами
     */
    void setSortedDirectory(String SortedDirectory);
}
