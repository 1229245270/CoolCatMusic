package com.hzc.coolcatmusic.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTPopupWindow;
import com.hzc.coolcatmusic.utils.DaoUtils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public abstract class ChatGPTAdapter extends BindingRecyclerViewAdapter<ChatGPTEntity> {

    public ChatGPTAdapter(){

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
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, ChatGPTEntity item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);

        ImageView ivImage = binding.getRoot().findViewById(R.id.iv_image);
        TextView tvChat = binding.getRoot().findViewById(R.id.tv_chat);
        LinearLayout llItem = binding.getRoot().findViewById(R.id.ll_item);
        View viewTop = binding.getRoot().findViewById(R.id.view_top);
        View viewBottom = binding.getRoot().findViewById(R.id.view_bottom);
        tvChat.setText(item.getContent());
        tvChat.setTextColor(Color.BLACK);
        List<String> strings = new ArrayList<>();
        strings.add("复制");
        if(item.getRole() != null && ("user".equals(item.getRole()) || "user_error".equals(item.getRole()))){
            strings.add("重新发送");
            ivImage.setImageResource(R.mipmap.ic_launcher);
            llItem.setBackgroundColor(Color.WHITE);
            viewTop.setVisibility(View.GONE);
            viewBottom.setVisibility(View.GONE);
        }else{
            if("error".equals(item.getRole())){
                tvChat.setTextColor(Color.RED);
            }
            ivImage.setImageResource(R.drawable.ic_chatgpt_assistant);
            llItem.setBackgroundColor(Color.parseColor("#CCD9D9E3"));
            viewTop.setVisibility(View.VISIBLE);
            viewBottom.setVisibility(View.VISIBLE);
        }
        if(!"error".equals(item.getRole())){
            llItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ChatGPTPopupWindow popupWindow = new ChatGPTPopupWindow(binding.getRoot().getContext(),strings) {
                        @Override
                        public void onItemClick(PopupWindow window,String string) {
                            switch (string){
                                case "复制":
                                    ClipboardManager clipboardManager = (ClipboardManager) AppApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText(null,item.getContent());
                                    clipboardManager.setPrimaryClip(clipData);
                                    ToastUtils.showShort("复制成功");
                                    break;
                                case "重新发送":
                                    resend(item);
                                    break;
                                default:
                            }
                            window.dismiss();
                        }
                    };
                    popupWindow.showAsCenter(llItem);
                    return false;
                }
            });
        }

        llItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KLog.d(event);
                if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    hideKeyBoard(v);
                }
                return false;
            }
        });
    }

    public abstract void resend(ChatGPTEntity item);
    public abstract void hideKeyBoard(View view);
}


