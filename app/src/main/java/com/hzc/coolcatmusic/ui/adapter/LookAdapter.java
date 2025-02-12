package com.hzc.coolcatmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.entity.LookEntity;
import com.hzc.coolcatmusic.ui.main.look.ItemLookUtil;
import com.hzc.coolcatmusic.ui.main.look.ItemLookVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

public class LookAdapter<T> extends BindingRecyclerViewAdapter<T> {

    public static final String TAG = "LookAdapter";

    @NonNull
    @Override
    public ViewDataBinding onCreateBinding(@NonNull LayoutInflater inflater, int layoutId, @NonNull ViewGroup viewGroup) {
        return super.onCreateBinding(inflater, layoutId, viewGroup);
    }

    @Override
    public void onBindBinding(@NonNull ViewDataBinding binding, int variableId, int layoutRes, int position, T item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        binding.getRoot().setTag(position);
        ItemLookVideo itemLookVideo = binding.getRoot().findViewById(R.id.item_look_video);
        itemLookVideo.setPlayTag(TAG);
        itemLookVideo.setPlayPosition(position);
        final String url = "http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4";
        ItemLookUtil.optionPlayer(itemLookVideo,url,true,"title");
        itemLookVideo.setUpLazy(url,true,null,null,"title");

        //增加封面
        /*ImageView imageView = new ImageView(binding.getRoot().getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);
        if(imageView.getParent() != null){
            ViewGroup viewGroup = (ViewGroup) imageView.getParent();
            viewGroup.removeView(imageView);
        }
        itemLookVideo.setThumbImageView(imageView);*/
        itemLookVideo.loadCoverImage(url,R.mipmap.ic_launcher);
        if(position == GSYVideoManager.instance().getPlayPosition()){
            itemLookVideo.getThumbImageViewLayout().setVisibility(View.GONE);
        }else{
            itemLookVideo.getThumbImageViewLayout().setVisibility(View.VISIBLE);
        }
    }
}

