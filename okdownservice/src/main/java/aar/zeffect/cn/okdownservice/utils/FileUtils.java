package aar.zeffect.cn.okdownservice.utils;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class FileUtils {

    public static String read(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";
        File tempFile = new File(filePath);
        if (!tempFile.exists() || tempFile.isDirectory()) return "";
        FileInputStream fileInput;
        try {
            fileInput = new FileInputStream(tempFile);
            return inputStream2String(fileInput);
        } catch (FileNotFoundException e) {
            return "";
        }

    }

    public static boolean write(String filePath, String content) {
        return write(filePath, content, false);
    }


    public static boolean write(String filePath, String content, boolean append) {
        try {
            if (TextUtils.isEmpty(filePath)) return false;
            File tempFile = new File(filePath);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            }
            if (tempFile.isDirectory()) return false;
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }


    }

    private static String inputStream2String(InputStream inputs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int i = inputs.read();
            while (i != -1) {
                baos.write(i);
                i = inputs.read();
            }
        } catch (IOException e) {
        } finally {
            return baos.toString();
        }
    }
}
