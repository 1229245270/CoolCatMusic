package me.goldze.mvvmhabit.http;


import android.app.Application;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;
import retrofit2.HttpException;

public abstract class NetCallback<M> implements Observer<M> {

    public abstract void onSuccess(M model);

    public abstract void onFailure(String msg);

    public abstract void onFinish();

    @Override
    public void onNext(M m) {
        try{
            if (((BaseBean)m).getStatus()){
                onSuccess(m);
            }else {
                onFailureBefore(((BaseBean)m).getMsg());
            }
        }catch (Exception e){
            KLog.e(e.toString());
            onFailureBefore("数据异常");
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            String msg = httpException.getMessage();

            if (code == 502 || code == 500 || code == 404) {
                msg = "服务器异常，请稍后再试";
            } else {
                msg = "网络连接失败，请稍后再试";
            }
            onFailureBefore(msg);
        } /*else if (e instanceof UnknownHostException){
            onFailure("网络连接失败，请稍后再试");
        } else if (e instanceof ConnectException){
            onFailure("网络状态异常，请稍后再试");
        } else if (e instanceof JsonSyntaxException){
            onFailure("未知错误，请稍后再试");
        } else if (e instanceof NullPointerException){
            onFailure("未知错误，请稍后再试");
            //throw new NullPointerException("程序异常，稍后自动重启");
        } */else {
            onFailureBefore("酷喵发呆了，请稍后再试");
        }
        onFinish();
    }

    private void onFailureBefore(String msg){
        ToastUtils.showShort(msg);
        onFailure(msg);
    }

    @Override
    public void onComplete() {
        onFinish();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }
}
