package com.hzc.coolCatMusic.ui.main;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.event.ChangeFragmentEvent;
import com.hzc.coolCatMusic.event.IsOpenDrawerLayoutEvent;
import com.hzc.coolCatMusic.ui.detail.DetailActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class HomeViewModel extends BaseViewModel<DemoRepository> {

    private Disposable musicSubscription;
    private Disposable isOpenDrawerSubscription;
    private Disposable changeFragmentSubscription;

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        musicSubscription = RxBus.getDefault().toObservable(PlayingMusicEntity.class)
                .subscribe(new Consumer<PlayingMusicEntity>() {
                    @Override
                    public void accept(PlayingMusicEntity playingMusicEntity) throws Exception {
                        changePlaying.postValue(playingMusicEntity);
                    }
                });
        isOpenDrawerSubscription = RxBus.getDefault().toObservable(IsOpenDrawerLayoutEvent.class)
                .subscribe(new Consumer<IsOpenDrawerLayoutEvent>() {
                    @Override
                    public void accept(IsOpenDrawerLayoutEvent isOpenDrawerLayoutEvent) throws Exception {
                        isOpenDrawerLayout.postValue(true);
                    }
                });
        changeFragmentSubscription = RxBus.getDefault().toObservable(ChangeFragmentEvent.class)
                .subscribe(new Consumer<ChangeFragmentEvent>() {
                    @Override
                    public void accept(ChangeFragmentEvent changeFragmentEvent) throws Exception {
                        changeFragment.postValue(changeFragmentEvent);
                    }
                });

        RxSubscriptions.add(musicSubscription);
        RxSubscriptions.add(isOpenDrawerSubscription);
        RxSubscriptions.add(changeFragmentSubscription);

    }

    public void startFragment(Fragment showFragment, Bundle bundle){
        ChangeFragmentEvent event = new ChangeFragmentEvent(showFragment,bundle);
        RxBus.getDefault().post(event);
    }

    /*public BindingCommand<Boolean> openDetailActivity = new BindingCommand<Boolean>(() -> {
        startActivity(DetailActivity.class);
    });*/

    //音乐条显示
    public SingleLiveEvent<PlayingMusicEntity> changePlaying = new SingleLiveEvent<>();

    //侧边栏显示
    public SingleLiveEvent<Boolean> isOpenDrawerLayout = new SingleLiveEvent<>();

    //切换fragment
    public SingleLiveEvent<ChangeFragmentEvent> changeFragment = new SingleLiveEvent<>();


    public HomeViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(musicSubscription);
        RxSubscriptions.remove(isOpenDrawerSubscription);
        RxSubscriptions.remove(changeFragmentSubscription);
    }
}
