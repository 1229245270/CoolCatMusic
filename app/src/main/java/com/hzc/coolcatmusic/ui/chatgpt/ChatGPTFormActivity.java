package com.hzc.coolcatmusic.ui.chatgpt;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.databinding.ActivityChatgptFormBinding;
import com.hzc.coolcatmusic.utils.ChatFloatingUtils;
import com.hzc.coolcatmusic.utils.DaoUtils.ChatGPTUtils;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class ChatGPTFormActivity extends BaseActivity<ActivityChatgptFormBinding, ChatGPTFormActivityViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_chatgpt_form;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ChatGPTFormActivityViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(ChatGPTFormActivityViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.setTitleText("ChatGPT会话");

        viewModel.chatGPTFormEntities.clear();
        viewModel.chatGPTFormEntities.addAll(ChatGPTUtils.getAllChatGPTForm());


    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

    }

}
