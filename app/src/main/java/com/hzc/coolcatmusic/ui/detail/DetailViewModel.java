package com.hzc.coolcatmusic.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;

public class DetailViewModel extends ToolbarViewModel<DemoRepository> {
    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public DetailViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

}
