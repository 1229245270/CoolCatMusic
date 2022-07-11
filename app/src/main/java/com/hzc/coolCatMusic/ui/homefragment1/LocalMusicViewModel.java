package com.hzc.coolCatMusic.ui.homefragment1;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.app.SPUtilsConfig;
import com.hzc.coolCatMusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.adapter.SongAdapter;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;
import com.hzc.coolCatMusic.ui.main.HomeFragmentViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class LocalMusicViewModel extends ToolbarViewModel<DemoRepository> {

    public LocalMusicViewModel(@NonNull Application application, DemoRepository model) {
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
        public void onItemClick(Object entity) {
            if(entity instanceof LocalSongEntity){
                Intent intent = new Intent();
                intent.putExtra(MusicService.SRC,((LocalSongEntity) entity).getPath());
                intent.putExtra(MusicService.ALL_NAME,((LocalSongEntity) entity).getDisplay_name());
                intent.putExtra(MusicService.SINGER,((LocalSongEntity) entity).getArtist());
                intent.putExtra(MusicService.SINGER_IMAGE,((LocalSongEntity) entity).getImage());
                intent.putExtra(MusicService.SONG_NAME,((LocalSongEntity) entity).getAlbums());
                intent.putExtra(MusicService.SONG_IMAGE,((LocalSongEntity) entity).getImage());
                intent.putExtra(MusicService.LYRICS,"");
                intent.putExtra(MusicService.YEAR_ISSUE,"");

                MusicConnection.musicInterface.play(intent,((LocalSongEntity) entity).getPath(),0);

            }else{
                isRequestRead.setValue(false);
            }
        }
    };

    private void savePlayingSongs(){
        for(int i = 0;i < localSongList.size();i++){
            if(localSongList.get(i) instanceof LocalSongEntity){
                PlayingMusicEntity entity = new PlayingMusicEntity();
                LocalSongEntity localSongEntity = (LocalSongEntity) localSongList.get(i);
                entity.setSrc(localSongEntity.getPath());
                entity.setAllName(localSongEntity.getDisplay_name());
                entity.setSinger(localSongEntity.getArtist());
                entity.setSingerImage(null);
                entity.setSongName(localSongEntity.getAlbums());
                entity.setSongImage(null);
                entity.setLyrics("");
                entity.setYearIssue("");
                AppApplication.daoSession.insert(entity);
            }
        }
    }

    public SongAdapter<Object> localSongAdapter = new SongAdapter<>();

    public ObservableList<Object> localSongList = new ObservableArrayList<Object>();

    public OnItemBind<Object> localSongBind = new OnItemBind<Object>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Object item) {
            if(item instanceof String){
                itemBinding.set(BR.item, R.layout.item_song_total);
            }else if(item instanceof Boolean){
                itemBinding.set(BR.item,R.layout.item_song_button).bindExtra(BR.onItemClickListener,onItemClickListener);
            }else{
                itemBinding.set(BR.item,R.layout.item_song).bindExtra(BR.onItemClickListener,onItemClickListener);
            }
        }
    };

}
