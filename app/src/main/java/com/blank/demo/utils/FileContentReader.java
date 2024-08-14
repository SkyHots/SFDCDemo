package com.blank.demo.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileContentReader {
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }
}