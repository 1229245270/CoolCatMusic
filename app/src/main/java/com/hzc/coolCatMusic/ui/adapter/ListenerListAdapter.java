package com.hzc.coolCatMusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.entity.ListenerEntity;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public abstract class ListenerListAdapter extends BindingRecyclerViewAdapter<Object> {
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
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, Object item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycleView);

    }

}


