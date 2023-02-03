package com.ddongwu.library;

import com.ddongwu.library.iinterface.IToastStyle;

/**
 * 类描述：Toast 展示参数 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 17:03 <br/>
 */
public class ToastParams {
    /**
     * 显示的文本
     */
    public CharSequence text;

    /**
     * Toast 显示时长，有两种值可选
     * <p>
     * 短Toast：{@link android.widget.Toast#LENGTH_SHORT}
     * 长Toast：{@link android.widget.Toast#LENGTH_LONG}
     */
    public int duration = -1;

    /**
     * Toast 样式
     */
    public IToastStyle style;
}
