package com.hzc.coolcatmusic.ui.costom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.coolcatmusic.app.AppApplication;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.goldze.mvvmhabit.utils.KeyboardUtils;

public class ReboundRecycleView extends RecyclerView {
    public ReboundRecycleView(@NonNull Context context) {
        this(context, null);
    }

    public ReboundRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ReboundRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        OverScrollDecoratorHelper.setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            KeyboardUtils.hideSoftKeyBoard(this,getContext());
        }
        return super.onTouchEvent(e);
    }

}
