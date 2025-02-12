package com.hzc.coolcatmusic.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.FragmentHome3Binding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTActivity;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTFormActivity;
import com.hzc.coolcatmusic.ui.login.LoginActivity;

import me.goldze.mvvmhabit.base.BaseFragment;

public class HomeFragment3 extends BaseFragment<FragmentHome3Binding,HomeFragment3ViewModel> {

    ViewPager2 mainViewPager;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home3;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeFragment3ViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(HomeFragment3ViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        binding.button.setOnClickListener(v -> {
            startActivity(ChatGPTFormActivity.class);
        });
    }
}
