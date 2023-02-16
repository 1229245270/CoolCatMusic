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
import com.hzc.coolcatmusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolcatmusic.entity.ListenerEntity;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.utils.LocalUtils;
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

        ListenerEntity<Object> listenerEntity = new ListenerEntity<>();
        listenerEntity.setTitle("本地歌曲");
        listenerEntity.setTip("本地歌曲");
        viewModel.listenerEntityList.add(listenerEntity);
        listenerEntity = new ListenerEntity<>();
        listenerEntity.setTitle("每日推荐");
        listenerEntity.setTip("每日推荐");
        viewModel.listenerEntityList.add(listenerEntity);
        listenerEntity = new ListenerEntity<>();
        listenerEntity.setTitle("排行版");
        listenerEntity.setTip("排行版");
        viewModel.listenerEntityList.add(listenerEntity);
        listenerEntity = new ListenerEntity<>();
        listenerEntity.setTitle("新歌版");
        listenerEntity.setTip("新歌版");
        viewModel.listenerEntityList.add(listenerEntity);
        initLocalSong();
        viewModel.loadSong();
    }

    @SuppressLint("CheckResult")
    private void initLocalSong(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        List<LocalSongEntity> localSongEntities = LocalUtils.getAllMediaList(getActivity(), 60, 0, 3);
                        List<Object> objectList = new ArrayList<>(localSongEntities);
                        viewModel.listenerEntityList.get(0).setList(objectList);
                        viewModel.listenerAdapter.notifyItemChanged(0);
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
