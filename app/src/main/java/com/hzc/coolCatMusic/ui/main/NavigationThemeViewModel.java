package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class NavigationThemeViewModel extends BaseViewModel<DemoRepository> {
    public NavigationThemeViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationThemeViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public SingleLiveEvent<Boolean> isCheck = new SingleLiveEvent<>();
}
