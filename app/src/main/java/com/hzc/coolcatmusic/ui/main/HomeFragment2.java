package com.hzc.coolcatmusic.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.FragmentHome2Binding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.entity.LookEntity;
import com.hzc.coolcatmusic.ui.main.look.LookVideoActivity;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import me.goldze.mvvmhabit.base.BaseFragment;

public class HomeFragment2 extends BaseFragment<FragmentHome2Binding,HomeFragment2ViewModel> {



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home2;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeFragment2ViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(HomeFragment2ViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();

        for (int i = 0; i < 19; i++) {
            LookEntity lookEntity = new LookEntity();
            lookEntity.setTitleImage("");
            lookEntity.setTitleText("title");
            lookEntity.setAuthorImage("");
            lookEntity.setAuthorName("authorName");
            lookEntity.setLookTimes("1");
            viewModel.lookEntities.add(lookEntity);
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recycleView.getLayoutManager();

        binding.recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem,lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(layoutManager == null){
                    return;
                }
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if(GSYVideoManager.instance().getPlayPosition() >= 0){
                    //当前播放的位置
                    int position = GSYVideoManager.instance().getPlayPosition();
                    //对应的播放列表TAG
                    if(position < firstVisibleItem || position > lastVisibleItem){
                        /*if(!GSYVideoManager.isFullState(LookVideoActivity.this)){

                        }*/
                        GSYVideoManager.releaseAllVideos();
                        viewModel.lookAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setVideo(){

    }
}
