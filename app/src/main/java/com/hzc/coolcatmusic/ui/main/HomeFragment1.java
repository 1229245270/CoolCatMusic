package com.hzc.coolcatmusic.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.databinding.FragmentHome1Binding;
import com.hzc.coolcatmusic.entity.ExpandedTabEntity;
import com.hzc.coolcatmusic.entity.HomeFragment1ItemEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

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

    private void initItem(){
        List<HomeFragment1ItemEntity> itemEntityList = new ArrayList<>();
        HomeFragment1ItemEntity entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_localmusic),"本地音乐");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_recommend),"每日推荐");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_ranking),"排行榜");
        itemEntityList.add(entity);
        entity = new HomeFragment1ItemEntity(getDrawable(R.drawable.home_ranking),"新歌版");
        itemEntityList.add(entity);
        viewModel.itemEntityList.addAll(itemEntityList);

        ExpandedTabEntity<Object> expandedTabEntity = new ExpandedTabEntity<>();
        expandedTabEntity.setTitle("本地歌曲");
        expandedTabEntity.setTip("本地歌曲");
        viewModel.expandedTabEntityList.add(expandedTabEntity);
        expandedTabEntity = new ExpandedTabEntity<>();
        expandedTabEntity.setTitle("每日推荐");
        expandedTabEntity.setTip("每日推荐");
        viewModel.expandedTabEntityList.add(expandedTabEntity);
        expandedTabEntity = new ExpandedTabEntity<>();
        expandedTabEntity.setTitle("排行版");
        expandedTabEntity.setTip("排行版");
        viewModel.expandedTabEntityList.add(expandedTabEntity);
        expandedTabEntity = new ExpandedTabEntity<>();
        expandedTabEntity.setTitle("新歌版");
        expandedTabEntity.setTip("新歌版");
        viewModel.expandedTabEntityList.add(expandedTabEntity);
        initLocalSong();
        viewModel.loadSong();
    }

    @SuppressLint("CheckResult")
    private void initLocalSong(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        viewModel.loadLocalSongTop3();
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
