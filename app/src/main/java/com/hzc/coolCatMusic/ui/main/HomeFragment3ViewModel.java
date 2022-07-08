package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class HomeFragment3ViewModel extends BaseViewModel<DemoRepository> {

    public HomeFragment3ViewModel(@NonNull Application application) {
        super(application);
    }

    public HomeFragment3ViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }
}
