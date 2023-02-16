package com.hzc.coolcatmusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.LookEntity;
import com.hzc.coolcatmusic.ui.adapter.LookAdapter;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;

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

    public LookAdapter<LookEntity> lookAdapter = new LookAdapter<>();

    public ObservableList<LookEntity> lookEntities = new ObservableArrayList<LookEntity>();

    public OnItemBind<LookEntity> lookBind = new OnItemBind<LookEntity>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, LookEntity item) {
            itemBinding.set(BR.item, R.layout.item_look);

        }
    };


}
