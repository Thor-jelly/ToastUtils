package com.ddongwu.library.iinterface;

import android.view.View;
import android.widget.TextView;

/**
 * 类描述：Toast interface <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 15:00 <br/>
 */
public interface IToast {
    /**
     * 显示
     */
    void show();

    /**
     * 取消
     */
    void cancel();

    /**
     * 设置文本
     */
    void setText(CharSequence text);

    /**
     * 设置布局
     */
    void setView(View view);

    /**
     * 获取布局
     */
    View getView();

    /**
     * 设置显示时长
     */
    void setDuration(int duration);

    /**
     * 获取显示时长
     */
    int getDuration();

    /**
     * 设置重心偏移
     */
    void setGravity(int gravity, int xOffset, int yOffset);

    /**
     * 获取显示重心
     */
    int getGravity();

    /**
     * 获取水平偏移
     */
    int getXOffset();

    /**
     * 获取垂直偏移
     */
    int getYOffset();

    /**
     * 设置屏幕间距
     */
    void setMargin(float horizontalMargin, float verticalMargin);

    /**
     * 设置水平间距
     */
    float getHorizontalMargin();

    /**
     * 设置垂直间距
     */
    float getVerticalMargin();

    /**
     * 智能获取用于显示消息的 TextView
     */
    default TextView findMessageView(View view) {
        if (view instanceof TextView) {
            if (view.getId() == View.NO_ID) {
                view.setId(android.R.id.message);
            } else if (view.getId() != android.R.id.message) {
                // 必须将 TextView 的 id 值设置成 android.R.id.message
                throw new IllegalArgumentException("必须将 TextView 的 id 值设置成 android.R.id.message");
            }
            return (TextView) view;
        }

        View messageView = view.findViewById(android.R.id.message);
        if (messageView instanceof TextView) {
            return ((TextView) messageView);
        }

        // 如果设置的布局没有包含一个 TextView 则抛出异常，必须要包含一个 id 值成 android.R.id.message 的 TextView
        throw new IllegalArgumentException("必须将 TextView 的 id 值设置成 android.R.id.message");
    }
}
