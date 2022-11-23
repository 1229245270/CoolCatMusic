package com.hzc.coolCatMusic.ui.homefragment1;

import static com.hzc.coolCatMusic.app.SPUtilsConfig.Theme_TEXT_FONT_ID;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.Font;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.adapter.SongAdapter;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;
import com.hzc.coolCatMusic.utils.FileUtil;
import com.hzc.coolCatMusic.utils.LocalUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class LocalMusicViewModel extends ToolbarViewModel<DemoRepository> {

    public static final String TOKEN_LOCAL_MUSIC_SET_RESULT = "";
    public LocalMusicViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);

        Messenger.getDefault().register(this, TOKEN_LOCAL_MUSIC_SET_RESULT, new BindingAction() {
            @Override
            public void call() {
                isRequestRead.setValue(false);
            }
        });
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
                    public void accept(PlayingMusicEntity playingMusicEntity){
                        if(nowPlay != null && nowPlay == playingMusicEntity){
                            return;
                        }
                        nowPlay = playingMusicEntity;
                        if(oldPosition != -1){
                            localSongAdapter.notifyItemChanged(oldPosition);
                        }
                        for(int i = 0;i < localSongList.size();i++){
                            Object entity = localSongList.get(i);
                            if(entity instanceof LocalSongEntity){
                                LocalSongEntity localSongEntity = (LocalSongEntity) entity;
                                if(playingMusicEntity.getSrc().equals(localSongEntity.getPath())){
                                    localSongAdapter.notifyItemChanged(i);
                                    oldPosition = i;
                                    return;
                                }
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

    public SingleLiveEvent<Boolean> isRequestRead = new SingleLiveEvent<>();

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
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
                savePlayingSongs();
                MusicConnection.musicInterface.play(intent,((LocalSongEntity) entity).getPath(),position);
            }else{
                isRequestRead.setValue(false);
            }
        }
    };

    private void savePlayingSongs(){
        AppApplication.daoSession.getPlayingMusicEntityDao().deleteAll();
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

    public SongAdapter<Object> localSongAdapter = new SongAdapter<Object>() {
        @Override
        public void setOldPosition(int position) {
            oldPosition = position;
        }
    };

    public ObservableList<Object> localSongList = new ObservableArrayList<Object>();

    public OnItemBind<Object> localSongBind = new OnItemBind<Object>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Object item) {
            if(item instanceof String){
                itemBinding.set(BR.item, R.layout.item_song_total);
            }else if(item instanceof Boolean){
                itemBinding.set(BR.item,R.layout.item_song_button)
                        .bindExtra(BR.position,position)
                        .bindExtra(BR.onItemClickListener,onItemClickListener);
            }else{
                itemBinding.set(BR.item,R.layout.item_song)
                        .bindExtra(BR.position,position)
                        .bindExtra(BR.onItemClickListener,onItemClickListener);
            }
        }
    };

    @Override
    protected void rightIconOnClick() {
        super.rightIconOnClick();
        startFragment(new ScanningMusicFragment(),null);
    }

    public void loadLocalSong(Activity activity){
        Observable<List<Object>> observable = Observable.create(new ObservableOnSubscribe<List<Object>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Object>> emitter) throws Exception {
                List<Object> list = new ArrayList<>(LocalUtils.getAllMediaList(activity, 60, 0));
                list.add("共" + list.size() + "首");
                emitter.onNext(list);
            }
        }).map(new Function<List<Object>, List<Object>>() {
            @Override
            public List<Object> apply(@NonNull List<Object> objects) throws Exception {
                return objects;
            }
        });
        model.requestApi(new Function<Integer, ObservableSource<List<Object>>>() {
            @Override
            public ObservableSource<List<Object>> apply(@NonNull Integer integer) throws Exception {
                return observable;
            }
        }, new Observer<List<Object>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull List<Object> list) {
                //还原当前正在播放的歌曲
                LocalSongEntity localSongEntity = null;
                for(int i = 0;i < localSongList.size();i++){
                    if(localSongList.get(i) instanceof LocalSongEntity && ((LocalSongEntity) localSongList.get(i)).isCheck()){
                        localSongEntity = (LocalSongEntity) localSongList.get(i);
                        break;
                    }
                }
                if(localSongEntity != null){
                    for(int i = 0;i < list.size();i++){
                        if(list.get(i) instanceof LocalSongEntity && ((LocalSongEntity) list.get(i)).getPath().equals(localSongEntity.getPath())){
                            list.set(i,localSongEntity);
                            break;
                        }
                    }
                }
                localSongList.clear();
                localSongList.addAll(list);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
