package ru.palankar.sortingscript;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import ru.palankar.sortingscript.Model.JSONList;
import ru.palankar.sortingscript.Service.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptBody {
    //"src/main/resources/directories.properties" - для запуска с IDE
    //System.getProperty("user.dir") + "\\directories.properties" - для хапуска с билда
    private static final String PATH_TO_DIR_PROPERTIES = "src/main/resources/directories.properties";

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

                System.out.println("fileName: " + fileName);
                System.out.println("fileFromJson: " + fileFromJson.getName());
                System.out.println("OrganisationINN: " + OrganisationINN);
                System.out.println("OrganisationName: " + OrganisationName);
                System.out.println(dirOrg);
            }

            System.out.println(jsonList.getList());

        } else {
            logger.warn("Required files not found");
        }

        logger.info("Script complete");
    }
}
