package com.hzc.coolCatMusic.ui.homefragment1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.LottieAnimationView;
import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolCatMusic.databinding.FragmentScanningmusicBinding;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.utils.DialogUtils;
import com.hzc.coolCatMusic.utils.LocalUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import me.goldze.mvvmhabit.base.BaseFragment;

public class ScanningMusicFragment extends BaseFragment<FragmentScanningmusicBinding,ScanningMusicViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_scanningmusic;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ScanningMusicViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(ScanningMusicViewModel.class);
    }


    public LottieAnimationView lottie;
    @Override
    public void initData() {
        super.initData();
        lottie = binding.lottie;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.scanEvent.observe(this,(bool) -> {
            request();
        });
    }

    private final int PERMISSION_CODE = 1000;

    @SuppressLint("CheckResult")
    private void request(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// android 11  且 不是已经被拒绝
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, PERMISSION_CODE);
            }else{
                lottie.playAnimation();
                viewModel.startScan();
            }
        }else{
            lottie.playAnimation();
            viewModel.startScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PERMISSION_CODE){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                lottie.playAnimation();
                viewModel.startScan();
            }
        }
    }
}
