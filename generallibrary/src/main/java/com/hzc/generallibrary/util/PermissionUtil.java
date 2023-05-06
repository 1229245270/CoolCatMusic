package com.hzc.generallibrary.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tbruyelle.rxpermissions3.RxPermissions;

public class PermissionUtil {

    public static boolean hasPermissions(Context context,String[] permissions){
        for (String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }


    public static boolean requestPermission(ActivityResultLauncher<Intent> intentActivityResultLauncher, Context context, String[] permissions){
        if(!hasPermissions(context,permissions)){
            /*ActivityResultLauncher<Intent> intentActivityResultLauncher = activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if(result != null && result.getData() != null && result.getResultCode() == PermissionActivity.RESULT_CODE){
                            boolean havePermission = result.getData().getBooleanExtra(PermissionActivity.KEY_HAVE_PERMISSION,false);
                            onCallback.onResult(havePermission);
                        }else{
                            onCallback.onResult(false);
                        }
                    }
            );*/
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.putExtra(PermissionActivity.KEY_PERMISSIONS,permissions);
            intentActivityResultLauncher.launch(intent);
            return false;
        }else{
            return true;
        }
    }

}
