package util;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Persistence {
    private static final String saveFileName = "stlFiles.json";

    private static void checkFileExists() throws IOException {
        var file = new File(saveFileName);
        if (!file.exists()) {
            file.createNewFile();
            Files.writeString(file.toPath(), "{}");
        }
    }

    public static JSONObject readStlSaveFile() throws IOException {
        checkFileExists();
        var jsonString = Files.readString(new File(saveFileName).toPath());
        return new JSONObject(jsonString);
    }

    public static void writeStlSaveFile(JSONObject jsonObject) throws IOException {
        checkFileExists();
        Files.writeString(new File(saveFileName).toPath(), jsonObject.toString());
    }
}
