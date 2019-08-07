package ru.palankar.sortingscript.Service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.palankar.sortingscript.Model.JSONList;

import java.io.*;
import java.util.Collections;

public class JSONServiceImpl implements JSONService {
    private Logger logger = LogManager.getLogger(JSONServiceImpl.class);
    private JSONList jsonList = JSONList.getInstance();

    /**
     * Добавляет в имеющийся JSON-файл параметр со значением, если такого
     * параметра до этого не было. Если был, то заменяет его значение на
     * переданное
     * @param   json    JSON-файл
     * @param   param   параметр
     * @param   value   значение
     */
    @Override
    public void putParam(File json, String param, String value) {
        JSONObject object = getObj(json.getPath());

        if (object.containsKey(param))
            object.replace(param, value);
        else
            object.put(param, value);

        try (FileWriter writer = new FileWriter(json.getPath())){
            writer.write(object.toJSONString());
        } catch (IOException e) {
            System.out.println("Error adding param to " + json.getName());
        }
    }

    /**
     * Получает данные из json-файла в объект JSONObject
     * @param   path    путь json-файла
     * @return  объект JSONObject с данными из файла
     */
    @Override
    public JSONObject getObj(String path) {
        JSONParser parser = new JSONParser();
        JSONObject object = null;

        try (Reader reader = new FileReader(path)){

            object = (JSONObject) parser.parse(reader);

        } catch (IOException | ParseException e) {
            logger.error("Error reading json: " + path);
        }

        return object;
    }

    /**
     * Обновляет JSON в коллекции jsonList
     * @param   oldJSON     старый json
     * @param   newJSON     новый json
     */
    @Override
    public void updateJSON(File oldJSON, File newJSON) {
        if (!Collections.replaceAll(jsonList.getList(), oldJSON, newJSON))
            logger.warn("Failed to update jsons");
    }
}
