package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.event.IsOpenDrawerLayoutEvent;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

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
