package com.hzc.coolCatMusic.ui.costom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.SPUtilsConfig;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.ui.adapter.BaseRecycleAdapter;
import com.hzc.coolCatMusic.ui.adapter.BaseRecycleViewHolder;

import java.util.List;

import me.goldze.mvvmhabit.utils.SPUtils;

public class PlayingListDialog extends Dialog {

    private TextView textView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private List<PlayingMusicEntity> playingMusicEntityList;

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
    }

    private void initPlayingList(){
        playingMusicEntityList = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();

        BaseRecycleAdapter<PlayingMusicEntity> adapter = new BaseRecycleAdapter<PlayingMusicEntity>(getContext(),playingMusicEntityList,R.layout.dialog_playing_list){

            @Override
            public void convert(BaseRecycleViewHolder holder, PlayingMusicEntity item, int position) {

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
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecycleViewTouchHelper<PlayingMusicEntity>(playingMusicEntityList,adapter));
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
        onWindowAttributesChanged(lp);
    }
}
