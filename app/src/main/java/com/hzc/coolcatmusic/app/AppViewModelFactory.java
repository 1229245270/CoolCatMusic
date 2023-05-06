package com.hzc.coolcatmusic.app;

import android.annotation.SuppressLint;
import android.app.Application;

import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.ui.chatgpt.ChatFloatingViewModel;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTFormActivityViewModel;
import com.hzc.coolcatmusic.ui.detail.DetailViewModel;
import com.hzc.coolcatmusic.ui.generallibrary.PhotoViewModel;
import com.hzc.coolcatmusic.ui.homefragment1.LocalMusicViewModel;
import com.hzc.coolcatmusic.ui.homefragment1.ScanningMusicViewModel;
import com.hzc.coolcatmusic.ui.login.LoginViewModel;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTActivityViewModel;
import com.hzc.coolcatmusic.ui.main.HomeFragment1ViewModel;
import com.hzc.coolcatmusic.ui.main.HomeFragment2ViewModel;
import com.hzc.coolcatmusic.ui.main.HomeFragment3ViewModel;
import com.hzc.coolcatmusic.ui.main.HomeFragmentViewModel;
import com.hzc.coolcatmusic.ui.main.HomeViewModel;
import com.hzc.coolcatmusic.ui.main.NavigationThemeViewModel;
import com.hzc.coolcatmusic.ui.main.NavigationSensorViewModel;
import com.hzc.coolcatmusic.ui.main.NavigationSleepViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by goldze on 2019/3/26.
 */
public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final DemoRepository mRepository;

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideDemoRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, DemoRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mApplication, mRepository);
        }else if(modelClass.isAssignableFrom(HomeViewModel.class)){
            return (T) new HomeViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(HomeFragmentViewModel.class)){
            return (T) new HomeFragmentViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(HomeFragment1ViewModel.class)){
            return (T) new HomeFragment1ViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(HomeFragment2ViewModel.class)){
            return (T) new HomeFragment2ViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(HomeFragment3ViewModel.class)){
            return (T) new HomeFragment3ViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(LocalMusicViewModel.class)){
            return (T) new LocalMusicViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(NavigationSleepViewModel.class)){
            return (T) new NavigationSleepViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(NavigationSensorViewModel.class)){
            return (T) new NavigationSensorViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(NavigationThemeViewModel.class)){
            return (T) new NavigationThemeViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(DetailViewModel.class)){
            return (T) new DetailViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(ScanningMusicViewModel.class)){
            return (T) new ScanningMusicViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(ChatGPTActivityViewModel.class)){
            return (T) new ChatGPTActivityViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(ChatGPTFormActivityViewModel.class)){
            return (T) new ChatGPTFormActivityViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(ChatFloatingViewModel.class)){
            return (T) new ChatFloatingViewModel(mApplication,mRepository);
        }else if(modelClass.isAssignableFrom(PhotoViewModel.class)){
            return (T) new PhotoViewModel(mApplication,mRepository);
        }


        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
