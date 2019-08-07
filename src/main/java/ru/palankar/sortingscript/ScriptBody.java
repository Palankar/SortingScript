package ru.palankar.sortingscript;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.palankar.sortingscript.Model.JSONList;
import ru.palankar.sortingscript.Service.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


// TODO: 02.08.2019 Метод для получения параметров с логированием
// TODO: 02.08.2019 Метод создания директории с логированием
// TODO: 02.08.2019 Набор всех файлов и соотношение к JSON по параметрам

public class ScriptBody {
    //"src/main/resources/directories.json" - для запуска с IDE
    //System.getProperty("user.dir") + "\\directories.json" - для хапуска с билда
    private static final String PATH_TO_DIR_PROPERTIES = "src/main/resources/directories.json";
    private static final String PATH_TO_DOC_TYPES = "src/main/resources/documentTypes.json";


    CommandService cmd = new CommandServiceImpl();

    private Logger logger = LogManager.getLogger(ScriptBody.class);
    private DirectoryService dirService;
    private FileService fileService;
    private JSONList jsonList;
    private JSONService jsonService = new JSONServiceImpl();
    private List<File> allfiles = new ArrayList<>();

    public ScriptBody() {
        dirService = new DirectoryServiceImpl(PATH_TO_DIR_PROPERTIES);
        fileService = new WinCmdFileService(dirService.getUnsortedDirectory());
        jsonList = JSONList.getInstance();
    }

    public void startScript() {
        logger.info("Starting script...");
        File[] files = dirService.getUnsortedDirectory().toFile().listFiles();

        assert files != null;
        if (files.length == 0) {
            logger.warn("There are no files in " + dirService.getUnsortedDirectory().toString());
            return;
        }
        allfiles.addAll(Arrays.asList(files));

        if (jsonList.getList().size() > 0) {

            String fileName;
            File fileFromJson = null;
            String OrganisationINN;
            String OrganisationName;
            Path dirOrg;
            Path projectId;
            Path docName;
            Integer DocumentGroupID;

            for (File json : jsonList.getList()) {
                JSONObject obj = jsonService.getObj(json.getPath());

                System.out.println(json.getName());


                if (obj.containsKey("fileName")) {
                    fileName = obj.get("fileName").toString();

                    boolean isContains = false;
                    for (File file : allfiles) {
                        if (file.getName().contains(fileName)) {
                            fileFromJson = file;
                            isContains = true;
                        }
                    }
                    if (!isContains) {
                        logger.warn("Missing file with name: " + fileName);
                        continue;
                    }

                } else {
                    logger.warn("Parameter fileName is missing in the " + json.getName());
                    continue;
                }

                if (obj.containsKey("OrganisationINN")) {
                    OrganisationINN = obj.get("OrganisationINN").toString();
                } else {
                    logger.warn("Parameter OrganisationINN is missing in the " + json.getName());
                    continue;
                }

                if (obj.containsKey("OrganisationName")) {
                    OrganisationName = obj.get("OrganisationName").toString().replaceAll("[\\\\/:\"*?<>|]+", "_");
                } else {
                    logger.warn("Parameter OrganisationName is missing in the " + json.getName());
                    continue;
                }

                dirOrg = Paths.get(dirService.getSortedDirectory()+ "\\" + OrganisationINN+OrganisationName);
                if (!dirOrg.toFile().exists()) {
                    if (!dirOrg.toFile().mkdir())
                        logger.error("Can't create directory " + dirOrg.toString());
                }

                if (obj.containsKey("projectId")) {
                    projectId = Paths.get(dirOrg + "\\" + obj.get("projectId").toString());
                    if (!projectId.toFile().exists()) {
                        if (!projectId.toFile().mkdir())
                            logger.error("Can't create directory " + projectId.toString());
                    }
                } else {
                    logger.warn("Parameter projectId is missing in the " + json.getName());
                    continue;
                }

                if (obj.containsKey("DocumentGroupID")) {
                    DocumentGroupID = Integer.parseInt(obj.get("DocumentGroupID").toString());
                } else {
                    logger.warn("Parameter DocumentGroupID is missing in the " + json.getName());
                    continue;
                }

                JSONObject jsonObject =jsonService.getObj(PATH_TO_DOC_TYPES);


                if (jsonObject.containsKey(DocumentGroupID.toString())) {
                    docName = Paths.get(projectId + "\\" + jsonObject.get(DocumentGroupID.toString()));
                    if (!docName.toFile().exists())
                        if (!docName.toFile().mkdir())
                            logger.error("Can't create directory " + docName.toString());
                } else {
                    logger.warn("Parameter DocumentGroupID is missing in the documentTypes.json");
                    continue;
                }

                //перемещает с заменой
                //cmd.runCmd("move /Y \"" + fileFromJson.getPath() + "\" \"" + docName.toString() + "\"");
                fileService.moveFile(fileFromJson, dirService.getUnsortedDirectory(), docName);

                Path done = Paths.get(dirService.getUnsortedDirectory() + "\\done");
                if (!done.toFile().exists()) {
                    if (!done.toFile().mkdir())
                        logger.error("Can't create directory " + done.toString());
                }

                fileService.moveFile(json, dirService.getUnsortedDirectory(), done);
                //cmd.runCmd("move /Y \"" + json.getPath() + "\" \"" + done.toString() + "\"");


                // TODO: 02.08.2019 Зачем перемещению указывать начальную директорию, если можно указать ее внутри как директорию файла
                // TODO: 05.08.2019 Ошибка при перемещении файла. Переименовываются не те файлы, файл сам содержит не то имя

                System.out.println("fileName: " + fileName);
                System.out.println("fileFromJson: " + fileFromJson.getName());
                System.out.println("OrganisationINN: " + OrganisationINN);
                System.out.println("OrganisationName: " + OrganisationName);
                System.out.println(dirOrg);
                System.out.println(projectId);
                System.out.println(docName);
                System.out.println("DocumentGroupID: " + DocumentGroupID);
            }

            System.out.println(jsonList.getList());

        } else {
            logger.warn("Required files not found");
        }

        logger.info("Script complete");
    }
}
