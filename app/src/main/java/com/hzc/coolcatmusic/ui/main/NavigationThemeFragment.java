package com.hzc.coolcatmusic.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.FragmentNavigationThemeBinding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.entity.Font;
import com.hzc.coolcatmusic.utils.DaoUtils.FontUtils;
import com.hzc.coolcatmusic.app.SPUtilsConfig;

import java.io.File;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.ResponseBody;

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
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.fontSingleLiveEvent.observe(this,font -> {

            Font daoFont = FontUtils.getFontEntity(font.getId());
            if(viewModel.isHaveLocalFile(font)){
                ToastUtils.showShort("应用");

                MaterialDialogUtils.showBasicDialog(getContext(),"应用","").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(daoFont != null){
                            SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT_PATH,daoFont.getLocalFile());
                            SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT_ID,daoFont.getId());
                        }else if(font.getId().equals(-2L)){
                            SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT_PATH,"");
                            SPUtils.getInstance().put(SPUtilsConfig.Theme_TEXT_FONT_ID,-2L);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = AppApplication.getInstance().getPackageManager().getLaunchIntentForPackage(AppApplication.getInstance().getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                AppApplication.getInstance().startActivity(intent);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        },1000);
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        KLog.d("onNegative");
                    }
                }).show();
            }else{
                downFile(font);
            }
        });
    }


    private long oldTime = System.currentTimeMillis();
    @SuppressLint("CheckResult")
    private void downFile(Font font){
        if(getContext() == null){
            return;
        }
        String destFileDir = getContext().getCacheDir().getPath();  //文件存放的路径
        String destFileName = font.getName() + ".ttf";//文件存放的名称
        DownLoadManager.getInstance().load(font.getPath(), new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            @Override
            public void onStart() {
                if(font.getId().equals(viewModel.getSelectFontId()) && getContext() != null){
                    viewModel.changeView(100,0,View.VISIBLE,"0%",ContextCompat.getDrawable(getContext(), R.drawable.download_button));
                }
                viewModel.isDownloading = true;
            }

            @Override
            public void onCompleted() {
                if(getContext() == null){
                    return;
                }
                if(viewModel.isHaveLocalFile(font)){
                    if(font.getId().equals(viewModel.getSelectFontId())){
                        viewModel.changeView(100,100,View.INVISIBLE,"应用",ContextCompat.getDrawable(getContext(), R.drawable.download_button_apply));
                    }
                }
                viewModel.isDownloading = false;
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
                if(getContext() == null){
                    return;
                }
                String destFileDir = getContext().getCacheDir().getPath();  //文件存放的路径
                File dir = new File(destFileDir);
                String destFileName = font.getName() + ".ttf";//文件存放的名称
                File file = new File(dir, destFileName);
                font.setLocalFile(file.getPath());
                FontUtils.updateFontEntity(font);
            }

            @Override
            public void progress(final long progress, final long total) {
                long nowTime = System.currentTimeMillis();
                if(font.getId().equals(viewModel.getSelectFontId()) && getContext() != null && nowTime >= oldTime + 100){
                    viewModel.changeView((int) total,(int) progress,View.VISIBLE,(int)((double)progress / (double)total * 100) + "%",ContextCompat.getDrawable(getContext(), R.drawable.download_button));
                    oldTime = nowTime;
                }
            }

            @Override
            public void onError(Throwable e) {
                KLog.d("onError " + e.toString());
                //下载错误回调
            }
        });
    }

}
