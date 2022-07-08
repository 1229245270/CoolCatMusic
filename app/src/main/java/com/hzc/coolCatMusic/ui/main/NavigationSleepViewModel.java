package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class NavigationSleepViewModel extends BaseViewModel<DemoRepository> {
    public NavigationSleepViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationSleepViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }
}
