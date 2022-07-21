package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.hzc.coolCatMusic.data.BaseBean;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.Font;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;

public class NavigationThemeViewModel extends BaseViewModel<DemoRepository> {
    public NavigationThemeViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationThemeViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public SingleLiveEvent<Boolean> isCheck = new SingleLiveEvent<>();

    public void settingFont(){
        model.requestApi(new Function<Integer, ObservableSource<BaseBean>>() {
            @Override
            public ObservableSource<BaseBean> apply(@NonNull Integer integer) throws Exception {
                return model.settingFont();
            }
        },new NetCallback<BaseBean>(){

            @Override
            public void onSuccess(BaseBean result) {
                if(result.getStatus()){
                    List<Font> fonts = result.getResultList(new TypeToken<List<Font>>(){});
                    KLog.d("返回：" + fonts.toString());
                }
            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
