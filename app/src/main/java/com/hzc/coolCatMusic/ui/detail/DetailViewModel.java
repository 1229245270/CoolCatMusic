package com.hzc.coolCatMusic.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

public class DetailViewModel extends ToolbarViewModel<DemoRepository> {
    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public DetailViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

}
