package me.goldze.mvvmhabit.base;

import android.graphics.PointF;
import android.view.MotionEvent;

public class BaseGestureHandler {
    //屏幕宽高
    int sWidth = 1280;
    int sHeight = 720;
    //按下的点
    PointF down;
    //Y轴滑动的区间
    float minY, maxY;
    //按下时的时间
    long downTime;
    //边缘判定距离，
    double margin = sWidth * 0.035;
    //Y轴最大区间范围，即Y轴滑动超出这个范围不触发事件
    double height = sHeight * 0.2;
    //X轴最短滑动距离 X轴滑动范围低于此值不触发事件
    double width = sWidth * 0.1;
    //是否处于此次滑动事件
    boolean work = false;

    public boolean point(PointF up) {
        long upTime = System.currentTimeMillis();
        float tWidth = Math.abs(down.x - up.x);
        if (maxY - minY < height && tWidth > width && (upTime - downTime) / tWidth < 2.5) {
            //起点在左边
            if (down.x < margin) {
                left();//左滑需要处理的逻辑的方法
                return true;
            }

        }
        return false;
    }

    public boolean doEventF(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //记录下按下的点
                downTime = System.currentTimeMillis();
                down = new PointF(event.getX(), event.getY());
                minY = maxY = down.y;
                //判定是否处于边缘侧滑
                if (down.x < margin || (sWidth - down.x) < margin) work = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //记录滑动Y轴区间
                if (work)
                    if (event.getY() > down.y) {
                        maxY = event.getY();
                    } else {
                        minY = event.getY();
                    }
                break;
            case MotionEvent.ACTION_UP:
                if (work) {
                    //handle(new PointF(event.getX(), event.getY()));
                    work = false;
                    return true;
                }
                work = false;
        }

        return work;
    }

    public void left() {

        //处理左边缘滑动事件，这里你可以自己写一个ActivityUtil，用来finish当前的activity（这个网上很多，随便搜一下就有了）

    }
}
