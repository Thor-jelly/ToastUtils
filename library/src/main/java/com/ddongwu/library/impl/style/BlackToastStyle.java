package com.ddongwu.library.impl.style;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddongwu.library.iinterface.IToastStyle;

/**
 * 类描述：黑色居中 Toast <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 16:22 <br/>
 */
public class BlackToastStyle implements IToastStyle {
    @Override
    public View createView(Context context) {
        TextView textView = new TextView(context);
        textView.setId(android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xEEFFFFFF);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics());

        textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        GradientDrawable backgroundDrawable = new GradientDrawable();
        // 设置颜色
        backgroundDrawable.setColor(0xD5000000);
        // 设置圆角
        backgroundDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));
        // 设置背景
        textView.setBackground(backgroundDrawable);
        // 设置 Z 轴阴影
        textView.setZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));
        return textView;
    }
}
