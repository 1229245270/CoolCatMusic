package com.hzc.coolcatmusic.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.hzc.coolcatmusic.app.AppApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

/**
 * @author 12292
 */
public class FileUtil {


    public static List<File> getAllFiles(){
        List<File> files = new ArrayList<>();
        files.addAll(getFiles("/kgmusic/download/kgmusic"));
        files.addAll(getFiles("/netease/cloudmusic/Music"));
        return files;
    }

    private static List<File> getFiles(String path) {
        List<File> files = new ArrayList<>();
        File musicPath = new File(AppApplication.PATH_SD_DIR + path);
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

    //保存方法
    public static String getDownloadFile(Bitmap bm, String path) throws IOException {
        String newPath = AppApplication.PATH_CACHE_SONG_IMAGE + path.substring(0,path.lastIndexOf(".")) + ".png";
        String dirs = newPath.substring(0,newPath.lastIndexOf("/"));
        String name = newPath.substring(newPath.lastIndexOf("/") + 1);
        File fileDirs = new File(dirs);
        if(!fileDirs.exists()){
            boolean isSuccess = fileDirs.mkdirs();
            if(isSuccess){
                KLog.d(dirs + " 文件夹创建成功");
            }
        }
        File fileName = new File(dirs,name);
        if(!fileName.exists()){
            boolean isSuccess = fileName.createNewFile();
            if(isSuccess){
                KLog.d(fileName + " 文件创建成功");
            }
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return newPath;
    }
}
