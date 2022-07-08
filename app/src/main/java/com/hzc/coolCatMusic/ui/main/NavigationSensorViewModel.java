package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class NavigationSensorViewModel extends BaseViewModel<DemoRepository> {
    public NavigationSensorViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationSensorViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }
}
