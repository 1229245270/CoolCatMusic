package com.hzc.coolcatmusic.ui.chatgpt;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.hzc.coolcatmusic.event.ChatGPTEntityFormEvent;
import com.hzc.coolcatmusic.ui.adapter.ChatGPTAdapter;
import com.hzc.coolcatmusic.ui.adapter.ChatGPTFormAdapter;
import com.hzc.coolcatmusic.utils.ChatFloatingUtils;
import com.hzc.coolcatmusic.utils.DaoUtils.ChatGPTUtils;
import com.hzc.coolcatmusic.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KeyboardUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class ChatGPTFormActivityViewModel extends ToolbarViewModel<DemoRepository> {


    public ChatGPTFormActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public ChatGPTFormActivityViewModel(@NonNull Application application, DemoRepository model) {
        super(application, model);
    }

    private Disposable ChatGPTEntitySubscription;

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        ChatGPTEntitySubscription = RxBus.getDefault().toObservable(ChatGPTEntity.class)
                .subscribe(new Consumer<ChatGPTEntity>() {
                    @Override
                    public void accept(ChatGPTEntity chatGPTEntity) {
                        boolean isHave = false;
                        for(int i = 0;i < chatGPTFormEntities.size();i++){
                            if(chatGPTEntity.getChatForm() == chatGPTFormEntities.get(i).getId()){
                                ChatGPTFormEntity formEntity = ChatGPTUtils.getChatGPTFormEntityForChatForm(chatGPTEntity.getChatForm());
                                chatGPTFormEntities.set(i,formEntity);
                                isHave = true;
                                break;
                            }
                        }
                        if(!isHave){
                            chatGPTFormEntities.clear();
                            chatGPTFormEntities.addAll(ChatGPTUtils.getAllChatGPTForm());
                        }
                    }
                });
        RxSubscriptions.add(ChatGPTEntitySubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(ChatGPTEntitySubscription);
    }



    public ObservableList<ChatGPTFormEntity> chatGPTFormEntities = new ObservableArrayList<ChatGPTFormEntity>();

    public ChatGPTFormAdapter adapter = new ChatGPTFormAdapter() {
        @Override
        public void onItemClick(int position, ChatGPTFormEntity item) {
            Bundle bundle = new Bundle();
            bundle.putLong("id",item.getId());
            startActivity(ChatGPTActivity.class,bundle);
        }
    };

    public OnItemBind<Object> onItemBind = new OnItemBind<Object>() {
        @Override
        public void onItemBind(@NonNull ItemBinding itemBinding, int position, Object item) {
            itemBinding.set(BR.item,R.layout.item_chat_gpt_form)
                    .bindExtra(BR.position,position);
        }
    };

    public BindingCommand newChatClick = new BindingCommand(() -> startActivity(ChatGPTActivity.class));
    //public BindingCommand newChatClick = new BindingCommand(() -> startActivity(ChatFloating.class));
}
