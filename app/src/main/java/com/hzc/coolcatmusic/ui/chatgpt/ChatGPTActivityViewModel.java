package com.hzc.coolcatmusic.ui.chatgpt;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.base.viewmodel.ToolbarViewModel;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.ChatGPTFormEntity;
import com.hzc.coolcatmusic.entity.ChatGPTRequest;
import com.hzc.coolcatmusic.entity.ChatGPTResponse;
import com.hzc.coolcatmusic.entity.LocalSongEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.ui.adapter.ChatGPTAdapter;
import com.hzc.coolcatmusic.ui.listener.OnItemClickListener;
import com.hzc.coolcatmusic.utils.DaoUtils.ChatGPTUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.KeyboardUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class ChatGPTActivityViewModel extends ToolbarViewModel<DemoRepository> {


    public ChatGPTActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public ChatGPTActivityViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    private Disposable chatGPTEntitySubscription;

    public long id;

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        chatGPTEntitySubscription = RxBus.getDefault().toObservable(ChatGPTEntity.class)
                .subscribe(new Consumer<ChatGPTEntity>() {
                    @Override
                    public void accept(ChatGPTEntity chatGPTEntity) {
                        //新窗口
                        if(id == -1 && "user".contains(chatGPTEntity.getRole())){
                            id = chatGPTEntity.getChatForm();
                        }
                        if(id == chatGPTEntity.getChatForm()){
                            chatGPTEntitiesList.add(chatGPTEntity);
                            scrollToPositionEvent.setValue(chatGPTEntitiesList.size());
                            if(!"user".contains(chatGPTEntity.getRole())){
                                editTextEvent.setValue(true);
                            }
                        }
                    }
                });
        RxSubscriptions.add(chatGPTEntitySubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(chatGPTEntitySubscription);
    }

    public ObservableList<ChatGPTEntity> chatGPTEntitiesList = new ObservableArrayList<ChatGPTEntity>();

    public ChatGPTAdapter adapter = new ChatGPTAdapter() {
        @Override
        public void resend(ChatGPTEntity item) {
            send(item.getContent(),false);
        }

        @Override
        public void hideKeyBoard(View view) {
            KeyboardUtils.hideSoftKeyBoard(view, AppApplication.getInstance());
        }
    };

    public ObservableField<String> content = new ObservableField<>("");

    public OnItemBind<Object> onItemBind = new OnItemBind<Object>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Object item) {
            itemBinding.set(BR.item,R.layout.item_chat_gpt)
                    .bindExtra(BR.position,position);
                    //.bindExtra(BR.onItemClickListener,onItemClickListener);
        }
    };

    /*public OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position, Object entity) {
        }
    };*/

    SingleLiveEvent<Integer> scrollToPositionEvent = new SingleLiveEvent<>();

    SingleLiveEvent<Boolean> editTextEvent = new SingleLiveEvent<>();

    public void send(){
        send(content.get(),true);
    }

    public void send(String content,boolean clean){
        if(editTextEvent.getValue() != null && !editTextEvent.getValue()){
            ToastUtils.showShort("请等待chatGPT回复...");
            return;
        }
        if(!"".equals(content)){
            editTextEvent.setValue(false);
            if(clean){
                this.content.set("");
            }
            ChatGPTEntity entity = new ChatGPTEntity();
            entity.setContent(content);
            entity.setRole("user");
            entity.setChatForm(id);
            MusicConnection.musicInterface.sendChatGPTEntity(entity);
        }
    }
}
