package com.hzc.coolCatMusic.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.databinding.ObservableLong;

import com.google.gson.reflect.TypeToken;
import me.goldze.mvvmhabit.base.BaseBean;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.data.DemoRepository;
import com.hzc.coolCatMusic.entity.Font;
import com.hzc.coolCatMusic.entity.HomeFragment1ItemEntity;
import com.hzc.coolCatMusic.ui.adapter.SongAdapter;
import com.hzc.coolCatMusic.ui.homefragment1.LocalMusicFragment;
import com.hzc.coolCatMusic.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class NavigationThemeViewModel extends BaseViewModel<DemoRepository> {
    public NavigationThemeViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationThemeViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public SingleLiveEvent<Boolean> isCheck = new SingleLiveEvent<>();

    public ObservableList<Font> fontObservableList = new ObservableArrayList<>();

    public ObservableLong selectId = new ObservableLong(-1);

    public OnItemBind<Font> fontOnItemBind = new OnItemBind<Font>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Font item) {
            itemBinding.set(BR.item, R.layout.item_font)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.id,selectId.get())
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    public void settingFont(){
        List<Font> fonts = new ArrayList<>();
        fonts.add(new Font(0L,"字体1","",""));
        fonts.add(new Font(1L,"字体2","",""));
        fonts.add(new Font(2L,"字体3","",""));
        fonts.add(new Font(3L,"字体4","",""));
        fonts.add(new Font(4L,"字体5","",""));
        fonts.add(new Font(5L,"字体6","",""));
        fonts.add(new Font(6L,"字体7","",""));
        fontObservableList.addAll(fonts);
        model.requestApi(new Function<Integer, ObservableSource<BaseBean>>() {
            @Override
            public ObservableSource<BaseBean> apply(@NonNull Integer integer) throws Exception {
                return model.settingFont();
            }
        },new NetCallback<BaseBean>(){

            @Override
            public void onSuccess(BaseBean result) {
                if(result.getStatus()){
                    List<Font> fonts = result.getResultList(new TypeToken<List<Font>>(){});
                    KLog.d("返回：" + fonts.toString());
                    fontObservableList.addAll(fonts);
                }
            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }


    public BindingRecyclerViewAdapter<Font> fontAdapter = new BindingRecyclerViewAdapter<Font>() {};

    private int oldSelectPosition = -1;
    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            if(entity instanceof Font){
                Font font = (Font) entity;

                if(oldSelectPosition == -1){
                    for(int i = 0;i < fontObservableList.size();i++){
                        if(fontObservableList.get(i).getId() == selectId.get()){
                            oldSelectPosition = i;
                            break;
                        }
                    }
                }
                changeFont(font.getId(),position,oldSelectPosition);
                oldSelectPosition = position;
            }
        }
    };

    public void changeFont(Long id,int position,int oldPosition){
        selectId.set(id);
        fontAdapter.notifyItemChanged(position);
        fontAdapter.notifyItemChanged(oldPosition);
    }
}
