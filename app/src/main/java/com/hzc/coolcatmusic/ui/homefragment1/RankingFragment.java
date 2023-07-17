package com.hzc.coolcatmusic.ui.homefragment1;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolcatmusic.databinding.FragmentRankingBinding;
import com.hzc.coolcatmusic.utils.DialogUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * @author 12292
 */
public class RankingFragment extends BaseFragment<FragmentRankingBinding,RankingViewModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_localmusic;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RankingViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(RankingViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();

        initLocalSong(false);
        viewModel.setRightText("扫描");
        viewModel.setRightTextVisible(View.VISIBLE);


    }

    @SuppressLint("CheckResult")
    public void initLocalSong(boolean isRestart){
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isRequestRead.observe(this, this::initLocalSong);
    }

}
