package com.hzc.public_method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 12292
 */
public class RequestMethod {
    public static  <T> void requestApi(Function<Integer, ObservableSource<T>> function, Observer<T> observer){
        Observable<T> observable = Observable.just(1)
                .subscribeOn(Schedulers.io())
                .flatMap(function)
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(observer);
    }
}
