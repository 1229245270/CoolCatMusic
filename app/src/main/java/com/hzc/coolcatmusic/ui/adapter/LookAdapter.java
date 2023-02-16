package com.hzc.coolcatmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.entity.LookEntity;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public class LookAdapter<T> extends BindingRecyclerViewAdapter<T> {
    @NonNull
    @Override
    public ViewDataBinding onCreateBinding(@NonNull LayoutInflater inflater, int layoutId, @NonNull ViewGroup viewGroup) {
        return super.onCreateBinding(inflater, layoutId, viewGroup);
    }

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, T item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        LinearLayout songItem = binding.getRoot().findViewById(R.id.songItem);
        LookEntity lookEntity = (LookEntity) item;
        if(lookEntity.getRecommendSong() != null){
            songItem.setVisibility(View.VISIBLE);
        }else{
            songItem.setVisibility(View.GONE);
        }

    }
}

