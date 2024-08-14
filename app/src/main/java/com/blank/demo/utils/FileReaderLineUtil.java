package com.blank.demo.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderLineUtil {
    private static final String TAG = "FileReaderLineUtil";

    private FileReaderLineUtil() {
        // 私有构造函数，确保该类不会被实例化
    }

    public static byte[] readLinesToByteArray(String filePath) {
        List<byte[]> linesData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                byte[] lineBytes = line.getBytes();
                linesData.add(lineBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[]{};
        }

        int totalLength = calculateTotalLength(linesData);
        byte[] byteArray = new byte[totalLength];
        int offset = 0;
        for (byte[] lineData : linesData) {
            System.arraycopy(lineData, 0, byteArray, offset, lineData.length);
            offset += lineData.length;
        }

        return byteArray;
    }

    public static short readLinesLength(String filePath) {
        short len = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while (reader.readLine() != null) {
                len++;
            }
        } catch (IOException e) {
            len = 0;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 处理关闭文件读取器时的异常
                }
            }
        }
        return len;
    }

    private static int calculateTotalLength(List<byte[]> linesData) {
        int totalLength = 0;
        for (byte[] lineData : linesData) {
            totalLength += lineData.length;
        }
        return totalLength;
    }
}