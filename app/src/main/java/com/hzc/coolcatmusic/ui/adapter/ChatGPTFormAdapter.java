package com.hzc.coolcatmusic.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.ChatGPTFormEntity;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTPopupWindow;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.TimeUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public abstract class ChatGPTFormAdapter extends BindingRecyclerViewAdapter<ChatGPTFormEntity> {

    public ChatGPTFormAdapter(){

    }

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
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, ChatGPTFormEntity item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        TextView tvTitle = binding.getRoot().findViewById(R.id.tv_title);
        TextView tvNumber = binding.getRoot().findViewById(R.id.tv_number);
        TextView tvTime = binding.getRoot().findViewById(R.id.tv_time);
        TextView tvText = binding.getRoot().findViewById(R.id.tv_text);
        ImageView ivImage = binding.getRoot().findViewById(R.id.iv_image);
        String time = TimeUtils.getNewChatTime(item.getCreateDate(),false);
        tvTime.setText(time);
        tvTitle.setText("机器人" + item.getId() + "号");
        List<ChatGPTEntity> list = item.getList();
        if(list != null && list.size() > 0){
            tvNumber.setText(String.valueOf(list.size()));
            tvText.setText("[" + list.size() + "条]" + list.get(list.size() - 1).getContent());
        }else{
            tvNumber.setText("0");
        }
        Glide.with(binding.getRoot().getContext()).load(item.getImage()).error(R.mipmap.ic_launcher).into(ivImage);

        binding.getRoot().setOnClickListener(v ->{
            onItemClick(position, item);
        });
    }

    public abstract void onItemClick(int position,ChatGPTFormEntity item);
}


