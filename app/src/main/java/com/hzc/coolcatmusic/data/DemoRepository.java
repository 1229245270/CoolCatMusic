package com.hzc.coolcatmusic.data;

import com.hzc.coolcatmusic.data.source.HttpDataSource;
import com.hzc.coolcatmusic.data.source.LocalDataSource;
import com.hzc.coolcatmusic.entity.ChatGPTRequest;
import com.hzc.coolcatmusic.entity.DemoEntity;
import com.hzc.public_method.RequestMethod;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * MVVM的Model层，统一模块的数据仓库，包含网络数据和本地数据（一个应用可以有多个Repositor）
 * Created by goldze on 2019/3/26.
 */
public class DemoRepository extends BaseModel implements HttpDataSource, LocalDataSource {
    private volatile static DemoRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;
    private final LocalDataSource mLocalDataSource;

    private DemoRepository(@NonNull HttpDataSource httpDataSource,
                           @NonNull LocalDataSource localDataSource) {
        this.mHttpDataSource = httpDataSource;
        this.mLocalDataSource = localDataSource;
    }

    public static DemoRepository getInstance(HttpDataSource httpDataSource,
                                             LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (DemoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DemoRepository(httpDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    public <T> void requestApi(Function<Integer, ObservableSource<T>> function, Observer<T> observer){
        RequestMethod.requestApi(function,observer);
    }


    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Observable<Object> login() {
        return mHttpDataSource.login();
    }

    @Override
    public Observable<DemoEntity> loadMore() {
        return mHttpDataSource.loadMore();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoGet() {
        return mHttpDataSource.demoGet();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoPost(String catalog) {
        return mHttpDataSource.demoPost(catalog);
    }

    @Override
    public Observable<BaseBean> settingFont() {
        return mHttpDataSource.settingFont();
    }

    @Override
    public Observable<BaseBean> songUnlockWindow64(File file, String username) {
        return mHttpDataSource.songUnlockWindow64(file, username);
    }

    @Override
    public Observable<BaseBean> songUnlockWindow64Multiple(List<File> file, String username) {
        return mHttpDataSource.songUnlockWindow64Multiple(file, username);
    }

    @Override
    public Observable<BaseBean> chatGPTV1ChatCompletions(ChatGPTRequest request) {
        return mHttpDataSource.chatGPTV1ChatCompletions(request);
    }

    @Override
    public void saveUserName(String userName) {
        mLocalDataSource.saveUserName(userName);
    }

    @Override
    public void savePassword(String password) {
        mLocalDataSource.savePassword(password);
    }

    @Override
    public String getUserName() {
        return mLocalDataSource.getUserName();
    }

    @Override
    public String getPassword() {
        return mLocalDataSource.getPassword();
    }
}
