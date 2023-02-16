package com.hzc.coolcatmusic.ui.homefragment1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.utils.FileUtil;
import com.hzc.coolcatmusic.utils.LocalUtils;

import java.io.File;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.KLog;
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
            scanEvent.setValue(true);
        }
    });

    public void startScan(){
        isScan.set(true);
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

    private int select;
    private int successSize;
    private int failSize;
    private List<File> files;

    /**
     * 破解音乐
     */
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
                nextUnlock();
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void downloadUnlockSong(String url){
        try{
            //文件存放的路径
            String destFileDir = getApplication().getCacheDir().getPath();
            //文件存放的名称
            String destFileName = url.substring(url.lastIndexOf("/") + 1);

            DownLoadManager.getInstance().load(url, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {

                @Override
                public void onSuccess(ResponseBody responseBody) {
                    successSize++;
                    nextUnlock();
                }

                @Override
                public void progress(long progress, long total) {

                }

                @Override
                public void onError(Throwable e) {
                    failSize++;
                    nextUnlock();
                }
            });
        }catch (Exception e){
            failSize++;
            nextUnlock();
            KLog.e(e.toString());
        }
    }

    private void nextUnlock(){
        select++;
        unlockSuccessText.set(String.valueOf(successSize));
        unlockFailText.set(String.valueOf(failSize));
        if(select < files.size()){
            unlockSong();
        } else {
            Messenger.getDefault().sendNoMsg(LocalMusicViewModel.TOKEN_LOCAL_MUSIC_SET_RESULT);
            finish();
        }
    }
}
