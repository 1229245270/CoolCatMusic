package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.ListenerEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.ui.adapter.ListenerAdapter;
import com.hzc.coolCatMusic.ui.homefragment1.LocalMusicFragment;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class HomeFragment1ViewModel extends HomeViewModel {

    public HomeFragment1ViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    private Disposable mSubscription;
    private Disposable musicSubscription;

    private PlayingMusicEntity nowPlay;
    private int oldPosition = -1;
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.equals("onRestart")){
                            isRequestRead.setValue(true);
                        }
                    }
                });

        musicSubscription = RxBus.getDefault().toObservable(PlayingMusicEntity.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlayingMusicEntity>() {
                    @Override
                    public void accept(PlayingMusicEntity playingMusicEntity) throws Exception {
                        if(nowPlay != null && nowPlay == playingMusicEntity){
                            return;
                        }
                        nowPlay = playingMusicEntity;
                        if(oldPosition == -1){
                            oldPosition = listenerAdapter.getOldPosition();
                        }
                        if(oldPosition != -1){
                            listenerAdapter.getAdapter(0).notifyItemChanged(oldPosition);
                        }
                        for(int i = 0;i < listenerEntityList.get(0).getList().size();i++){
                            LocalSongEntity entity = (LocalSongEntity) listenerEntityList.get(0).getList().get(i);
                            if(playingMusicEntity.getSrc().equals(entity.getPath())){
                                listenerAdapter.getAdapter(0).notifyItemChanged(i);
                                oldPosition = i;
                                return;
                            }
                        }
                    }
                });

        RxSubscriptions.add(mSubscription);
        RxSubscriptions.add(musicSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(musicSubscription);
    }

    SingleLiveEvent<Boolean> isRequestRead = new SingleLiveEvent<>();

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            if(entity instanceof HomeFragment1ItemEntity){
                startFragment(new LocalMusicFragment(),null);
            }
        }
    };

    public ObservableList<HomeFragment1ItemEntity> itemEntityList = new ObservableArrayList<>();

    public OnItemBind<HomeFragment1ItemEntity> itemEntityOnItemBind = new OnItemBind<HomeFragment1ItemEntity>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, HomeFragment1ItemEntity item) {
            itemBinding.set(BR.item,R.layout.item_fragment_home1)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.onItemClickListener,onItemClickListener);

        }
    };

    public ObservableList<ListenerEntity<Object>> listenerEntityList = new ObservableArrayList<>();

    public OnItemBind<ListenerEntity<Object>> listenerEntityOnItemBind = new OnItemBind<ListenerEntity<Object>>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, ListenerEntity<Object> item) {
            itemBinding.set(BR.item,R.layout.item_listener)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    public ListenerAdapter listenerAdapter = new ListenerAdapter(){};


    public void loadSong(){


        /*model.requestApi(new Function<Integer, ObservableSource<BaseBean>>() {
            @Override
            public ObservableSource<BaseBean> apply(@NonNull Integer integer) throws Exception {
                return model.settingFont();
            }
        },new NetCallback<BaseBean>(){
            @Override
            public void onSuccess(BaseBean result) {
            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onFinish() {

            }
        });*/
    }
}
