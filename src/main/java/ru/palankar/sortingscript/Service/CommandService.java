package ru.palankar.sortingscript.Service;

public interface CommandService {

    /**
     * Запускает команду через Runtime
     * @param   command     команда для запуска
     */
    void runCmd(String command);
    // TODO: 30.07.2019 Если потом понадобится - можно добавить поле выбора ОС и команды по Linux
}
