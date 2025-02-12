package com.hzc.coolcatmusic.ui.chatgpt;

import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_END_SIDE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_START_SIDE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.databinding.ActivityChatgptBinding;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.ui.detail.DetailViewModel;
import com.hzc.coolcatmusic.utils.DaoUtils.ChatGPTUtils;

import java.util.List;

import me.everything.android.ui.overscroll.HorizontalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.KeyboardUtils;

public class ChatGPTActivity extends BaseActivity<ActivityChatgptBinding,ChatGPTActivityViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_chatgpt;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ChatGPTActivityViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(ChatGPTActivityViewModel.class);
    }

    public long id;
    @Override
    public void initData() {
        super.initData();

        Intent intent = getIntent();
        id = intent.getLongExtra("id",-1);

        binding.rvChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && viewModel.chatGPTEntitiesList.size() > 0) {
                binding.rvChat.smoothScrollToPosition(viewModel.chatGPTEntitiesList.size() - 1);
            }
        });
        List<ChatGPTEntity> list = ChatGPTUtils.getChatGPTEntityForChatForm(id);
        viewModel.chatGPTEntitiesList.addAll(list);
        binding.rvChat.smoothScrollToPosition(list.size());
        viewModel.id = id;
        if(AppApplication.chatGPTRead.get(id) == null || Boolean.TRUE.equals(AppApplication.chatGPTRead.get(id))){
            viewModel.editTextEvent.setValue(true);
        }else{
            viewModel.editTextEvent.setValue(false);
        }

        viewModel.setTitleText("ChatGPT聊天");
        binding.tvChat.setOnClickListener(v -> {
            viewModel.send();
        });

        binding.getRoot().setOnClickListener(v -> {
            KeyboardUtils.hideSoftKeyBoard(v,this);
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.scrollToPositionEvent.observe(this,position -> {
            binding.rvChat.smoothScrollToPosition(position);
        });
        viewModel.editTextEvent.observe(this,aBoolean -> {
            if(aBoolean){
                binding.tvChat.setBackgroundResource(R.drawable.chatgpt_send_button);
                binding.tvChat.setText("发送");
            }else{
                binding.tvChat.setBackgroundResource(R.drawable.chatgpt_unsend_button);
                binding.tvChat.setText("发送中");
            }
            binding.tvChat.setEnabled(aBoolean);
        });

    }

}
