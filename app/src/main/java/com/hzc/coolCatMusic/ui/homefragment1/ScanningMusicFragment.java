package com.hzc.coolCatMusic.ui.homefragment1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentLocalmusicBinding;
import com.hzc.coolCatMusic.databinding.FragmentScanningmusicBinding;

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
}
