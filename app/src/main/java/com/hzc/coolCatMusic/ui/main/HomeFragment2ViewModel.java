package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.ui.adapter.MvAdapter;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class HomeFragment2ViewModel extends BaseViewModel<DemoRepository> {

    public HomeFragment2ViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            KLog.d("1111111","onItemClick");
        }
    };

    public MvAdapter<Object> mvAdapter = new MvAdapter<>();

    public ObservableList<Object> mvList = new ObservableArrayList<Object>();

    public OnItemBind<Object> mvBind = new OnItemBind<Object>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Object item) {
            itemBinding.set(BR.item,R.layout.item_mv);

        }
    };


}
