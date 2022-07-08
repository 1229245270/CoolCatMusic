package com.hzc.coolCatMusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentNavigationSensorBinding;
import com.hzc.coolCatMusic.databinding.FragmentNavigationSleepBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

public class NavigationSensorFragment extends BaseFragment<FragmentNavigationSensorBinding,NavigationSensorViewModel> {

    public static NavigationSensorFragment instance;

    public static NavigationSensorFragment getInstance(){
        if(instance == null){
            instance = new NavigationSensorFragment();
        }
        return instance;
    }

    @Override
    public NavigationSensorViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(NavigationSensorViewModel.class);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_navigation_sensor;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
