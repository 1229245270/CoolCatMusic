package me.goldze.mvvmhabit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import me.goldze.mvvmhabit.utils.KLog;

public class TouchLinearLayout extends LinearLayout{
    public TouchLinearLayout(Context context) {
        this(context,null);
    }

    public TouchLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TouchLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){

    }

    private float startX;
    private float startY;
    private int eventEat;

    //有组件onTouchEvent返回true后不会再执行
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                eventEat = 0;
                startX = ev.getRawX();
                startY = ev.getRawY();
            case MotionEvent.ACTION_MOVE:
                //来到新的坐标
                float endX = ev.getRawX();
                float endY = ev.getRawY();
                //计算与点击时坐标的偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                if(eventEat == 0){
                    if(Math.abs(distanceX) > 100){
                        eventEat = 1;
                    }else if(Math.abs(distanceY) > 100){
                        eventEat = 2;
                    }
                }

                //拦截水平方向的事件
                if(eventEat == 1){
                    return true;
                //不拦截，子组件继续事件分发
                }else{
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
