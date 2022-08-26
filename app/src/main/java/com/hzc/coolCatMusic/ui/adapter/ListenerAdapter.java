package com.hzc.coolCatMusic.ui.adapter;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.entity.ListenerEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.NetworkSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.main.HomeActivity;
import com.hzc.coolCatMusic.utils.AnimationUtils;
import com.hzc.coolCatMusic.utils.DaoUtils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public abstract class ListenerAdapter extends BindingRecyclerViewAdapter<ListenerEntity<Object>> {
    @NonNull
    @Override
    public ViewDataBinding onCreateBinding(@NonNull LayoutInflater inflater, int layoutId, @NonNull ViewGroup viewGroup) {
        return super.onCreateBinding(inflater, layoutId, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    private boolean isOpen = true;
    private int showHeight = 0;
    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, ListenerEntity<Object> item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycleView);
        LinearLayout llShow = binding.getRoot().findViewById(R.id.llShow);
        LinearLayout llMenu = binding.getRoot().findViewById(R.id.llMenu);
        LinearLayout llMore = binding.getRoot().findViewById(R.id.llMore);
        List<Object> list = item.getList();

        LinearLayout llShowView = binding.getRoot().findViewById(R.id.llShowView);
        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(showHeight == 0){
                    showHeight = llShowView.getMeasuredHeight();
                }
                //修复bug:布局高度为0时不执行动画
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llShowView.getLayoutParams();
                if(params.height == 0){
                    params.height = 1;
                    llShowView.setLayoutParams(params);
                }
                Animation animation;
                if(isOpen){
                    animation = new Animation() {
                        @Override
                        public boolean willChangeBounds() {
                            return super.willChangeBounds();
                        }

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            super.applyTransformation(interpolatedTime, t);
                            params.height = (int) (showHeight * (1 - interpolatedTime));
                            llShowView.setLayoutParams(params);
                        }
                    };
                }else{
                    animation = new Animation() {
                        @Override
                        public boolean willChangeBounds() {
                            return super.willChangeBounds();
                        }

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            super.applyTransformation(interpolatedTime, t);
                            params.height = (int) (showHeight * interpolatedTime);
                            llShowView.setLayoutParams(params);
                        }
                    };
                }
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //禁止点击
                        llShow.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isOpen = !isOpen;
                        llShow.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animation.setDuration(1000);
                llShowView.startAnimation(animation);
            }
        });

        BaseRecycleAdapter<Object> adapter;
        switch (item.getTip()){
            case "本地歌曲":
                adapter = new BaseRecycleAdapter<Object>(binding.getRoot().getContext(), list,R.layout.item_song) {
                    @Override
                    public void convert(BaseRecycleViewHolder holder, Object item, int position) {
                        ImageView imageView = holder.getView(R.id.songImage);
                        if(item instanceof LocalSongEntity){
                            Object image;
                            LocalSongEntity entity = (LocalSongEntity) item;
                            if(entity.getImage() != null){
                                image = entity.getImage();
                            }else{
                                image = R.drawable.ceshi;
                            }
                            Glide.with(binding.getRoot().getContext())
                                    .load(image)
                                    .into(imageView);
                            TextView songName = holder.getView(R.id.songName);
                            TextView singer = holder.getView(R.id.singer);
                            LinearLayout songItem = holder.getView(R.id.songItem);

                            PlayingMusicEntity playingMusicEntity = MusicUtils.getPlayingMusicEntity();
                            if(playingMusicEntity != null && playingMusicEntity.getSrc().equals(((LocalSongEntity) item).getPath())){
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_select,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_songName_check));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_singer_check));
                            }else{
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_unselect,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_songName_uncheck));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_singer_uncheck));
                            }

                        }
                    }
                };
                adapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Object object, int position) {
                        Intent intent = new Intent();
                        intent.putExtra(MusicService.SRC,((LocalSongEntity) object).getPath());
                        intent.putExtra(MusicService.ALL_NAME,((LocalSongEntity) object).getDisplay_name());
                        intent.putExtra(MusicService.SINGER,((LocalSongEntity) object).getArtist());
                        intent.putExtra(MusicService.SINGER_IMAGE,((LocalSongEntity) object).getImage());
                        intent.putExtra(MusicService.SONG_NAME,((LocalSongEntity) object).getAlbums());
                        intent.putExtra(MusicService.SONG_IMAGE,((LocalSongEntity) object).getImage());
                        intent.putExtra(MusicService.LYRICS,"");
                        intent.putExtra(MusicService.YEAR_ISSUE,"");
                        MusicConnection.musicInterface.play(intent,((LocalSongEntity) object).getPath(),position);
                    }

                    @Override
                    public void onItemLongClick(Object object, int position) {

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                recyclerView.setAdapter(adapter);
                break;
            case "每日推荐":
                adapter = new BaseRecycleAdapter<Object>(binding.getRoot().getContext(), list,R.layout.item_song) {
                    @Override
                    public void convert(BaseRecycleViewHolder holder, Object item, int position) {
                        ImageView imageView = holder.getView(R.id.songImage);
                        if(item instanceof NetworkSongEntity){
                            NetworkSongEntity entity = (NetworkSongEntity) item;

                            Glide.with(binding.getRoot().getContext())
                                    .load(entity.getSongImage())
                                    .into(imageView);
                            TextView songName = holder.getView(R.id.songName);
                            TextView singer = holder.getView(R.id.singer);
                            LinearLayout songItem = holder.getView(R.id.songItem);

                            PlayingMusicEntity playingMusicEntity = MusicUtils.getPlayingMusicEntity();
                            if(playingMusicEntity != null && playingMusicEntity.getSrc().equals(((NetworkSongEntity) item).getPath())){
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_select,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_songName_check));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_singer_check));
                            }else{
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_unselect,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_songName_uncheck));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_singer_uncheck));
                            }
                            songName.setText(entity.getSongName());
                            singer.setText(entity.getSingerName());
                        }
                    }
                };
                adapter.setOnItemClickListener(new BaseRecycleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Object object, int position) {
                        Intent intent = new Intent();
                        intent.putExtra(MusicService.SRC,((LocalSongEntity) object).getPath());
                        intent.putExtra(MusicService.ALL_NAME,((LocalSongEntity) object).getDisplay_name());
                        intent.putExtra(MusicService.SINGER,((LocalSongEntity) object).getArtist());
                        intent.putExtra(MusicService.SINGER_IMAGE,((LocalSongEntity) object).getImage());
                        intent.putExtra(MusicService.SONG_NAME,((LocalSongEntity) object).getAlbums());
                        intent.putExtra(MusicService.SONG_IMAGE,((LocalSongEntity) object).getImage());
                        intent.putExtra(MusicService.LYRICS,"");
                        intent.putExtra(MusicService.YEAR_ISSUE,"");
                        MusicConnection.musicInterface.play(intent,((LocalSongEntity) object).getPath(),position);
                    }

                    @Override
                    public void onItemLongClick(Object object, int position) {

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                //recyclerView.setAdapter(adapter);
                break;
            case "排行版":
                LinearLayout llRank = binding.getRoot().findViewById(R.id.llRank);
                ImageView ivRankOne = binding.getRoot().findViewById(R.id.ivRankOne);
                ImageView ivRankTwo = binding.getRoot().findViewById(R.id.ivRankTwo);
                ImageView ivRankThree = binding.getRoot().findViewById(R.id.ivRankThree);
                TextView tvRankOne = binding.getRoot().findViewById(R.id.tvRankOne);
                TextView tvRankTwo = binding.getRoot().findViewById(R.id.tvRankTwo);
                TextView tvRankThree = binding.getRoot().findViewById(R.id.tvRankThree);
                llRank.setVisibility(View.VISIBLE);

                ivRankOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KLog.d("11111111");
                        TextView textView = new TextView(binding.getRoot().getContext());
                        textView.setText("+1");
                        textView.setTextColor(Color.RED);
                        ValueAnimator animator = ValueAnimator.ofInt(10,200);
                        animator.setDuration(1000);
                        new AnimationUtils(llShow, textView, new AnimationUtils.ViewLikeClickListener() {
                            @Override
                            public void onClick(View view, boolean toggle, AnimationUtils animationUtils) {
                                KLog.d("11112222");
                                animationUtils.startLikeAnim(animator);
                            }
                        });
                    }
                });

                break;
            case "新歌版":
                break;
        }
    }

}


