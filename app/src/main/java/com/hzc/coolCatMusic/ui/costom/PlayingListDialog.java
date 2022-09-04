package com.hzc.coolCatMusic.ui.costom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.SPUtilsConfig;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolCatMusic.ui.adapter.BaseRecycleViewHolder;
import com.hzc.coolCatMusic.utils.DaoUtils.MusicUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;

public class PlayingListDialog extends Dialog {

    private TextView textView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private List<PlayingMusicEntity> playingMusicEntityList;

    private Disposable musicSubscription;
    private RecycleViewTouchHelper recycleViewTouchHelper;
    private BaseRecycleAdapter<PlayingMusicEntity> adapter;

    public PlayingListDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_playing_list);
        setViewLocation();
        setCanceledOnTouchOutside(true);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recycleView);

        setPlayingOrder(false);
        textView.setOnClickListener(view -> {
            setPlayingOrder(true);
        });

        initPlayingList();
        registerRxBus();
    }

    private PlayingMusicEntity nowPlay;
    private int oldPosition = -1;

    private void registerRxBus(){
        musicSubscription = RxBus.getDefault().toObservable(PlayingMusicEntity.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlayingMusicEntity>() {
                    @Override
                    public void accept(PlayingMusicEntity playingMusicEntity) throws Exception {
                        if(nowPlay != null && nowPlay == playingMusicEntity){
                            return;
                        }
                        nowPlay = playingMusicEntity;
                        if(oldPosition != -1){
                            adapter.notifyItemChanged(oldPosition);
                        }
                        for(int i = 0;i < playingMusicEntityList.size();i++){
                            PlayingMusicEntity entity = playingMusicEntityList.get(i);
                            if(playingMusicEntity.getSrc().equals(entity.getSrc())){
                                adapter.notifyItemChanged(i);
                                oldPosition = i;
                                return;
                            }
                        }
                    }
                });
        RxSubscriptions.add(musicSubscription);
    }



    private void removeRxBus(){
        RxSubscriptions.remove(musicSubscription);
    }

    private void initPlayingList(){
        playingMusicEntityList = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();
        adapter = new BaseRecycleAdapter<PlayingMusicEntity>(getContext(),playingMusicEntityList,R.layout.item_playing_song){

            @Override
            public void convert(BaseRecycleViewHolder holder, PlayingMusicEntity item, int position) {
                holder.setText(R.id.songName,item.getSongName());
                holder.setText(R.id.singer,item.getSinger());
                KLog.d("position" + item.toString());
                PlayingMusicEntity entity = MusicUtils.getPlayingMusicEntity();
                if(entity != null && entity.getSrc().equals(item.getSrc())){
                    //修复bug:当歌曲暂停时进入,没有回传playingMusicEntity,刷新不了该条旧数据
                    if(oldPosition == -1){
                        oldPosition = position;
                    }
                    holder.setBackground(R.id.songItem,ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.recycleview_item_select,null));
                    holder.setTextColor(R.id.songName,ContextCompat.getColor(getContext(), R.color.item_songName_check));
                    holder.setTextColor(R.id.singer,ContextCompat.getColor(getContext(), R.color.item_singer_check));
                }else{
                    holder.setBackground(R.id.songItem,ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.recycleview_item_unselect,null));
                    holder.setTextColor(R.id.songName,ContextCompat.getColor(getContext(), R.color.black_text));
                    holder.setTextColor(R.id.singer,ContextCompat.getColor(getContext(), R.color.gray_text));
                }

                //长按图标滑动
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        recycleViewTouchHelper.setEdit(false);
                        return false;
                    }
                });
                holder.getView(R.id.songButton).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        recycleViewTouchHelper.setEdit(true);
                        return true;
                    }
                });
            }
        };
        adapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object object, int position) {
                Intent intent = new Intent();
                intent.putExtra(MusicService.SRC,((PlayingMusicEntity) object).getSrc());
                intent.putExtra(MusicService.ALL_NAME,((PlayingMusicEntity) object).getAllName());
                intent.putExtra(MusicService.SINGER,((PlayingMusicEntity) object).getSinger());
                intent.putExtra(MusicService.SINGER_IMAGE,((PlayingMusicEntity) object).getSingerImage());
                intent.putExtra(MusicService.SONG_NAME,((PlayingMusicEntity) object).getSongName());
                intent.putExtra(MusicService.SONG_IMAGE,((PlayingMusicEntity) object).getSongImage());
                intent.putExtra(MusicService.LYRICS,"");
                intent.putExtra(MusicService.YEAR_ISSUE,"");
                MusicConnection.musicInterface.play(intent,((PlayingMusicEntity) object).getSrc(),position);
            }

            @Override
            public void onItemLongClick(Object object, int position) {
                KLog.d("onItemLongClick");
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recycleViewTouchHelper = new RecycleViewTouchHelper<PlayingMusicEntity>(playingMusicEntityList, adapter) {
            @Override
            public void moveResult() {
                for(int i = 0;i < playingMusicEntityList.size();i++){
                    if(nowPlay.getSrc().equals(playingMusicEntityList.get(i).getSrc())){
                        SPUtils.getInstance().put(SPUtilsConfig.PLAYING_NUM,i);
                        AppApplication.daoSession.getPlayingMusicEntityDao().deleteAll();
                        for(PlayingMusicEntity entity : playingMusicEntityList){
                            AppApplication.daoSession.getPlayingMusicEntityDao().insert(entity);
                        }
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recycleViewTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setPlayingOrder(boolean isOnClick){
        int playingOrder = SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_ORDER);
        if(isOnClick){
            if(SPUtilsConfig.ORDER_PLAY == playingOrder){
                playingOrder = SPUtilsConfig.RANDOM_PLAY;
            }else if(SPUtilsConfig.RANDOM_PLAY == playingOrder){
                playingOrder = SPUtilsConfig.LOOP_PLAY;
            }else{
                playingOrder = SPUtilsConfig.ORDER_PLAY;
            }
            SPUtils.getInstance().put(SPUtilsConfig.PLAYING_ORDER,playingOrder);
        }
        Drawable drawable = null;
        String text = "";
        switch (playingOrder){
            case SPUtilsConfig.ORDER_PLAY:
                drawable = ContextCompat.getDrawable(getContext(),R.drawable.dialog_shunxu);
                text = "顺序播放";
                break;
            case SPUtilsConfig.RANDOM_PLAY:
                drawable = ContextCompat.getDrawable(getContext(),R.drawable.dialog_suiji);
                text = "随机播放";
                break;
            case SPUtilsConfig.LOOP_PLAY:
                drawable = ContextCompat.getDrawable(getContext(),R.drawable.dialog_xunhuan);
                text = "单曲循环";
                break;
        }
        if(drawable != null){
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            textView.setText(text);
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private void setViewLocation(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.x = 0;
        lp.y = height;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //去除dialog周围padding
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        onWindowAttributesChanged(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        removeRxBus();
    }
}
