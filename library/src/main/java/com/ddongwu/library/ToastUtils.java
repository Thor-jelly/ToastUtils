package com.ddongwu.library;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ddongwu.library.iinterface.IToastStyle;
import com.ddongwu.library.impl.style.BlackToastStyle;
import com.ddongwu.library.impl.style.CustomViewToastStyle;
import com.ddongwu.library.impl.style.LocationToastStyle;
import com.ddongwu.library.showhandler.ToastHandler;


/**
 * 参考：[AndroidUtilCode-ToastUtils](https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/src/main/java/com/blankj/utilcode/util/ToastUtils.java)   <br/>
 * 参考：[getActivity-ToastUtils](https://github.com/getActivity/ToastUtils)   <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 14:26 <br/>
 */
public class ToastUtils {
    private ToastUtils() {
    }

    private static Application sApplication;

    /**
     * Toast 样式
     */
    private static IToastStyle sToastStyle;
    private static ToastHandler sToastHandler;

    public static void init(Application application) {
        sApplication = application;
        if (sToastStyle == null) {
            //默认黑色居中样式
            sToastStyle = new BlackToastStyle();
        }
        if (sToastHandler == null) {
            sToastHandler = new ToastHandler();
        }
        sToastHandler.init(sApplication);
    }

    /**
     * 短Toast
     */
    public static void showShort(CharSequence text) {
        ToastParams toastParams = new ToastParams();
        toastParams.text = text;
        toastParams.duration = Toast.LENGTH_SHORT;
        show(toastParams);
    }

    /**
     * 长Toast
     */
    public static void showLong(CharSequence text) {
        ToastParams toastParams = new ToastParams();
        toastParams.text = text;
        toastParams.duration = Toast.LENGTH_LONG;
        show(toastParams);
    }

    /**
     * 自动根据文字长度设置 显示时长
     */
    public static void show(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int duration = text.length() > 10 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        ToastParams toastParams = new ToastParams();
        toastParams.text = text;
        toastParams.duration = duration;
        show(toastParams);
    }

    /**
     * 可以局部设置 Toast 样式的 展示
     */
    public static void show(ToastParams params) {
        if (sApplication == null || sToastHandler == null) {
            Log.e("ToastUtils", "请先在 Application 中，初始化 ToastUtils.init");
            return;
        }
        if (params == null || TextUtils.isEmpty(params.text)) {
            return;
        }
        if (params.style == null) {
            //默认给 黑色 主题样式
            params.style = sToastStyle;
        }
        sToastHandler.showToast(params);
    }

    /**
     * 取消Toast
     */
    public static void cancel() {
        if (sApplication == null || sToastHandler == null) {
            Log.e("ToastUtils", "请先在 Application 中，初始化 ToastUtils.init");
            return;
        }
        sToastHandler.cancelToast();
    }

    /**
     * 全局生效-设置 Toast 位置
     */
    public static void setGravity(int gravity) {
        setGravity(gravity, 0, 0);
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        setGravity(gravity, xOffset, yOffset, 0, 0);
    }

    public static void setGravity(int gravity, int xOffset, int yOffset, float horizontalMargin, float verticalMargin) {
        sToastStyle = new LocationToastStyle(sToastStyle, gravity, xOffset, yOffset, horizontalMargin, verticalMargin);
    }

    /**
     * 全局生效-设置 Toast 新的布局
     */
    public static void setView(int id) {
        if (id <= 0) {
            return;
        }
        setStyle(new CustomViewToastStyle(
                id,
                sToastStyle.getGravity(),
                sToastStyle.getXOffset(),
                sToastStyle.getYOffset(),
                sToastStyle.getHorizontalMargin(),
                sToastStyle.getVerticalMargin()));
    }

    /**
     * 全局生效-设置 Toast 样式
     *
     * @param style 样式实现类写法参考 下面两种
     *              黑色居中样式：{@link BlackToastStyle},
     *              自定义样式：{@link CustomViewToastStyle}
     */
    public static void setStyle(IToastStyle style) {
        sToastStyle = style;
    }
}
