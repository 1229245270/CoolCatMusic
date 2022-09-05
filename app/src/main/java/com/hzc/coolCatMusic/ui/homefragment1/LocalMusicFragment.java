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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.ui.main.HomeActivity;
import com.hzc.coolCatMusic.ui.main.HomeFragment;
import com.hzc.coolCatMusic.ui.main.HomeFragment1ViewModel;
import com.hzc.coolCatMusic.ui.main.HomeViewModel;
import com.hzc.coolCatMusic.utils.DialogUtils;
import com.hzc.coolCatMusic.utils.LocalUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.Messenger;
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

    float startX = 0;
    float startY = 0;
    private int eventEat;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initData() {
        super.initData();
        initLocalSong(false);
        viewModel.setRightIconVisible(View.VISIBLE);
        viewModel.setRightIcon(R.mipmap.ic_launcher);

        /*binding.recycleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = ev.getX();
                        startY = ev.getY();
                        eventEat = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //来到新的坐标
                        float endX = ev.getX();
                        float endY = ev.getY();
                        //计算偏移量
                        float distanceX = endX - startX;
                        float distanceY = endY - startY;
                        //主页
                        if(Math.abs(distanceX) > 100){
                            eventEat = 1;
                        }else if(Math.abs(distanceY) > 100){
                            eventEat = 2;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(eventEat == 1){
                        }
                        break;
                    default:
                        break;
                }
                return false;
                return binding.recycleView.onTouchEvent(ev);
            }
        });*/
    }


    @SuppressLint("CheckResult")
    public void initLocalSong(boolean isRestart){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    List<Object> list = new ArrayList<>();
                    if (aBoolean) {
                        list.addAll(LocalUtils.getAllMediaList(getActivity(),60,0));
                        list.add("共" + list.size() + "首");

                        for(int i = 0;i < 20;i++){
                            LocalSongEntity localSongEntity = new LocalSongEntity();
                            localSongEntity.setPath("" + i);
                            localSongEntity.setArtist("artist");
                            localSongEntity.setAlbums("albums");
                            list.add(localSongEntity);
                        }

                        //还原当前正在播放的歌曲
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isRequestRead.observe(this,aBoolean -> {
            KLog.d("aBoolean",aBoolean);
            initLocalSong(aBoolean);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
