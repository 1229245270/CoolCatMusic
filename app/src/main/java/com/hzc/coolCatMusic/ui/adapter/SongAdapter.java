package com.hzc.coolCatMusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.utils.DaoUtils.MusicUtils;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public abstract class SongAdapter<T> extends BindingRecyclerViewAdapter<T> {
    @NonNull
    @Override
    public ViewDataBinding onCreateBinding(@NonNull LayoutInflater inflater, int layoutId, @NonNull ViewGroup viewGroup) {
        return super.onCreateBinding(inflater, layoutId, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, T item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        ImageView imageView = binding.getRoot().findViewById(R.id.songImage);
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
            TextView songName = binding.getRoot().findViewById(R.id.songName);
            TextView singer = binding.getRoot().findViewById(R.id.singer);
            LinearLayout songItem = binding.getRoot().findViewById(R.id.songItem);

            PlayingMusicEntity playingMusicEntity = MusicUtils.getPlayingMusicEntity();
            if(playingMusicEntity != null && playingMusicEntity.getSrc().equals(((LocalSongEntity) item).getPath())){
                setOldPosition(position);
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

    public abstract void setOldPosition(int position);

}


