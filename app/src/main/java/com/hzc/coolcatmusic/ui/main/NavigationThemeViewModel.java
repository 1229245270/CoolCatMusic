package com.hzc.coolcatmusic.ui.main;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import com.google.gson.reflect.TypeToken;
import me.goldze.mvvmhabit.base.BaseBean;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.SPUtilsConfig;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.Font;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.DaoUtils.FontUtils;

import java.io.File;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;
import skin.support.SkinCompatManager;

public class NavigationThemeViewModel extends BaseViewModel<DemoRepository> {
    public NavigationThemeViewModel(@NonNull Application application) {
        super(application);
    }

    public NavigationThemeViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    public SingleLiveEvent<Font> fontSingleLiveEvent = new SingleLiveEvent<>();

    public ObservableList<Font> fontObservableList = new ObservableArrayList<>();
    public ObservableField<Font> selectFontObservableField = new ObservableField<>();
    public ObservableInt max = new ObservableInt(0);
    public ObservableInt progress = new ObservableInt(0);
    public ObservableInt seekbarVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableField<String> buttonText = new ObservableField<>("下载");
    public ObservableField<Drawable> buttonBackground = new ObservableField<Drawable>(ContextCompat.getDrawable(getApplication(), R.drawable.download_button));
    public void changeView(int maxValue,int progressValue,int visible,String text,Drawable drawable){
        if(max.get() != maxValue){
            max.set(maxValue);
        }
        if(progress.get() != progressValue){
            progress.set(progressValue);
        }
        if(seekbarVisibility.get() != visible){
            seekbarVisibility.set(visible);
        }
        String string = buttonText.get();
        if(string != null && !string.equals(text)){
            buttonText.set(text);
        }
        if(buttonBackground.get() != drawable){
            buttonBackground.set(drawable);
        }
    }

    public BindingCommand<Boolean> fontApplyCommand = new BindingCommand<>(new BindingAction() {
        @Override
        public void call() {
            Font font = selectFontObservableField.get();
            if(font != null && !isDownloading){
                fontSingleLiveEvent.setValue(font);
            }else{
                ToastUtils.showShort("请等待其他文件下载...");
            }
        }
    });

    public BindingCommand<Boolean> textClick = new BindingCommand<>(new BindingAction() {
        @Override
        public void call() {
            KLog.d("11111111111");
            SkinCompatManager.getInstance().loadSkin(SPUtilsConfig.THEME_MODE_NIGHT, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        }
    });

    public OnItemBind<Font> fontOnItemBind = new OnItemBind<Font>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Font item) {

            itemBinding.set(BR.item, R.layout.item_font)
                    .bindExtra(BR.position,position)
                    .bindExtra(BR.id,getSelectFontId())
                    .bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    public Long getSelectFontId(){
        Long id = -1L;
        Font font = selectFontObservableField.get();
        if(font != null){
            id = font.getId();
        }
        return id;
    }

    public void settingFont(){
        model.requestApi(new Function<Integer, ObservableSource<BaseBean>>() {
            @Override
            public ObservableSource<BaseBean> apply(@NonNull Integer integer) throws Exception {
                return model.settingFont();
            }
        },new NetCallback<BaseBean>(){

            @Override
            public void onSuccess(BaseBean result) {
                List<Font> fonts = result.getResultList(new TypeToken<List<Font>>(){});
                Font font = new Font(-2L,"默认","","","");
                fontObservableList.add(font);
                fontObservableList.addAll(fonts);
                long l = SPUtils.getInstance().getLong(SPUtilsConfig.THEME_TEXT_FONT_ID);
                if(l == -1L){
                    if(fonts.size() >= 1){
                        changeFont(fonts.get(0),0,oldSelectPosition);
                    }
                }else{
                    for(int i = 0;i < fonts.size();i++){
                        if(fonts.get(i).getId().equals(l)){
                            changeFont(fonts.get(i),i,oldSelectPosition);
                            break;
                        }
                    }
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
    public boolean isDownloading = false;

    private int oldSelectPosition = -1;
    public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position,Object entity) {
            if(entity instanceof Font){
                Font font = (Font) entity;
                if(oldSelectPosition == -1){
                    for(int i = 0;i < fontObservableList.size();i++){
                        if(fontObservableList.get(i).getId().equals(getSelectFontId())){
                            oldSelectPosition = i;
                            break;
                        }
                    }
                }
                changeFont(font,position,oldSelectPosition);
                oldSelectPosition = position;
            }
        }
    };

    public void changeFont(Font font,int position,int oldPosition){
        if(oldSelectPosition == position){
            return;
        }
        selectFontObservableField.set(font);
        if(isHaveLocalFile(font)){
            changeView(0,0,View.INVISIBLE,"应用",ContextCompat.getDrawable(getApplication(), R.drawable.download_button_apply));
        }else{
            changeView(0,0,View.INVISIBLE,"下载",ContextCompat.getDrawable(getApplication(), R.drawable.download_button));
        }
        fontAdapter.notifyItemChanged(position);
        fontAdapter.notifyItemChanged(oldPosition);
    }

    public boolean isHaveLocalFile(Font font){
        Font daoFont = FontUtils.getFontEntity(font.getId());
        String destFileDir = getApplication().getCacheDir().getPath();  //文件存放的路径
        File dir = new File(destFileDir);
        String destFileName = font.getName() + ".ttf";//文件存放的名称
        File file = new File(dir, destFileName);
        return (daoFont != null && !StringUtils.isEmpty(daoFont.getLocalFile()) && file.exists()) || font.getId().equals(-2L);
    }
}
