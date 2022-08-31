package com.hzc.coolCatMusic.data.source;

import me.goldze.mvvmhabit.base.BaseBean;
import com.hzc.coolCatMusic.entity.DemoEntity;

import java.io.File;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * Created by goldze on 2019/3/26.
 */
public interface HttpDataSource {
    //模拟登录
    Observable<Object> login();

    //模拟上拉加载
    Observable<DemoEntity> loadMore();

    Observable<BaseResponse<DemoEntity>> demoGet();

    Observable<BaseResponse<DemoEntity>> demoPost(String catalog);

    Observable<BaseBean> settingFont();

    Observable<BaseBean> songUnlockWindow64(File file, String username);
}
