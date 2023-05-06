package com.hzc.coolcatmusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.FragmentHome2Binding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.entity.LookEntity;

import me.goldze.mvvmhabit.base.BaseFragment;

public class HomeFragment2 extends BaseFragment<FragmentHome2Binding,HomeFragment2ViewModel> {



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home2;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeFragment2ViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(HomeFragment2ViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();

        for (int i = 0; i < 19; i++) {
            LookEntity lookEntity = new LookEntity();
            lookEntity.setTitleImage("https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png");
            lookEntity.setTitleText("title");
            lookEntity.setAuthorImage("https://profile.csdnimg.cn/6/E/0/0_weixin_44360546");
            lookEntity.setAuthorName("authorName");
            lookEntity.setLookTimes("1");
            viewModel.lookEntities.add(lookEntity);
        }
    }

    private void setVideo(){

    }
}
