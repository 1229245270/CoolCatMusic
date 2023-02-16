package com.hzc.coolcatmusic.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class DialogUtils {
    public static void openAppSettingDialog(Context context){
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("跳转应用权限设置？")
                .cancelable(true)
                .positiveText("是的")
                .negativeText("取消")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(which == DialogAction.POSITIVE){
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("package:" + context.getPackageName()));
                            context.startActivity(intent);
                        }
                        dialog.cancel();
                    }
                })
                .build();
        dialog.show();
    }


}
