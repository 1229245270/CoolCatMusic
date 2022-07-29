package com.hzc.coolCatMusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.ui.costom.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.HashMap;
import java.util.Map;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public class MvAdapter<T> extends BindingRecyclerViewAdapter<T> {
    @NonNull
    @Override
    public ViewDataBinding onCreateBinding(@NonNull LayoutInflater inflater, int layoutId, @NonNull ViewGroup viewGroup) {
        return super.onCreateBinding(inflater, layoutId, viewGroup);
    }

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, T item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        SampleCoverVideo video = binding.getRoot().findViewById(R.id.video);
        String url = "https://cos.icxl.xyz/c03328206d894477a3f8c9767a4de5649342908.mov";
        video.loadCoverImage(url,R.mipmap.ic_launcher);
        video.getBackButton().setVisibility(View.GONE);
        //全屏按钮
        video.getFullscreenButton().setOnClickListener(view -> {
            video.startWindowFullscreen(binding.getRoot().getContext(), false,true);
        });

        Map<String, String> header = new HashMap<>();
        header.put("ee", "33");

        GSYVideoOptionBuilder gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        gsyVideoOptionBuilder
                .setIsTouchWiget(false)
                //.setThumbImageView(imageView)
                .setUrl(url)
                .setVideoTitle("title")
                .setCacheWithPlay(false)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setPlayTag("TAG")
                .setMapHeadData(header)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setPlayPosition(position)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        if (!video.isIfCurrentIsFullscreen()) {
                            //静音
                            GSYVideoManager.instance().setNeedMute(true);
                        }

                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        //全屏不静音
                        GSYVideoManager.instance().setNeedMute(true);
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        GSYVideoManager.instance().setNeedMute(false);
                        video.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                    }
                }).build(video);
    }
}

