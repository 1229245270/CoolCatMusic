package com.hzc.coolCatMusic.ui.homefragment1;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.ui.main.HomeFragment1ViewModel;
import com.hzc.coolCatMusic.utils.DialogUtils;
import com.hzc.coolCatMusic.utils.LocalUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class LocalMusicFragment extends BaseFragment<FragmentLocalmusicBinding,LocalMusicViewModel> {

    public static LocalMusicFragment instance;

    public static LocalMusicFragment getInstance(){
        if(instance == null){
            instance = new LocalMusicFragment();
        }
        return instance;
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
    }

    @SuppressLint("CheckResult")
    private void initLocalSong(boolean isRestart){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    List<Object> list = new ArrayList<>();
                    if (aBoolean) {
                        list.addAll(LocalUtils.getAllMediaList(getActivity(),60,0));
                        list.add("共" + list.size() + "首");
                        LocalSongEntity localSongEntity = null;
                        for(int i = 0;i < viewModel.localSongList.size();i++){
                            if(viewModel.localSongList.get(i) instanceof LocalSongEntity && ((LocalSongEntity) viewModel.localSongList.get(i)).isCheck()){
                                localSongEntity = (LocalSongEntity) viewModel.localSongList.get(i);
                                break;
                            }
                        }
                        if(localSongEntity != null){
                            for(int i = 0;i < list.size();i++){
                                if(list.get(i) instanceof LocalSongEntity && ((LocalSongEntity) list.get(i)).getPath().equals(localSongEntity.getPath())){
                                    list.set(i,localSongEntity);
                                    break;
                                }
                            }
                        }
                        viewModel.localSongList.clear();
                        viewModel.localSongList.addAll(list);
                    } else if(!isRestart){
                        list.add(false);
                        viewModel.localSongList.clear();
                        viewModel.localSongList.addAll(list);
                        boolean isShow = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(!isShow){
                            DialogUtils.openAppSettingDialog(getContext());
                        }
                    }
                });
    }

    private PlayingMusicEntity nowPlay;
    private LocalSongEntity oldLocalSong;
    private int oldPosition = -1;
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isRequestRead.observe(this,aBoolean -> {
            KLog.d("aBoolean",aBoolean);
            initLocalSong(aBoolean);
        });

        viewModel.changePlaying.observe(this,playingMusicEntity -> {
            if(nowPlay != null && nowPlay == playingMusicEntity){
                return;
            }
            nowPlay = playingMusicEntity;
            if(oldPosition != -1 && oldLocalSong != null){
                oldLocalSong.setCheck(false);
                viewModel.localSongList.set(oldPosition,oldLocalSong);
            }
            for(int i = 0;i < viewModel.localSongList.size();i++){
                Object entity = viewModel.localSongList.get(i);
                if(entity instanceof LocalSongEntity){
                    LocalSongEntity localSongEntity = (LocalSongEntity) entity;
                    if(playingMusicEntity.getSrc().equals(localSongEntity.getPath())){
                        localSongEntity.setCheck(true);
                        viewModel.localSongList.set(i,localSongEntity);
                        oldLocalSong = localSongEntity;
                        oldPosition = i;
                        return;
                    }
                }
            }
        });
    }
}
