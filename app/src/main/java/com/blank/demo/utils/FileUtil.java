package com.blank.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static List<String> listFilesInDirectory(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return fileNames;
        }
        File[] files = directory.listFiles();
        if (files == null)
            return fileNames;
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    public static String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    public static void writeJsonToFile(String json, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}