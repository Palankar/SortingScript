package ru.palankar.sortingscript.Service;

import org.json.simple.JSONObject;

import java.io.File;

public interface JSONService {

    /**
     * Добавляет в имеющийся JSON-файл параметр со значением, если такого
     * параметра до этого не было. Если был, то заменяет его значение на
     * переданное
     * @param   json    JSON-файл
     * @param   param   параметр
     * @param   value   значение
     */
    void putParam(File json, String param, String value);

    /**
     * Получает данные из json-файла в объект JSONObject
     * @param   path    путь json-файла
     * @return  объект JSONObject с данными из файла
     */
    JSONObject getObj(String path);

    /**
     * Обновляет JSON в коллекции jsonList
     * @param   oldJSON     старый json
     * @param   newJSON     новый json
     */
    void updateJSON(File oldJSON, File newJSON);
}
