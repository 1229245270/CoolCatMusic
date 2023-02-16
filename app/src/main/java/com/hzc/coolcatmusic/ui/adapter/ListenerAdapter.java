package com.hzc.coolcatmusic.ui.adapter;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.entity.ListenerEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.NetworkSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.ui.costom.NiceImageView;
import com.hzc.coolcatmusic.utils.AnimationUtils;
import com.hzc.coolcatmusic.utils.DaoUtils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<Integer,Integer> showHeightMap = new HashMap<>();
    private final Map<Integer,Boolean> IsOpenMap = new HashMap<>();
    private final Map<Integer,BaseRecycleAdapter<Object>> adapterMap = new HashMap<>();

    public BaseRecycleAdapter<Object> getAdapter(int position){
        return adapterMap.get(position);
    }
    private int oldPosition = -1;
    public int getOldPosition(){
        return oldPosition;
    }

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, ListenerEntity<Object> item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycleView);
        LinearLayout llShow = binding.getRoot().findViewById(R.id.llShow);
        LinearLayout llMenu = binding.getRoot().findViewById(R.id.llMenu);
        LinearLayout llMore = binding.getRoot().findViewById(R.id.llMore);
        List<Object> list = item.getList();
        if(list == null){
            list = new ArrayList<>();
        }

        LinearLayout llShowView = binding.getRoot().findViewById(R.id.llShowView);
        llShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeightMap.putIfAbsent(position,llShowView.getMeasuredHeight());
                //修复bug:布局高度为0时不执行动画
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llShowView.getLayoutParams();
                if(params.height == 0){
                    params.height = 1;
                    llShowView.setLayoutParams(params);
                }
                Animation animation;

                IsOpenMap.putIfAbsent(position, true);
                Boolean isOpen = IsOpenMap.get(position);
                if(isOpen != null){
                    if(isOpen){
                        animation = new Animation() {
                            @Override
                            public boolean willChangeBounds() {
                                return super.willChangeBounds();
                            }

                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                super.applyTransformation(interpolatedTime, t);
                                Integer value = showHeightMap.get(position);
                                if(value != null){
                                    params.height = (int) (value * (1 - interpolatedTime));
                                    llShowView.setLayoutParams(params);
                                }
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
                                Integer value = showHeightMap.get(position);
                                if(value != null){
                                    params.height = (int) (value * interpolatedTime);
                                    llShowView.setLayoutParams(params);
                                }
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
                            IsOpenMap.put(position, !isOpen);
                            llShow.setEnabled(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    animation.setDuration(300);
                    llShowView.startAnimation(animation);
                }
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
                            songName.setText(entity.getAlbums());
                            singer.setText(entity.getArtist());

                            PlayingMusicEntity playingMusicEntity = MusicUtils.getPlayingMusicEntity();
                            if(playingMusicEntity != null && playingMusicEntity.getSrc().equals(((LocalSongEntity) item).getPath())){
                                //修复bug:当歌曲暂停时进入,没有回传playingMusicEntity,刷新不了该条旧数据
                                if(oldPosition == -1){
                                    oldPosition = position;
                                }
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_select,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_songName_check));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.item_singer_check));
                            }else{
                                songItem.setBackground(ResourcesCompat.getDrawable(binding.getRoot().getResources(),R.drawable.recycleview_item_unselect,null));
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.black_text));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_text));
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
                adapterMap.put(0,adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                recyclerView.setAdapter(adapterMap.get(0));
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
                                songName.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.black_text));
                                singer.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_text));
                            }
                            songName.setText(entity.getSongName());
                            singer.setText(entity.getSingerName());
                        }
                    }
                };
                adapterMap.put(1,adapter);
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
                recyclerView.setAdapter(adapterMap.get(1));
                break;
            case "排行版":
                LinearLayout llRank = binding.getRoot().findViewById(R.id.llRank);
                LinearLayout llRankOne = binding.getRoot().findViewById(R.id.llRankOne);
                LinearLayout llRankTwo = binding.getRoot().findViewById(R.id.llRankTwo);
                LinearLayout llRankThree = binding.getRoot().findViewById(R.id.llRankThree);
                NiceImageView ivRankOne = binding.getRoot().findViewById(R.id.ivRankOne);
                NiceImageView ivRankTwo = binding.getRoot().findViewById(R.id.ivRankTwo);
                NiceImageView ivRankThree = binding.getRoot().findViewById(R.id.ivRankThree);
                TextView tvRankOne = binding.getRoot().findViewById(R.id.tvRankOne);
                TextView tvRankTwo = binding.getRoot().findViewById(R.id.tvRankTwo);
                TextView tvRankThree = binding.getRoot().findViewById(R.id.tvRankThree);
                if(list.size() >= 3){
                    llRank.setVisibility(View.VISIBLE);
                    NetworkSongEntity networkSongOne = (NetworkSongEntity) list.get(0);
                    NetworkSongEntity networkSongTwo = (NetworkSongEntity) list.get(1);
                    NetworkSongEntity networkSongThree = (NetworkSongEntity) list.get(2);
                    Glide.with(binding.getRoot().getContext()).load(networkSongOne.getSongImage()).into(ivRankOne);
                    Glide.with(binding.getRoot().getContext()).load(networkSongTwo.getSongImage()).into(ivRankTwo);
                    Glide.with(binding.getRoot().getContext()).load(networkSongThree.getSongImage()).into(ivRankThree);
                    tvRankOne.setText(networkSongOne.getSongName());
                    tvRankTwo.setText(networkSongTwo.getSongName());
                    tvRankThree.setText(networkSongThree.getSongName());

                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView textView = new TextView(binding.getRoot().getContext());
                            textView.setText("+1");
                            textView.setTextColor(Color.RED);
                            ValueAnimator animator = ValueAnimator.ofInt(10,200);
                            animator.setDuration(3000);

                            new AnimationUtils(v, textView, new AnimationUtils.ViewLikeClickListener() {
                                @Override
                                public void onClick(View view, boolean toggle, AnimationUtils animationUtils) {
                                    animationUtils.startLikeAnim(animator);
                                }
                            });
                        }
                    };
                    llRankOne.setOnClickListener(onClickListener);
                    llRankTwo.setOnClickListener(onClickListener);
                    llRankThree.setOnClickListener(onClickListener);
                }
                break;
            case "新歌版":
                break;
        }
    }

}


