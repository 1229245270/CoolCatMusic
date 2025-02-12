package com.hzc.coolcatmusic.ui.homefragment1;

import android.app.Application;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.reflect.TypeToken;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.SPUtilsConfig;
import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.ExpandedTabEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolcatmusic.ui.adapter.BaseRecycleViewHolder;
import com.hzc.coolcatmusic.ui.adapter.ExpandedTabAdapter;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.FileUtil;
import com.hzc.coolcatmusic.utils.LocalUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class ScanningMusicViewModel extends ToolbarViewModel<DemoRepository> {

    public ScanningMusicViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    String mainBg = "#" + Integer.toHexString(ContextCompat.getColor(getApplication(), me.goldze.mvvmhabit.R.color.mainBg) & 0x00ffffff);
    String redBg = "#" + Integer.toHexString(ContextCompat.getColor(getApplication(), me.goldze.mvvmhabit.R.color.red) & 0x00ffffff);

    public ObservableField<CharSequence> scanResultText = new ObservableField<>("");
    public int minDuration = SPUtils.getInstance().getInt(SPUtilsConfig.SCAN_MIN_DURATION,60);
    public int minSize = SPUtils.getInstance().getInt(SPUtilsConfig.SCAN_MIN_SIZE,100);
    public ObservableField<CharSequence> minDurationText = new ObservableField<>("");
    public ObservableField<CharSequence> minSizeText = new ObservableField<>("");

    /**
     * 破解按钮状态
     */
    public enum DownloadStatus{
        //未执行
        UN_EXECUTED,
        //破解中
        UNLOCKING,
        //破解成功
        UNLOCK_SUCCESS,
        //破解失败
        UNLOCK_FAIL,
        //下载成功
        DOWNLOAD_SUCCESS,
        //下载失败
        DOWNLOAD_FAIL
    }

    /**
     * 界面状态
     */
    public enum ViewStatus{
        //默认状态
        DEFAULT,
        //搜索中
        SEARCHING,
        //搜索结束
        SEARCH_END,
        //下载中
        DOWNLOADING,
        //结束
        DOWNLOAD_END,
        //下载失败
        DOWNLOAD_FAIL
    }
    public SingleLiveEvent<DownloadStatus> downloadStatus = new SingleLiveEvent<>();
    public SingleLiveEvent<ViewStatus> viewStatus = new SingleLiveEvent<>();

    public void setSeekBarText(){
        minDurationText.set(Html.fromHtml("" +
                "<font color='#000000'>扫描大于</font>" +
                "<font color='" + mainBg + "' size=30>" + minDuration + "秒</font>" +
                "<font color='#000000'>以上的音频文件</font>",Html.FROM_HTML_MODE_COMPACT));
        minSizeText.set(Html.fromHtml("" +
                "<font color='#000000'>扫描大于</font>" +
                "<font color='" + mainBg + "' size=30>" + minSize + "KM</font>" +
                "<font color='#000000'>以上的音频文件</font>",Html.FROM_HTML_MODE_COMPACT));
    }

    public List<String> unlockSongList = new ArrayList<>();
    public ExpandedTabAdapter adapter = new ExpandedTabAdapter(false) {
        @Override
        public void initChildRecycleView(Context context, RecyclerView recyclerView, List<Object> list,int basePosition) {
            BaseRecycleAdapter<Object> childAdapter = new BaseRecycleAdapter<Object>(context, list,R.layout.item_song) {
                @Override
                public void convert(BaseRecycleViewHolder holder, Object item, int position) {
                    TextView songName = holder.getView(R.id.songName);
                    TextView singer = holder.getView(R.id.singer);
                    ImageView songImage = holder.getView(R.id.songImage);
                    LinearLayout songItem = holder.getView(R.id.songItem);
                    ImageView ivCheck = holder.getView(R.id.ivCheck);

                    songImage.setVisibility(View.GONE);

                    final String path;
                    if(item instanceof LocalSongEntity){
                        ivCheck.setVisibility(View.GONE);
                        LocalSongEntity entity = (LocalSongEntity) item;
                        songName.setText(entity.getAlbums());
                        singer.setText(entity.getArtist());
                        songName.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                        path = entity.getPath();
                    }else if(item instanceof File){
                        ivCheck.setVisibility(View.VISIBLE);
                        File file = (File) item;
                        songName.setText(file.getName());
                        singer.setVisibility(View.GONE);
                        path = file.getPath();
                    }else{
                        path = null;
                    }
                    if(StringUtils.isContain(unlockSongList,path)){
                        ivCheck.setImageResource(R.drawable.ic_checkbox_selected);
                    }else{
                        ivCheck.setImageResource(R.drawable.ic_checkbox_unselected);
                    }
                    singer.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                    songItem.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.recycleview_item_unselect,null));
                }
            };
            childAdapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Object object, int position) {
                    if(object instanceof LocalSongEntity){
                        LocalSongEntity entity = (LocalSongEntity) object;
                        String path = entity.getPath();
                        if(StringUtils.isContain(unlockSongList,path)){
                            unlockSongList.remove(path);
                        }else{
                            unlockSongList.add(path);
                        }
                    }else if(object instanceof File){
                        File file = (File) object;
                        String path = file.getPath();
                        if(StringUtils.isContain(unlockSongList,path)){
                            unlockSongList.remove(path);
                        }else{
                            unlockSongList.add(path);
                        }
                    }
                    adapter.notifyItemChanged(basePosition,0);
                }

                @Override
                public void onItemLongClick(Object object, int position) {

                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(childAdapter);
        }

        @Override
        public void initRecycleView(View view, int position, ExpandedTabEntity<Object> item) {
            ImageView ivCheck = view.findViewById(R.id.ivCheck);

            final List<Object> list = item.getList();
            boolean isCheck = true;
            if(list.size() > 0) {
                Object object = list.get(0);
                if(object instanceof LocalSongEntity){
                    ivCheck.setVisibility(View.GONE);
                    for(int i = 0;i < list.size();i++){
                        LocalSongEntity entity = (LocalSongEntity) list.get(i);
                        String path = entity.getPath();
                        if(!StringUtils.isContain(unlockSongList,path)){
                            isCheck = false;
                            break;
                        }
                    }
                }else if(object instanceof File){
                    ivCheck.setVisibility(View.VISIBLE);
                    for(int i = 0;i < list.size();i++){
                        File entity = (File) list.get(i);
                        String path = entity.getPath();
                        if(!StringUtils.isContain(unlockSongList,path)){
                            isCheck = false;
                            break;
                        }
                    }
                }
            }else{
                ivCheck.setVisibility(View.GONE);
            }
            if(isCheck){
                ivCheck.setImageResource(R.drawable.ic_checkbox_selected);
                ivCheck.setTag(R.drawable.ic_checkbox_selected);
            }else{
                ivCheck.setImageResource(R.drawable.ic_checkbox_unselected);
                ivCheck.setTag(R.drawable.ic_checkbox_unselected);
            }
            ivCheck.setOnClickListener(v -> {
                boolean b = ivCheck.getTag().equals(R.drawable.ic_checkbox_selected);
                if(list.size() > 0){
                    Object object = list.get(0);
                    if(object instanceof LocalSongEntity){
                        for(int i = 0;i < list.size();i++){
                            LocalSongEntity localSongEntity = (LocalSongEntity) list.get(i);
                            String path = localSongEntity.getPath();
                            if(!b){
                                if(!StringUtils.isContain(unlockSongList, path)){
                                    unlockSongList.add(path);
                                }
                            }else{
                                if(StringUtils.isContain(unlockSongList, path)){
                                    unlockSongList.remove(path);
                                }
                            }
                        }
                    }else if(object instanceof File){
                        for(int i = 0;i < list.size();i++){
                            File file = (File) list.get(i);
                            String path = file.getPath();
                            if(!b){
                                if(!StringUtils.isContain(unlockSongList, path)){
                                    unlockSongList.add(path);
                                }
                            }else{
                                if(StringUtils.isContain(unlockSongList, path)){
                                    unlockSongList.remove(path);
                                }
                            }
                        }
                    }
                    adapter.notifyItemChanged(position,0);
                }
            });
        }

    };

    public ObservableList<ExpandedTabEntity<Object>> list = new ObservableArrayList<>();

    public OnItemBind<ExpandedTabEntity<Object>> itemBind = new OnItemBind<ExpandedTabEntity<Object>>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, ExpandedTabEntity<Object> item) {
            itemBinding.set(BR.item,R.layout.item_expanded_tab)
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

    public BindingCommand<Boolean> finish = new BindingCommand<Boolean>(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });

    public void startScan(){
        viewStatus.setValue(ViewStatus.SEARCHING);
        model.requestApi(LocalUtils.getLocalMusicObservable(getApplication(), minDuration, minSize), new DemoRepository.RequestCallback<List<LocalSongEntity>>() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(List<LocalSongEntity> localSongEntities) {
                int lockSize = 0;
                int totalSize = 0;
                List<ExpandedTabEntity<Object>> expandedTabEntities;
                ExpandedTabEntity<Object> expandedTabEntity;
                List<Object> itemList;
                int size;
                expandedTabEntities = new ArrayList<>();


                expandedTabEntity = new ExpandedTabEntity<>();
                List<File> kgMusic = FileUtil.getMusicFiles(FileUtil.KG_MUSIC);
                itemList = new ArrayList<>(kgMusic);
                expandedTabEntity.setList(itemList);
                size = itemList.size();
                expandedTabEntity.setTitle("酷狗音乐(" + size + "首)");
                totalSize += size;
                lockSize += size;
                expandedTabEntities.add(expandedTabEntity);

                expandedTabEntity = new ExpandedTabEntity<>();
                List<File> wyyMusic = FileUtil.getMusicFiles(FileUtil.WYY_MUSIC);
                itemList = new ArrayList<>(wyyMusic);
                expandedTabEntity.setList(itemList);
                size = itemList.size();
                expandedTabEntity.setTitle("网易云音乐(" + size + "首)");
                totalSize += size;
                lockSize += size;
                expandedTabEntities.add(expandedTabEntity);

                expandedTabEntity = new ExpandedTabEntity<>();
                itemList = new ArrayList<>(localSongEntities);
                expandedTabEntity.setList(itemList);
                size = itemList.size();
                expandedTabEntity.setTitle("本地音乐(" + size + "首)");
                totalSize += size;
                expandedTabEntities.add(expandedTabEntity);

                list.clear();
                list.addAll(expandedTabEntities);
                //scanText.set(String.valueOf(list.size() + file.size()));
                scanResultText.set(Html.fromHtml("" +
                        "<font color='#000000'>共搜索到</font>" +
                        "<font color='" + mainBg + "' size=30>" + totalSize + "首</font>" +
                        "<font color='#000000'>音乐，其中加密音乐</font>" +
                        "<font color='" + redBg + "' size=30>" + lockSize + "首</font>" +
                        "<font color='#000000'>。</font>",Html.FROM_HTML_MODE_COMPACT));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                viewStatus.setValue(ViewStatus.SEARCH_END);
            }
        });
    }

    public SingleLiveEvent<List<String>> downloadFileEvent = new SingleLiveEvent<>();
    /**
     * 破解音乐
     */
    public void unlockSong(){
        viewStatus.setValue(ViewStatus.DOWNLOADING);
        downloadStatus.setValue(DownloadStatus.UNLOCKING);
        List<File> files = new ArrayList<>();
        for(String unlockSong : unlockSongList){
            File file = new File(unlockSong);
            files.add(file);
        }
        model.requestApi(model.songUnlockWindow64Multiple(files, "zhangsan"), new DemoRepository.RequestCallback<BaseBean>() {
            @Override
            public void onBefore() {

            }

            @Override
            public void onSuccess(BaseBean baseBean) {
                List<String> list = baseBean.getResultList(new TypeToken<List<String>>(){});
                downloadFileEvent.setValue(list);
                downloadStatus.setValue(DownloadStatus.UNLOCK_SUCCESS);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort(e.getMessage());
                downloadStatus.setValue(DownloadStatus.UNLOCK_FAIL);
                viewStatus.setValue(ViewStatus.DOWNLOAD_FAIL);
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
