package com.ddongwu.library.impl.style;

import android.content.Context;
import android.view.View;

import com.ddongwu.library.iinterface.IToastStyle;

/**
 * 类描述：位置样式 Toast<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 16:42 <br/>
 */
public class LocationToastStyle implements IToastStyle {
    private final IToastStyle mStyle;
    private final int mGravity;
    private final int mXOffset;
    private final int mYOffset;
    private final float mHorizontalMargin;
    private final float mVerticalMargin;

    /**
     * 只改变原来位置信息，所以需要把原来 Toast 样式传进来
     */
    public LocationToastStyle(IToastStyle style, int gravity) {
        this(style, gravity, 0, 0, 0F, 0F);
    }

    public LocationToastStyle(IToastStyle style, int gravity, int xOffset, int yOffset, float horizontalMargin, float verticalMargin) {
        mStyle = style;
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    @Override
    public View createView(Context context) {
        return mStyle.createView(context);
    }

    @Override
    public int getGravity() {
        return mGravity;
    }

    @Override
    public int getXOffset() {
        return mXOffset;
    }

    @Override
    public int getYOffset() {
        return mYOffset;
    }

    @Override
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    @Override
    public float getVerticalMargin() {
        return mVerticalMargin;
    }
}
