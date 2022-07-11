package me.goldze.mvvmhabit.http;


import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class NetCallback<M> implements Observer<M> {

    public abstract void onSuccess(M model);

    public abstract void onFailure(String msg);

    public abstract void onFinish();

    @Override
    public void onNext(M m) {
        /*if (((BaseBean)m).getStatus().equals("error")){
            onFailure(((BaseBean)m).getMsg());
        }else {
            onSuccess(m);
        }*/
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //httpException.response().errorBody().string()
            int code = httpException.code();
            String msg = httpException.getMessage();

            /*LogUtil.e("code:"+"code:"+code);
            LogUtil.e("code:"+"msg:"+msg);
            LogUtil.e("code:"+"error:"+e.getMessage());*/

            if (code == 502 || code == 500 || code == 404) {
                msg = "服务器异常，请稍后再试";
            } else {
                msg = "网络连接失败，请稍后再试";
            }
            onFailure(msg);
        } else if (e instanceof UnknownHostException){
            onFailure("网络连接失败，请稍后再试");
        } else if (e instanceof ConnectException){
            onFailure("网络状态异常，请稍后再试");
        } else if (e instanceof JsonSyntaxException){
            onFailure("未知错误，请稍后再试");
        } else if (e instanceof NullPointerException){
            onFailure("程序异常，稍后自动重启");
            //throw new NullPointerException("程序异常，稍后自动重启");
        } else {
            //onFailure(EmptyUtil.checkString(e.getMessage(),"网络连接失败，请稍后再试"));
        }
        onFinish();
        //LogUtil.e("msg:"+e.getMessage());
    }

    @Override
    public void onComplete() {
        onFinish();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }
}
