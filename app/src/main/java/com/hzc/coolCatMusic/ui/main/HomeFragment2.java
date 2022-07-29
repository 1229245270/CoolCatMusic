package com.hzc.coolCatMusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentHome2Binding;
import com.hzc.coolCatMusic.ui.costom.ScrollCalculatorHelper;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

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
            Object videoModel = new Object();
            viewModel.mvList.add(videoModel);
        }
        //限定范围为屏幕一半的上下偏移180
        int playTop = CommonUtil.getScreenHeight(requireContext()) / 2 - CommonUtil.dip2px(requireContext(), 180);
        int playBottom = CommonUtil.getScreenHeight(requireContext()) / 2 + CommonUtil.dip2px(requireContext(), 180);
        ScrollCalculatorHelper helper = new ScrollCalculatorHelper(R.id.video, playTop, playBottom);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.recycleView.setLayoutManager(linearLayoutManager);
        binding.recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                helper.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                //这是滑动自动播放的代码
                if (true) {
                    helper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, lastVisibleItem - firstVisibleItem);
                }
            }
        });
    }
}
