package com.hzc.coolcatmusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.event.IsOpenDrawerLayoutEvent;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;

public class HomeFragmentViewModel extends HomeViewModel {

    public String mainLogo = "https://profile.csdnimg.cn/6/E/0/0_weixin_44360546";
    //public ObservableInt mainPlaceholderRes = new ObservableInt(R.mipmap.ic_launcher);
    public int mainPlaceholderRes = R.mipmap.ic_launcher;


    public BindingCommand<Boolean> logoClick = new BindingCommand<Boolean>(() -> {
        RxBus.getDefault().post(new IsOpenDrawerLayoutEvent());
    });

    public HomeFragmentViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }


}
