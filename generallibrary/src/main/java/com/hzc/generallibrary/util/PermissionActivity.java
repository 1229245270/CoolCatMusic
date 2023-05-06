package com.hzc.generallibrary.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hzc.generallibrary.R;

import java.util.ArrayList;

public class PermissionActivity extends AppCompatActivity {

    private String[] permissions;
    private final int requestCode = 100;
    public static final String KEY_PERMISSIONS = "permissions";
    public static final int RESULT_CODE = 200;
    public static final String KEY_HAVE_PERMISSION = "havePermission";
    private static ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private OnCallback onCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        Intent intent = getIntent();
        if(intent != null){
            permissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
            requestPermissions(permissions,requestCode);
        }else{
            finish();
        }

        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result != null && result.getData() != null && result.getResultCode() == PermissionActivity.RESULT_CODE){
                        boolean havePermission = result.getData().getBooleanExtra(PermissionActivity.KEY_HAVE_PERMISSION,false);
                        onCallback.onResult(havePermission);
                    }else{
                        onCallback.onResult(false);
                    }
                }
        );
    }

    public static void startActivity(Activity activity,String[] permissions){
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra(PermissionActivity.KEY_PERMISSIONS,permissions);
        intentActivityResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(this.requestCode == requestCode){
            boolean hasPermission = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                Intent intent = new Intent();
                intent.putExtra(KEY_HAVE_PERMISSION,true);
                setResult(RESULT_CODE,intent);
            }
        }
        finish();
    }

    public void setOnCallback(OnCallback onCallback) {
        this.onCallback = onCallback;
    }

    public interface OnCallback{
        /**
         * 回调
         * @param havePermission 是否授权
         */
        void onResult(boolean havePermission);
    }
}
