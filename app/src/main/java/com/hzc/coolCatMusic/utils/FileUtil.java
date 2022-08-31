package com.hzc.coolCatMusic.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

public class FileUtil {


    public static File[] getFiles() {
        File sdDir = Environment.getExternalStorageDirectory();
        File kgMusicPath = new File(sdDir + "/kgmusic/download/kgmusic");
        File[] files = kgMusicPath.listFiles();
        return files;
    }


    public static String execute_command(String cmd) {
        try {
            //String keyCommand = "setprop " + propName;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  cmd;
    }

    //读取指定目录下的所有TXT文件的文件名
    private static String getFileName(File[] files) {
        String str = "";
        if (files != null) {	// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()){//检查此路径名的文件是否是一个目录(文件夹)
                    Log.i("zeng-eee", "若是文件目录。继续读1" +file.getName().toString()+file.getPath().toString());
                    getFileName(file.listFiles());
                    Log.i("zeng-eee", "若是文件目录。继续读2" +file.getName().toString()+ file.getPath().toString());
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".txt")) {
                        String s=fileName.substring(0,fileName.lastIndexOf(".")).toString();
                        Log.i("zeng-eee", "文件名txt：：   " + s);
                        str += fileName.substring(0,fileName.lastIndexOf("."))+"\n";
                    }
                }
            }
        }
        return str;
    }

    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.e("copyFile", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("copyFile", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("copyFile", "copyFile:  oldFile cannot read.");
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
