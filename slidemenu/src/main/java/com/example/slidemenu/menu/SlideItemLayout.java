package com.example.slidemenu.menu;

import android.app.PendingIntent;
import android.content.Context;
import android.util.AttributeSet;
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

    private View contentView;
    private View menuView;

    // 滚动者
    private Scroller mScroller;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下记录坐标
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
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
                break;
            case MotionEvent.ACTION_UP:
                int totalScrollX = getScrollX();// 偏移量
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
    }

    private void closeMenu() {
        // -> 关闭menu菜单向右滑动
        // getScrollX: 表示当前已经移动的偏移量，其实就是menu最开始在最右侧滑动此时的距离
        // distanceX: menu要关闭，需要滑动到最右侧，此时还需要在X轴上滑动的距离，向右为负，向左为正
        int distanceX = 0 - getScrollX();

        // getScrollX: startX表示当前的在X轴上的偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), distanceX, 0);

        invalidate();// 重新绘制
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }

    }
}
