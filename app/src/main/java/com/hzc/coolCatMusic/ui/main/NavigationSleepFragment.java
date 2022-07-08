package com.hzc.coolCatMusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentHome3Binding;
import com.hzc.coolCatMusic.databinding.FragmentNavigationSleepBinding;

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
}
