package com.hzc.coolcatmusic.ui.homefragment1;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.utils.DialogUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class LocalMusicFragment extends BaseFragment<FragmentLocalmusicBinding,LocalMusicViewModel> {

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
    public LocalMusicViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(LocalMusicViewModel.class);
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
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    List<Object> list = new ArrayList<>();
                    if (aBoolean) {
                        viewModel.loadLocalSong(getActivity());
                    } else if(!isRestart){
                        list.add(false);
                        viewModel.localSongList.clear();
                        viewModel.localSongList.addAll(list);
                        boolean isShow = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(!isShow){
                            DialogUtils.openAppSettingDialog();
                        }
                    }
                });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isRequestRead.observe(this, this::initLocalSong);
    }

}
