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
import com.hzc.coolCatMusic.entity.ListenerEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.utils.DaoUtils.MusicUtils;

import java.util.List;

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

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, ListenerEntity<Object> item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycleView);
        switch (item.getTip()){
            case "本地歌曲":

                break;
            case "每日推荐":
                break;
            case "排行版":
                break;
            case "新歌版":
                break;
        }
    }

}


