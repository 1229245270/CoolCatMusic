package com.hzc.coolcatmusic.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class AnimationUtils {
    public interface ViewLikeClickListener {
        /**
         * @param view          被点赞的按钮
         * @param toggle        开关
         * @param animationUtils 工具类本身
         */
        void onClick(View view, boolean toggle, AnimationUtils animationUtils);
    }

    // 被点击的按钮
    private View mClickView;
    private View mAnimView;
    private ViewLikeClickListener mListener;
    private boolean toggle = false; // 点击开关标识
    private int mX; // 距离屏幕左侧距离
    private int mY; // 距离屏幕顶端距离, 越往下数值越大

    /**
     * @param mClickView 被点击的View
     * @param mAnimView  点赞后, 向上浮动的View
     * @param mListener  被点击的View,点击后的回调事件.
     */
    public AnimationUtils(View mClickView, View mAnimView, @NonNull ViewLikeClickListener mListener) {
        this.mClickView = mClickView;
        this.mAnimView = mAnimView;
        this.mListener = mListener;
        initListener();
    }

    /**
     * 设置View的监听
     */
    private void initListener() {
        mClickView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    getLocation(); // 获取被点击View的坐标
                    toggle = !toggle;
                    if (mListener != null) {
                        mListener.onClick(mClickView, toggle, AnimationUtils.this);
                    }
                    // mView.performClick();
                }
                // 正常的OnClickListener将无法调用
                return true;
            }
        });
    }

    /**
     * 获取View在屏幕中的坐标
     */
    private void getLocation() {
        int[] mLocation = new int[2];
        mClickView.getLocationOnScreen(mLocation);
        mX = mLocation[0];
        mY = mLocation[1];
    }

    /**
     * 开始动画
     */
    private void startAnim(ValueAnimator valueAnimator) {
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimView.setAlpha(1 - (Float) valueAnimator.getAnimatedFraction());
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mAnimView.getLayoutParams();
                params.topMargin = (int) (mY - mAnimView.getMeasuredHeight() - 100 * valueAnimator.getAnimatedFraction());
                mAnimView.setLayoutParams(params);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                removeChildView(mAnimView);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 将上浮控件添加到屏幕中
     *
     * @param animview
     */
    private void addAnimView(View animview) {
        Activity activityFromView = getActivityFromView(mClickView);
        if (activityFromView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout mRootView = (FrameLayout) activityFromView.getWindow().getDecorView().getRootView();
            mRootView.addView(animview, params);
            // 测量浮动View的大小
            animview.measure(0, 0);
            params.topMargin = (int) (mY - animview.getMeasuredHeight());
            params.leftMargin = mX + mClickView.getMeasuredWidth() / 2 - animview.getMeasuredHeight() / 2;
            animview.setLayoutParams(params);
        }
    }

    /**
     * 开始动画
     */
    public void startLikeAnim(ValueAnimator valueAnimator) {
        removeChildView(mAnimView);
        addAnimView(mAnimView);
        startAnim(valueAnimator);
    }

    /**
     * 获取Activity
     *
     * @param view
     * @return
     */
    public Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    /**
     * 将子View从父容器中去除
     */
    private void removeChildView(View mChildView) {
        ViewGroup parentViewGroup = (ViewGroup) mChildView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(mChildView);
        }
    }
}
