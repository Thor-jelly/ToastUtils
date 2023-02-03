package com.ddongwu.library.showhandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.ddongwu.library.ToastParams;
import com.ddongwu.library.iinterface.IToast;
import com.ddongwu.library.iinterface.IToastStyle;
import com.ddongwu.library.impl.toast.AbsToast;
import com.ddongwu.library.impl.toast.ActivityToast;
import com.ddongwu.library.impl.toast.ApplicationToast;
import com.ddongwu.library.impl.toast.NotificationToast;
import com.ddongwu.library.impl.toast.SystemToast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 类描述：Toast 展示 隐藏 处理 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 17:39 <br/>
 */
public class ToastHandler {
    /**
     * Handler 对象
     */
    private final static Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 默认延迟时间
     * <p>
     * 延迟一段时间之后再执行，因为在没有通知栏权限的情况下，Toast 只能显示在当前 Activity 上面
     * 如果当前 Activity 在 showToast 之后立马进行 finish 了，那么这个时候 Toast 可能会显示不出来
     * 因为 Toast 会显示在销毁 Activity 界面上，而不会显示在新跳转的 Activity 上面
     */
    private static final int DEFAULT_DELAY_TIMEOUT = 200;

    private Application mApplication;

    /**
     * Toast 对象
     */
    private WeakReference<IToast> mToastReference;

    /**
     * 显示消息 Token
     */
    private final Object mShowMessageToken = new Object();

    /**
     * 取消消息 Token
     */
    private final Object mCancelMessageToken = new Object();

    public ToastHandler() {
    }

    public void init(Application application) {
        mApplication = application;
        ToastActivityManager.getInstance().register(application);
    }

    public void showToast(ToastParams params) {
        if (mApplication == null) {
            Log.e("ToastUtils", "ToastHandler showToast 未获取到Application，请先初始化ToastUtils");
            return;
        }
        // 移除之前未显示的 Toast 消息
        HANDLER.removeCallbacksAndMessages(mShowMessageToken);
        long uptimeMillis = SystemClock.uptimeMillis() + DEFAULT_DELAY_TIMEOUT;
        HANDLER.postAtTime(new ShowToastRunnable(params), mShowMessageToken, uptimeMillis);
    }

    public void cancelToast() {
        if (mApplication == null) {
            Log.e("ToastUtils", "ToastHandler showToast 未获取到Application，请先初始化ToastUtils");
            return;
        }
        HANDLER.removeCallbacksAndMessages(mCancelMessageToken);
        long uptimeMillis = SystemClock.uptimeMillis();
        HANDLER.postAtTime(new CancelToastRunnable(), mCancelMessageToken, uptimeMillis);
    }

    /**
     * 显示任务
     */
    private class ShowToastRunnable implements Runnable {
        private final ToastParams mToastParams;

        private ShowToastRunnable(ToastParams params) {
            mToastParams = params;
        }

        @Override
        public void run() {
            IToast toast = null;
            if (mToastReference != null) {
                toast = mToastReference.get();
            }

            if (toast != null) {
                // 取消上一个 Toast 的显示，避免出现重叠的效果
                toast.cancel();
            }
            toast = createToast(mToastParams.style);
            mToastReference = new WeakReference<>(toast);
            toast.setDuration(mToastParams.duration);
            toast.setText(mToastParams.text);
            toast.show();
        }
    }

    private IToast createToast(IToastStyle style) {
        Activity foregroundActivity = ToastActivityManager.getInstance().getForegroundActivity();
        IToast toast;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                Settings.canDrawOverlays(mApplication)) {
            // 如果有悬浮窗权限，就开启全局的 Toast
            toast = new ApplicationToast(mApplication);
        } else if (foregroundActivity != null) {
            // 如果没有悬浮窗权限，就开启一个依附于 Activity 的 Toast
            toast = new ActivityToast(foregroundActivity);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !isNotificationsEnabled(mApplication)) {
            // 处理 Toast 关闭通知栏权限之后无法弹出的问题
            // 通过查看和对比 NotificationManagerService 的源码
            // 发现这个问题已经在 Android 10 版本上面修复了
            // 但是 Toast 只能在前台显示，没有通知栏权限后台 Toast 仍然无法显示
            // 并且 Android 10 刚好禁止了 Hook 通知服务
            // 已经有通知栏权限，不需要 Hook 系统通知服务也能正常显示系统 Toast
            toast = new NotificationToast(mApplication);
        } else {
            toast = new SystemToast(mApplication);
        }

        if (isSupportToastStyle(toast)) {
            customToastStyle(toast, style);
        }
        return toast;
    }

    @SuppressLint("DiscouragedPrivateApi")
    private boolean isNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        }

        // 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled()
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = appOps.getClass().getMethod("checkOpNoThrow",
                    Integer.TYPE, Integer.TYPE, String.class);
            Field field = appOps.getClass().getDeclaredField("OP_POST_NOTIFICATION");
            int value = (int) field.get(Integer.class);
            return ((int) method.invoke(appOps, value, context.getApplicationInfo().uid,
                    context.getPackageName())) == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 是否支持设置自定义 Toast 样式
     */
    protected boolean isSupportToastStyle(IToast toast) {
        // targetSdkVersion >= 30 的情况下在后台显示自定义样式的 Toast 会被系统屏蔽，并且日志会输出以下警告：
        // Blocking custom toast from package com.xxx.xxx due to package not in the foreground
        // targetSdkVersion < 30 的情况下 new Toast，并且不设置视图显示，系统会抛出以下异常：
        // java.lang.RuntimeException: This Toast was not created with Toast.makeText()
        return toast instanceof AbsToast || Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                mApplication.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.R;
    }

    /**
     * 自定义 Toast 的样式
     */
    protected void customToastStyle(IToast toast, IToastStyle style) {
        toast.setView(style.createView(mApplication));
        toast.setGravity(style.getGravity(), style.getXOffset(), style.getYOffset());
        toast.setMargin(style.getHorizontalMargin(), style.getVerticalMargin());
    }

    /**
     * 取消任务
     */
    private class CancelToastRunnable implements Runnable {
        @Override
        public void run() {
            IToast toast = null;
            if (mToastReference != null) {
                toast = mToastReference.get();
            }

            if (toast == null) {
                return;
            }
            toast.cancel();
        }
    }
}
