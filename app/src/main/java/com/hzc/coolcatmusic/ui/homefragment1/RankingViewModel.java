package com.hzc.coolcatmusic.ui.homefragment1;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.ui.adapter.SongAdapter;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.DaoUtils.MusicUtils;
import com.hzc.coolcatmusic.utils.LocalUtils;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class RankingViewModel extends ToolbarViewModel<DemoRepository> {

    public RankingViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        KLog.d("onRestart");
        isRequestRead.setValue(true);
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
    }


    @Override
    public void removeRxBus() {
        super.removeRxBus();
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
                MusicConnection.musicInterface.play(intent,((LocalSongEntity) entity).getPath(),position);
            }else{
                isRequestRead.setValue(false);
            }
        }
    };

    @Override
    protected void rightIconOnClick() {
        super.rightIconOnClick();
    }

    @Override
    protected void rightTextOnClick() {
        super.rightTextOnClick();
        //startFragment(new ScanningMusicFragment(),null);

    }

    /*public void getInfo(){
        model.requestApi(new Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public ObservableSource<? extends Object> apply(Integer integer) throws Exception {
                return null;
            }
        }, new Observer<List<LocalSongEntity>>() {

        });
    }*/

}
