package com.hzc.coolcatmusic.ui.main;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.event.ChangeFragmentEvent;
import com.hzc.coolcatmusic.event.IsOpenDrawerLayoutEvent;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class HomeViewModel<M extends BaseModel> extends BaseViewModel<M> {

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


    public HomeViewModel(@NonNull Application application, M model) {
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
