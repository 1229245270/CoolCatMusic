package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.ui.homefragment1.LocalMusicFragment;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;

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
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }

    SingleLiveEvent<Boolean> isRequestRead = new SingleLiveEvent<>();

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            if(entity instanceof HomeFragment1ItemEntity){
                startFragment(LocalMusicFragment.getInstance(),null);
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

}
