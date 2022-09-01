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
        files.addAll(Arrays.asList(getKgFiles()));
        files.addAll(Arrays.asList(getWyyFiles()));
        return files;
    }

    private static File[] getKgFiles() {
        File kgMusicPath = new File(sdDir + "/kgmusic/download/kgmusic");
        return kgMusicPath.listFiles();
    }

    private static File[] getWyyFiles() {
        File wyyMusicPath = new File(sdDir + "/netease/cloudmusic/Music");
        return wyyMusicPath.listFiles();
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
