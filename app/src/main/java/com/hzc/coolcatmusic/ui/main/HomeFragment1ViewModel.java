package com.hzc.coolcatmusic.ui.main;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.ExpandedTabEntity;
import com.hzc.coolcatmusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.ui.adapter.ExpandedTabAdapter;
import com.hzc.coolcatmusic.ui.homefragment1.LocalMusicFragment;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.LocalUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class HomeFragment1ViewModel extends HomeViewModel<DemoRepository> {

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
                            oldPosition = expandedTabAdapter.getOldPosition();
                        }
                        if(oldPosition != -1){
                            expandedTabAdapter.getAdapter(0).notifyItemChanged(oldPosition);
                        }
                        for(int i = 0; i < expandedTabEntityList.get(0).getList().size(); i++){
                            LocalSongEntity entity = (LocalSongEntity) expandedTabEntityList.get(0).getList().get(i);
                            if(playingMusicEntity.getSrc().equals(entity.getPath())){
                                expandedTabAdapter.getAdapter(0).notifyItemChanged(i);
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

    SingleLiveEvent<Boolean> openCeShiActivity = new SingleLiveEvent<>();

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            if(position == 0){
                startFragment(new LocalMusicFragment(),null);
            }else if(position == 2){
                startFragment(new LocalMusicFragment(),null);
            }
            /*if(entity instanceof HomeFragment1ItemEntity){
                startFragment(new LocalMusicFragment(),null);
            }*/
        }
    };

    public ObservableList<HomeFragment1ItemEntity> itemEntityList = new ObservableArrayList<>();

    public OnItemBind<HomeFragment1ItemEntity> itemEntityOnItemBind = new OnItemBind<HomeFragment1ItemEntity>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, HomeFragment1ItemEntity item) {
            itemBinding.set(BR.item, R.layout.item_fragment_home1)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    public ObservableList<ExpandedTabEntity<Object>> expandedTabEntityList = new ObservableArrayList<>();

    public OnItemBind<ExpandedTabEntity<Object>> listenerEntityOnItemBind = new OnItemBind<ExpandedTabEntity<Object>>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, ExpandedTabEntity<Object> item) {
            itemBinding.set(BR.item,R.layout.item_expanded_tab)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    public ExpandedTabAdapter expandedTabAdapter = new ExpandedTabAdapter(){
        @Override
        public void initChildRecycleView(Context context,RecyclerView recyclerView, List<Object> list,int position) {

        }

        @Override
        public void initRecycleView(View view, int position, ExpandedTabEntity<Object> item) {

        }
    };

    public void loadLocalSongTop3(){
        model.requestApi(LocalUtils.getDefaultLocalMusicObservable(getApplication(), 3), new DemoRepository.RequestCallback<List<LocalSongEntity>>() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(List<LocalSongEntity> localSongEntities) {
                List<Object> objectList = new ArrayList<>(localSongEntities);
                expandedTabEntityList.get(0).setList(objectList);
                expandedTabAdapter.notifyItemChanged(0);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void loadSong(){
        model.requestApi(model.settingFont(), new DemoRepository.RequestCallback<BaseBean>() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(BaseBean baseBean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
