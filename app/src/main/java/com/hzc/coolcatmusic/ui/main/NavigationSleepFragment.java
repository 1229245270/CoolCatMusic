package com.hzc.coolcatmusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.slider.Slider;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.databinding.FragmentNavigationSleepBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

public class NavigationSleepFragment extends BaseFragment<FragmentNavigationSleepBinding,NavigationSleepViewModel> {

    public static NavigationSleepFragment instance;

    public static NavigationSleepFragment getInstance(){
        if(instance == null){
            instance = new NavigationSleepFragment();
        }
        return instance;
    }

    @Override
    public NavigationSleepViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(NavigationSleepViewModel.class);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_navigation_sleep;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();

    }

    @Override
    public void initData() {
        super.initData();
        binding.slScreenOff.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {

            }
        });
    }
}
