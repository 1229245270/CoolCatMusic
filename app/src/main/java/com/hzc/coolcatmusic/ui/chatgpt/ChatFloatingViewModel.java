package com.hzc.coolcatmusic.ui.chatgpt;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChatFloatingViewModel<M extends BaseModel> extends BaseViewModel<M> {
    public ChatFloatingViewModel(@NonNull Application application) {
        super(application);
    }

    public ChatFloatingViewModel(@NonNull Application application, M model) {
        super(application, model);
    }
}
