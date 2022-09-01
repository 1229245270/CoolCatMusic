package com.hzc.coolCatMusic.ui.homefragment1;

import static android.os.Environment.getExternalStorageDirectory;

import android.app.Application;
import android.content.Intent;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolCatMusic.data.DemoRepository;
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
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;
import okhttp3.ResponseBody;

public class ScanningMusicViewModel extends ToolbarViewModel<DemoRepository> {

    public ScanningMusicViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public ObservableBoolean isScan = new ObservableBoolean(false);

    public ObservableBoolean unlockSwitch = new ObservableBoolean(true);
    public ObservableBoolean timeSwitch = new ObservableBoolean(true);
    public ObservableBoolean sizeSwitch = new ObservableBoolean(true);

    public ObservableField<String> scanText = new ObservableField<>("0");
    public ObservableField<String> unlockText = new ObservableField<>("0");
    public ObservableField<String> unlockSuccessText = new ObservableField<>("0");
    public ObservableField<String> unlockFailText = new ObservableField<>("0");

    public SingleLiveEvent<Boolean> scanEvent = new SingleLiveEvent<>();
    public BindingCommand<Boolean> startScan = new BindingCommand<Boolean>(new BindingAction() {
        @Override
        public void call() {
            isScan.set(true);
            scanEvent.setValue(true);
            List<LocalSongEntity> list = LocalUtils.getAllMediaList(getApplication(),timeSwitch.get() ? 60 : 0, sizeSwitch.get() ? 0.1 : 0);
            scanText.set(String.valueOf(list.size()));
            List<File> file = FileUtil.getAllFiles();
            scanText.set(String.valueOf(list.size() + file.size()));
            select = 0;
            successSize = 0;
            failSize = 0;
            files = file;
            unlockSong();
        }
    });

    private int select;
    private int successSize;
    private int failSize;
    private List<File> files;
    private void unlockSong(){
        model.requestApi(new Function<Integer, ObservableSource<BaseBean>>() {
            @Override
            public ObservableSource<BaseBean> apply(@NonNull Integer integer) throws Exception {
                return model.songUnlockWindow64(files.get(select),"zhangsan");
            }
        },new NetCallback<BaseBean>(){

            @Override
            public void onSuccess(BaseBean result) {
                downloadUnlockSong((String) result.getData());
            }

            @Override
            public void onFailure(String msg) {
                failSize++;
                select++;
                unlockSuccessText.set(String.valueOf(successSize));
                unlockFailText.set(String.valueOf(failSize));
                if(select < files.size()){
                    unlockSong();
                } else {
                    finish();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void downloadUnlockSong(String url){
        try{
            String destFileDir = getApplication().getCacheDir().getPath();  //文件存放的路径
            String destFileName = url.substring(url.lastIndexOf("/") + 1);//文件存放的名称
            DownLoadManager.getInstance().load(url, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {

                @Override
                public void onSuccess(ResponseBody responseBody) {


                    successSize++;
                    select++;
                    unlockSuccessText.set(String.valueOf(successSize));
                    unlockFailText.set(String.valueOf(failSize));
                    if(select < files.size()){
                        unlockSong();
                    } else {
                        finish();
                    }
                }

                @Override
                public void progress(long progress, long total) {

                }

                @Override
                public void onError(Throwable e) {
                    failSize++;
                    select++;
                    unlockSuccessText.set(String.valueOf(successSize));
                    unlockFailText.set(String.valueOf(failSize));
                    if(select < files.size()){
                        unlockSong();
                    } else {
                        finish();
                    }
                }
            });
        }catch (Exception e){
            failSize++;
            select++;
            unlockSuccessText.set(String.valueOf(successSize));
            unlockFailText.set(String.valueOf(failSize));
            if(select < files.size()){
                unlockSong();
            } else {
                finish();
            }
            KLog.e(e.toString());
        }

    }

}
