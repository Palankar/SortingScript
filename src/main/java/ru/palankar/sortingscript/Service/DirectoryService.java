package ru.palankar.sortingscript.Service;

import java.nio.file.Path;

public interface DirectoryService {

    /**
     * Инициалиизация директорий из .properties файла
     * Файл обязательно должен лежать в одной директории со скриптом
     * @param   properties  путь к файлу с указаниями директорий
     */
    void init(String properties);

    /**
     * Получить путь стартовой директории
     * @return  стартовая директория в формате Path
     */
    Path getUnsortedDirectory();

    /**
     * Задать путь стартовой директории
     * @param   firstDirectory  путь стартовой директории
     */
    void setUnsortedDirectory(String firstDirectory);

    /**
     * Получить путь директории с антивирусом
     * @return  директория с анитвирусом в формате Path
     */
    Path getSortedDirectory();

    /**
     * Задать путь директории с антивирусом
     * @param   secondDirectory путь директории с антивирусом
     */
    void setSortedDirectory(String secondDirectory);
}
