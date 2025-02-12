package com.hzc.coolcatmusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class HomeFragment3ViewModel extends BaseViewModel<DemoRepository> {

    public HomeFragment3ViewModel(@NonNull Application application) {
        super(application);
    }

    public HomeFragment3ViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }
}
