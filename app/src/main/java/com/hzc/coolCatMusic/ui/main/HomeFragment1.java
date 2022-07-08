package com.hzc.coolCatMusic.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentHome1Binding;
import com.hzc.coolCatMusic.entity.HomeFragment1DetailEntity;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.utils.DialogUtils;
import com.hzc.coolCatMusic.utils.LocalUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class HomeFragment1 extends BaseFragment<FragmentHome1Binding,HomeFragment1ViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home1;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeFragment1ViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(HomeFragment1ViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        initItem();
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SongItemTouchHelper(list, viewModel.localSongAdapter));
        //itemTouchHelper.attachToRecyclerView(binding.recycleView);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d("onHiddenChanged",hidden);
    }

    public HomeFragment1DetailEntity fragment1DetailEntity = new HomeFragment1DetailEntity();

    private void initItem(){
        List<HomeFragment1ItemEntity> itemEntityList = new ArrayList<>();
        HomeFragment1ItemEntity entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_localmusic),"本地音乐");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_recommend),"每日推荐");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_ranking),"排行榜");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_ranking),"排行榜");
        itemEntityList.add(entity);
        viewModel.itemEntityList.addAll(itemEntityList);
        initLocalSong();
    }

    @SuppressLint("CheckResult")
    private void initLocalSong(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        List<LocalSongEntity> localMusic = LocalUtils.getAllMediaList(getActivity(),60,0);
                        fragment1DetailEntity.setLocalSongEntityList(localMusic.subList(0, Math.min(localMusic.size(), 3)));
                    }
                });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isRequestRead.observe(this,aBoolean -> {
            initLocalSong();
        });
    }

    private Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(viewModel.getApplication(), id);
    }
}
