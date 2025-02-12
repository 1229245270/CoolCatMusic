package com.hzc.coolcatmusic.data.source;

import me.goldze.mvvmhabit.base.BaseBean;

import com.hzc.coolcatmusic.entity.ChatGPTRequest;
import com.hzc.coolcatmusic.entity.DemoEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    Observable<BaseBean> songUnlockWindow64Multiple(List<File> file, String username);

    Observable<BaseBean> chatGPTV1ChatCompletions(ChatGPTRequest request);
}
