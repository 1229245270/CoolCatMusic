package com.hzc.coolCatMusic.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

public class BaseBean implements Serializable {

    private boolean status;
    private String msg = "";
    private Object data;

    public <E> E getResultBean(Class<E> eClass){
        if (data == null){
            return null;
        }
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(gson.toJson(data),eClass);
    }

    public <T> T getResultList(TypeToken<T> token){
        if (data == null || TextUtils.isEmpty(data.toString())){
            return null;
        }
        Type type = token.getType();
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(gson.toJson(data),type);
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
