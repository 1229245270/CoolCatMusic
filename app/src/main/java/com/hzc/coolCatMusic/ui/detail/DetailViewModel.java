package com.hzc.coolCatMusic.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolCatMusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class DetailViewModel extends ToolbarViewModel<DemoRepository> {
    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public DetailViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }
}
