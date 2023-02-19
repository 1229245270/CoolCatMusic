package com.hzc.coolcatmusic.ui.homefragment1;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.ExpandedTabEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleViewHolder;
import com.hzc.coolcatmusic.ui.adapter.ExpandedTabAdapter;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.DaoUtils.MusicUtils;
import com.hzc.coolcatmusic.utils.FileUtil;
import com.hzc.coolcatmusic.utils.LocalUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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
import me.goldze.mvvmhabit.utils.StringUtils;
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


    public ExpandedTabAdapter adapter = new ExpandedTabAdapter() {
        @Override
        public void initChildRecycleView(Context context, RecyclerView recyclerView, List<Object> list) {
            BaseRecycleAdapter<Object> adapter = new BaseRecycleAdapter<Object>(context, list,R.layout.item_song_no_image) {
                @Override
                public void convert(BaseRecycleViewHolder holder, Object item, int position) {
                    TextView songName = holder.getView(R.id.songName);
                    TextView singer = holder.getView(R.id.singer);
                    LinearLayout songItem = holder.getView(R.id.songItem);
                    if(item instanceof LocalSongEntity){
                        LocalSongEntity entity = (LocalSongEntity) item;
                        songName.setText(entity.getAlbums());
                        singer.setText(entity.getArtist());songName.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                    }else if(item instanceof File){
                        File file = (File) item;
                        songName.setText(file.getName());
                        //singer.setText(file.getPath());
                    }
                    singer.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                    songItem.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.recycleview_item_unselect,null));
                }
            };
            adapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Object object, int position) {
                }

                @Override
                public void onItemLongClick(Object object, int position) {

                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
    };

    public ObservableList<ExpandedTabEntity<Object>> list = new ObservableArrayList<>();

    public OnItemBind<ExpandedTabEntity<Object>> itemBind = new OnItemBind<ExpandedTabEntity<Object>>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, ExpandedTabEntity<Object> item) {
            itemBinding.set(BR.item,R.layout.item_listener)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };
    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {

        }
    };

    public SingleLiveEvent<Boolean> scanEvent = new SingleLiveEvent<>();
    public BindingCommand<Boolean> startScan = new BindingCommand<Boolean>(new BindingAction() {
        @Override
        public void call() {
            scanEvent.setValue(true);
        }
    });

    public void startScan(){
        /*
        List<LocalSongEntity> list = LocalUtils.getAllMediaList(getApplication(),timeSwitch.get() ? 60 : 0, sizeSwitch.get() ? 0.1 : 0);
        scanText.set(String.valueOf(list.size()));
        List<File> file = FileUtil.getAllFiles();
        scanText.set(String.valueOf(list.size() + file.size()));
        select = 0;
        successSize = 0;
        failSize = 0;
        files = file;

        startFragment(new ScanningMusicFragment(),null);
        unlockSong();*/
        scanningMusic();
    }

    private int select;
    private int successSize;
    private int failSize;
    private List<File> files;

    private void scanningMusic(){
        model.requestApi(new Function<Integer, ObservableSource<List<LocalSongEntity>>>() {
            @Override
            public ObservableSource<List<LocalSongEntity>> apply(@NonNull Integer integer) throws Exception {
                return LocalUtils.getLocalMusicObservable(getApplication(), 60, 0.1);
            }
        }, new Observer<List<LocalSongEntity>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                isScan.set(true);
            }

            @Override
            public void onNext(@NonNull List<LocalSongEntity> localSongEntities) {
                List<ExpandedTabEntity<Object>> expandedTabEntities = new ArrayList<>();
                ExpandedTabEntity<Object> expandedTabEntity = new ExpandedTabEntity<>();
                List<Object> itemList = new ArrayList<>(localSongEntities);
                expandedTabEntity.setList(itemList);
                expandedTabEntity.setTitle("本地音乐(" + itemList.size() + "首)");
                expandedTabEntities.add(expandedTabEntity);

                expandedTabEntity = new ExpandedTabEntity<>();
                List<File> kgMusic = FileUtil.getMusicFiles(FileUtil.KG_MUSIC);
                itemList = new ArrayList<>(kgMusic);
                expandedTabEntity.setList(itemList);
                expandedTabEntity.setTitle("酷狗音乐(" + itemList.size() + "首)");
                expandedTabEntities.add(expandedTabEntity);

                expandedTabEntity = new ExpandedTabEntity<>();
                List<File> wyyMusic = FileUtil.getMusicFiles(FileUtil.WYY_MUSIC);
                itemList = new ArrayList<>(wyyMusic);
                expandedTabEntity.setList(itemList);
                expandedTabEntity.setTitle("网易云音乐(" + itemList.size() + "首)");
                expandedTabEntities.add(expandedTabEntity);
                list.clear();
                list.addAll(expandedTabEntities);
                scanText.set(String.valueOf(list.size()));
                //scanText.set(String.valueOf(list.size() + file.size()));
                select = 0;
                successSize = 0;
                failSize = 0;
                //files = file;
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                isScan.set(false);
            }
        });
    }

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
