package com.example.slidemenu.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 侧滑菜单Item
 */
public class SlideItemLayout extends FrameLayout {

    private static final String TAG = SlideItemLayout.class.getSimpleName();
    private View contentView;
    private View menuView;

    // 滚动者
    private Scroller mScroller;

    // 菜单打开关闭回调
    private SlideListener mSlideListener;

    private int contentWidth;
    private int menuWidth;
    private int viewHeight;// 整个Item, content, menu高度都相同

    public SlideItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }


    /**
     * 布局加载完成后，被回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }


    /**
     * 在测量方法里，拿到各个控件的高和宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        contentWidth = contentView.getMeasuredWidth();
        menuWidth = menuView.getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 将Menu布局到content的后面紧挨着，因为整个宽度跟content的宽度相同，相当于menu被隐藏了，不显示
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, viewHeight);
    }


    private float startX;
    private float startY;

    // 当手指被按下的坐标，只记录一次
    private float downX;
    private float downY;


    // 当对contentView设置了点击事件后，左右滑动时也会触发contentView的点击事件，所以需要在其父控件slideItemLayout中重写onInterceptTouchEvent方法
    // onInterceptTouchEvent方法在左右滑动的时候返回true,这样就会调用其onTouchEvent，在其它情况下返回false,这样点击的时候也会执行子view的click方法了
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean isIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "SlideItemLayout-onTouchEvent-ACTION_DOWN");
                // 按下记录坐标
                downX = startX = event.getX();
                downY = startY = event.getY();
                if (mSlideListener != null) {
                    mSlideListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "SlideItemLayout-onTouchEvent-ACTION_MOVE");
                // 记录结束值
                float endX = event.getX();

                // 只要判断在水平方向上有滑动就需要拦截
                float DX = Math.abs(endX - downX);
                if (DX > 8) {
                    // 水平方向滑动
                    // 响应侧滑
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return isIntercept;
    }

    // Activity -> RelativeLayout -> ListView -> SlideItemLayout ->(contentView + menuView)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "SlideItemLayout-onTouchEvent-ACTION_DOWN");
                // 按下记录坐标
                downX = startX = event.getX();
                downY = startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "SlideItemLayout-onTouchEvent-ACTION_MOVE");
                // 记录结束值
                float endX = event.getX();
                float endY = event.getY();

                // 计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY; // 实际上是0因为是在X轴上滑动，Y轴方向没有变化


                int toScrollX = (int) (getScrollX() - distanceX);
                int toScrollY = (int) (getScrollY() - 0);

                if (toScrollX < 0) {
                    toScrollX = 0;
                } else if (toScrollX > menuWidth) {
                    toScrollX = menuWidth;
                }

                scrollTo(toScrollX, toScrollY);

                startX = event.getX();
                startY = event.getY();

                // 可能存在同时上下滑动ListView上滑动和左右滑动SlideItem滑动的情况
                // 在X轴和Y轴滑动的距离
                float DX = Math.abs(endX - downX);
                float DY = Math.abs(endY - downY);
                if (DX > DY && DX > 8) {
                    // 水平方向滑动
                    // 响应侧滑
                    // 反拦截-事件不让父类ListView处理,交给SlideItemLayout处理
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "SlideItemLayout-onTouchEvent-ACTION_UP");
                int totalScrollX = getScrollX();// 偏移量
                // 当手指松开时，如果Menu向左滑动距离小于一半，则回弹回去不显示，否则显示此Menu
                if (totalScrollX < menuWidth / 2) {
                    // 关闭右侧Menu
                    closeMenu();
                } else {
                    // 打开Menu
                    openMenu();
                }

                break;

        }

        return true;
    }

    private void openMenu() {
        //  打开menu菜单向左滑动
        // getScrollX: 表示当前已经移动的偏移量，其实就是menu最开始在最右侧滑动此时的距离
        // distanceX: menu要打开，需要滑动到最左侧，此时还需要在X轴上滑动的距离，向右为负，向左为正
        int distanceX = menuWidth - getScrollX();

        // getScrollX: startX表示当前的在X轴上的偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), distanceX, 0);

        invalidate();// 重新绘制

        if (mSlideListener != null) {
            mSlideListener.onOpen(this);
        }

    }

    public void closeMenu() {
        // -> 关闭menu菜单向右滑动
        // getScrollX: 表示当前已经移动的偏移量，其实就是menu最开始在最右侧滑动此时的距离
        // distanceX: menu要关闭，需要滑动到最右侧，此时还需要在X轴上滑动的距离，向右为负，向左为正
        int distanceX = 0 - getScrollX();

        // getScrollX: startX表示当前的在X轴上的偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), distanceX, 0);

        invalidate();// 重新绘制

        if (mSlideListener != null) {
            mSlideListener.onClose(this);
        }

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

    }


    public void setSlideListener(SlideListener slideListener) {
        mSlideListener = slideListener;
    }
}
