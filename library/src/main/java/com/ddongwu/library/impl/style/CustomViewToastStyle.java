package com.ddongwu.library.impl.style;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.ddongwu.library.iinterface.IToastStyle;

/**
 * 类描述：自定义 Toast 样式，默认居中<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 16:50 <br/>
 */
public class CustomViewToastStyle implements IToastStyle {
    private final int mLayoutResId;
    private final int mGravity;
    private final int mXOffset;
    private final int mYOffset;
    private final float mHorizontalMargin;
    private final float mVerticalMargin;

    public CustomViewToastStyle(int id) {
        this(id, Gravity.CENTER);
    }

    public CustomViewToastStyle(int id, int gravity) {
        this(id, gravity, 0, 0);
    }

    public CustomViewToastStyle(int id, int gravity, int xOffset, int yOffset) {
        this(id, gravity, xOffset, yOffset, 0F, 0F);
    }

    public CustomViewToastStyle(int id, int gravity, int xOffset, int yOffset, float horizontalMargin, float verticalMargin) {
        mLayoutResId = id;
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(mLayoutResId, null);
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
