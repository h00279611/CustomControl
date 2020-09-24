package com.example.slidemenu.menu;

/**
 * 此接口主要是解决 用户从右往左滑动多个Item的menu菜单从而打开多个menu菜单
 *
 * 正确的操作是：当用户点击B Item的菜单时，应该关闭A item打开的菜单
 *
 */
public interface SlideListener {


    /**
     * 当鼠标按下时
     * @param itemLayout
     */
    void onDown(SlideItemLayout itemLayout);

    /**
     * 当打开Menu时
     * @param itemLayout
     */
    void onOpen(SlideItemLayout itemLayout);


    void onClose(SlideItemLayout itemLayout);

}
