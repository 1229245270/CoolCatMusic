package com.hzc.coolCatMusic.ui.detail;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.ActivityDetailBinding;
import com.hzc.coolCatMusic.ui.main.HomeViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

public class DetailActivity extends BaseActivity<ActivityDetailBinding,DetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public DetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(DetailViewModel.class);
    }
}
