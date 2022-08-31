package com.hzc.coolCatMusic.data.source.http;

import me.goldze.mvvmhabit.base.BaseBean;
import com.hzc.coolCatMusic.data.source.HttpDataSource;
import com.hzc.coolCatMusic.data.source.http.service.DemoApiService;
import com.hzc.coolCatMusic.entity.DemoEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.KLog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by goldze on 2019/3/26.
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private DemoApiService apiService;
    private volatile static HttpDataSourceImpl INSTANCE = null;

    public static HttpDataSourceImpl getInstance(DemoApiService apiService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl(DemoApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observable<Object> login() {
        return Observable.just(new Object()).delay(3, TimeUnit.SECONDS); //延迟3秒
    }

    @Override
    public Observable<DemoEntity> loadMore() {
        return Observable.create(new ObservableOnSubscribe<DemoEntity>() {
            @Override
            public void subscribe(ObservableEmitter<DemoEntity> observableEmitter) throws Exception {
                DemoEntity entity = new DemoEntity();
                List<DemoEntity.ItemsEntity> itemsEntities = new ArrayList<>();
                //模拟一部分假数据
                for (int i = 0; i < 10; i++) {
                    DemoEntity.ItemsEntity item = new DemoEntity.ItemsEntity();
                    item.setId(-1);
                    item.setName("模拟条目");
                    itemsEntities.add(item);
                }
                entity.setItems(itemsEntities);
                observableEmitter.onNext(entity);
            }
        }).delay(3, TimeUnit.SECONDS); //延迟3秒
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoGet() {
        return apiService.demoGet();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoPost(String catalog) {
        return apiService.demoPost(catalog);
    }

    @Override
    public Observable<BaseBean> settingFont() {
        return apiService.settingFont();
    }

    @Override
    public Observable<BaseBean> songUnlockWindow64(File file, String username) {
        RequestBody fileRQ = RequestBody.create(null, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("path", file.getName(), fileRQ);
        RequestBody user = RequestBody.create(null,username);
        return apiService.songUnlockWindow64(part,user);
    }


}
