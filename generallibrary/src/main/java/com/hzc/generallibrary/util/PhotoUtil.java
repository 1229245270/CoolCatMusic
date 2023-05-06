package com.hzc.generallibrary.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.huantansheng.easyphotos.EasyPhotos;

import java.io.File;

/**
 * @author 12292
 */
public class PhotoUtil {

    public static void takePhoto(Activity activity, int requestCode){
        //取消焦点，防止滑动
        try {
            activity.getCurrentFocus().clearFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
        EasyPhotos.createAlbum(activity, true, false, GlideEngine.getInstance())
                .setFileProviderAuthority(activity.getPackageName() + ".provider")
                .setPuzzleMenu(false)
                .setCleanMenu(false)
                //.setSelectedPhotos(selectPhoto)//当传入已选中图片时，按照之前选中的顺序排序
                .start(requestCode);
    }

    /**
     *
     * @param activity
     * @param requestCode
     * @param fileName
     * @return
     */
    public static String takePhotoFromCamera(AppCompatActivity activity, int requestCode, String fileName){
        //取消焦点，防止滑动
        /*try {
            activity.getCurrentFocus().clearFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
        String[] permissions = new String[]{Manifest.permission.CAMERA};

        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.CAMERA);
        RxPermissions*/
        /*long time = System.currentTimeMillis();
        String filePath;
        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;

        // 判断7.0android系统
        //通过FileProvider获取uri
        //临时添加一个拍照权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filePath = ImageUtil.getPathCachePath(activity,fileName) + time + ".jpg";
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", new File(filePath));
        } else {
            //暂不知道原因，必须使用SD卡路径，否则request会返回0
            filePath = Environment.getExternalStorageDirectory().getPath() + "/" + time + ".jpg";
            uri = Uri.fromFile(new File(filePath));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, requestCode);
        return filePath;*/
        return null;
    }

}
