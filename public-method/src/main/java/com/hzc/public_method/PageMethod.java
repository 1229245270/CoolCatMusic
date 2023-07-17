package com.hzc.public_method;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * @author 12292
 */
public class PageMethod {

    public static boolean isTouch = false;

    /**
     * 添加动画返回
     * @param showView 滑动的view
     * @param activity 上下文
     * @param backCallback 动画结束后回调
     */
    public static void onBackPressed(View showView, Activity activity,BackCallback backCallback) {
        if(showView != null && activity != null){
            TranslateAnimation translateAnimation = new TranslateAnimation (
                    0,showView.getMeasuredWidth() - showView.getTranslationX(),0,0);
            translateAnimation.setDuration(300);
            showView.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //禁止全局触摸
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //开启全局触摸
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    backCallback.onBack();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else {
            backCallback.onBack();
        }
    }

    public interface BackCallback{
        void onBack();
    }
}
