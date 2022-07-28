package com.hzc.coolCatMusic.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.FragmentNavigationThemeBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

public class NavigationThemeFragment extends BaseFragment<FragmentNavigationThemeBinding, NavigationThemeViewModel> {

    public static NavigationThemeFragment instance;

    public static NavigationThemeFragment getInstance(){
        if(instance == null){
            instance = new NavigationThemeFragment();
        }
        return instance;
    }

    @Override
    public NavigationThemeViewModel initViewModel() {
        if(getActivity() == null){
            return null;
        }
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this,factory).get(NavigationThemeViewModel.class);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_navigation_theme;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.settingFont();


        /*switchTheme.setChecked(SPUtils.getInstance().getString(SPUtilsConfig.Theme_TEXT_FONT).equals(SPUtilsConfig.THEME_TEXT_FONT_MI_SANS_NORMAL));
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                KLog.d("isChecked:" + isChecked);
                if(isChecked){
                    SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT,SPUtilsConfig.THEME_TEXT_FONT_MI_SANS_NORMAL);
                }else{
                    SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT,SPUtilsConfig.THEME_TEXT_FONT_SYSTEM);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = AppApplication.getInstance().getPackageManager().getLaunchIntentForPackage(AppApplication.getInstance().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        AppApplication.getInstance().startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                },1000);

            }
        });*/

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
    }
}
