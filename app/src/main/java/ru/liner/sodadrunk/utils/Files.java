package ru.liner.sodadrunk.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @package com.liner.linerlauncher.util
 * @created at 26.07.2020 - 6:02
 * @autor Line'R (serinity320@gmail.com)
 **/
public class Files {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static boolean delete(File destination) {
        if (destination == null)
            return true;
        if (!destination.canRead())
            return false;
        if (destination.isDirectory()) {
            File[] files = destination.listFiles();
            for (File file : files)
                delete(file);
        } else {
            destination.delete();
        }
        return true;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    public static boolean containEntry(String entryName, File folder){
        if(!folder.isDirectory())
            return false;
        for(File file: Objects.requireNonNull(folder.listFiles())){
            if(file.getName().equalsIgnoreCase(entryName))
                return true;
        }
        return false;
    }


    public static void stringToFile(String content, String filename){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"L-Launcher"+File.separator);
        if(!file.exists())
            file.mkdirs();
        File contentFile = new File(file, filename+".lldata");
        if(contentFile.exists())
            contentFile.delete();
        try {
            contentFile.createNewFile();
            try (FileOutputStream fileOutputStream = new FileOutputStream(contentFile)) {
                fileOutputStream.write(content.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stringToFile(String content, String filename, String directory){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+directory+File.separator);
        if(!file.exists())
            file.mkdirs();
        File contentFile = new File(file, filename+".lldata");
        if(contentFile.exists())
            contentFile.delete();
        try {
            contentFile.createNewFile();
            try (FileOutputStream fileOutputStream = new FileOutputStream(contentFile)) {
                fileOutputStream.write(content.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fileToString(String filename, String directory){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+directory+File.separator);
        if(!file.exists()) {
            file.mkdirs();
            return null;
        }
        File contentFile = new File(file, filename+".lldata");
        byte[] bytes = new byte[(int) contentFile.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(contentFile);
            fileInputStream.read(bytes);
            fileInputStream.close();
            return new String(bytes);
        } catch (IOException e){
            return null;
        }
    }
}
