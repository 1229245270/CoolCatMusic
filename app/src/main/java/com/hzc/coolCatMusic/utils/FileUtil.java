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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

public class FileUtil {

    private static final File sdDir = Environment.getExternalStorageDirectory();

    public static List<File> getAllFiles(){
        List<File> files = new ArrayList<>();
        files.addAll(getFiles("/kgmusic/download/kgmusic"));
        files.addAll(getFiles("/netease/cloudmusic/Music"));
        return files;
    }

    private static List<File> getFiles(String path) {
        List<File> files = new ArrayList<>();
        File musicPath = new File(sdDir + path);
        File[] listFiles = musicPath.listFiles();
        if(listFiles != null){
            for(File file : listFiles){
                try{
                    if(file.isFile() && !file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).equals(".mp3")){
                        files.add(file);
                    }
                }catch (Exception e){
                    KLog.e("文件解析失败:" + e.toString());
                }
            }
        }
        return files;
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
