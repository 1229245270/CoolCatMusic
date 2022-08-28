package com.hzc.coolCatMusic.ui.main;

import static com.hzc.coolCatMusic.app.SPUtilsConfig.Theme_TEXT_FONT_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.reflect.TypeToken;
import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentHome1Binding;
import com.hzc.coolCatMusic.entity.Font;
import com.hzc.coolCatMusic.entity.HomeFragment1DetailEntity;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.entity.ListenerEntity;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.utils.DialogUtils;
import com.hzc.coolCatMusic.utils.LocalUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;

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
