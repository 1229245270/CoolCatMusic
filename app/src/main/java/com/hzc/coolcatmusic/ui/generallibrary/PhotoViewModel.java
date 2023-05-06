package com.hzc.coolcatmusic.ui.generallibrary;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class PhotoViewModel extends BaseViewModel<DemoRepository> {
    public PhotoViewModel(@NonNull Application application) {
        super(application);
    }

    public PhotoViewModel(@NonNull Application application, DemoRepository repository) {
        super(application, repository);
    }
}
