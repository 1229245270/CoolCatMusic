package com.hzc.coolcatmusic.utils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.ui.chatgpt.ChatFloating;

import java.util.Collections;

import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @author 12292
 */
public class ChatFloatingUtils {

    private WindowManager windowManager;
    private WindowManager.LayoutParams floatLp;
    private View floatView;
    private final Context context;

    public ChatFloatingUtils(Context context){
        this.context = context;
    }

    public void initChatFloating() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_chat, null);
        /*floatView.findViewById(R.id.et_chat).setOnClickListener(v -> {
            stopSelf();
            windowManager.removeView(floatView);
            Intent backToHome = new Intent(getApplicationContext(), ChatFloating.class);
            backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToHome);
        });
        floatView.findViewById(R.id.tv_chat).setOnClickListener(v -> {
            stopSelf();
            windowManager.removeView(floatView);
        });*/

        int layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        floatLp = new WindowManager.LayoutParams(
                (int) (width * 0.4),
                (int) (height * 0.4),
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,PixelFormat.OPAQUE
                //WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                //PixelFormat.TRANSLUCENT
        );
        //floatLp = new WindowManager.LayoutParams(layoutType);
        //floatLp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //floatLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //floatLp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        floatLp.gravity = Gravity.CENTER;
        floatLp.x = 0;
        floatLp.y = 0;
        floatView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatLp;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        px = event.getRawX();
                        py = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                        break;
                    default:
                }
                return false;
            }
        });
    }

    public void openChatFloating(){
        if(!checkOverlayDisplayPermission()){
            ToastUtils.showShort("请授权");
            return;
        }
        if(windowManager == null){
            initChatFloating();
        }
        windowManager.addView(floatView, floatLp);
    }

    public void closeChatFloating(){
        if(windowManager != null){
            windowManager.removeView(floatView);
        }
    }

    private boolean checkOverlayDisplayPermission() {
        boolean permission = Settings.canDrawOverlays(context);
        if(!permission){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
        return permission;
    }

}
