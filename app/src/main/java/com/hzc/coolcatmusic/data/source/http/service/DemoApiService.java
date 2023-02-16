package com.hzc.coolcatmusic.data.source.http.service;

import me.goldze.mvvmhabit.base.BaseBean;
import com.hzc.coolcatmusic.entity.DemoEntity;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by goldze on 2017/6/15.
 */

public interface DemoApiService {
    @GET("action/apiv2/banner?catalog=1")
    Observable<BaseResponse<DemoEntity>> demoGet();

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<BaseResponse<DemoEntity>> demoPost(@Field("catalog") String catalog);

    @GET("setting/font")
    Observable<BaseBean> settingFont();

    @POST("song/unlock/window64")
    @Multipart
    Observable<BaseBean> songUnlockWindow64(@Part MultipartBody.Part path, @Part("username") RequestBody username);
}
