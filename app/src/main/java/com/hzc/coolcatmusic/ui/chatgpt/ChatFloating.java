package com.hzc.coolcatmusic.ui.chatgpt;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.databinding.ActivityHomeBinding;
import com.hzc.coolcatmusic.databinding.FloatingChatBinding;
import com.hzc.coolcatmusic.ui.main.HomeViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ChatFloating extends BaseActivity<FloatingChatBinding, ChatFloatingViewModel<DemoRepository>> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.floating_chat;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ChatFloatingViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(ChatFloatingViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        if (!checkOverlayDisplayPermission()) {
            Toast.makeText(getApplicationContext(), "请允许应用显示悬浮窗", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private boolean checkOverlayDisplayPermission() {
        // API23以后需要检查权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        } else {
            return true;
        }
    }
}
