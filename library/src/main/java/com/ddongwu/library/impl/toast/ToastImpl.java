package com.ddongwu.library.impl.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.ddongwu.library.showhandler.WindowLifecycle;

/**
 * 类描述：Toast实现 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 15:28 <br/>
 */
public final class ToastImpl {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 当前的 Toast 对象
     */
    private final AbsToast mToast;

    /**
     * WindowManager 辅助类
     */
    private WindowLifecycle mWindowLifecycle;

    /**
     * 当前应用的包名
     */
    private final String mPackageName;

    /**
     * 当前是否已经显示
     */
    private boolean mShow;

    /**
     * 当前是否全局显示
     */
    private boolean mGlobalShow;

    ToastImpl(Activity activity, AbsToast toast) {
        this((Context) activity, toast);
        mGlobalShow = false;
        mWindowLifecycle = new WindowLifecycle(activity);
    }

    ToastImpl(Application application, AbsToast toast) {
        this((Context) application, toast);
        mGlobalShow = true;
        mWindowLifecycle = new WindowLifecycle(application);
    }

    private ToastImpl(Context context, AbsToast toast) {
        mToast = toast;
        mPackageName = context.getPackageName();
    }

    boolean isShow() {
        return mShow;
    }

    void setShow(boolean show) {
        mShow = show;
    }

    /***
     * 显示 Toast 弹窗
     */
    void show() {
        if (isShow()) {
            return;
        }
        if (isMainThread()) {
            mShowRunnable.run();
        } else {
            HANDLER.removeCallbacks(mShowRunnable);
            HANDLER.post(mShowRunnable);
        }
    }

    /**
     * 取消 Toast 弹窗
     */
    public void cancel() {
        if (!isShow()) {
            return;
        }
        HANDLER.removeCallbacks(mShowRunnable);
        if (isMainThread()) {
            mCancelRunnable.run();
        } else {
            HANDLER.removeCallbacks(mCancelRunnable);
            HANDLER.post(mCancelRunnable);
        }
    }

    /**
     * 判断当前是否在主线程
     */
    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 发送无障碍事件
     */
    private void trySendAccessibilityEvent(View view) {
        final Context context = view.getContext();
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (!accessibilityManager.isEnabled()) {
            return;
        }
        // 将 Toast 视为通知，因为它们用于向用户宣布短暂的信息
        AccessibilityEvent event = AccessibilityEvent.obtain(
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
        event.setClassName(Toast.class.getName());
        event.setPackageName(context.getPackageName());
        view.dispatchPopulateAccessibilityEvent(event);
        accessibilityManager.sendAccessibilityEvent(event);
    }

    private final Runnable mShowRunnable = new Runnable() {
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            WindowManager windowManager = mWindowLifecycle.getWindowManager();
            if (windowManager == null) {
                return;
            }

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.packageName = mPackageName;
            params.gravity = mToast.getGravity();
            params.x = mToast.getXOffset();
            params.y = mToast.getYOffset();
            params.verticalMargin = mToast.getVerticalMargin();
            params.horizontalMargin = mToast.getHorizontalMargin();
            params.windowAnimations = mToast.getAnimationsId();

            // 如果是全局显示
            if (mGlobalShow) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }
            }

            try {
                windowManager.addView(mToast.getView(), params);
                // 添加一个移除 Toast 的任务
                HANDLER.postDelayed(() -> cancel(), mToast.getDuration() == Toast.LENGTH_LONG ?
                        mToast.getLongDuration() : mToast.getShortDuration());
                // 注册生命周期管控
                mWindowLifecycle.register(ToastImpl.this);
                // 当前已经显示
                setShow(true);
                // 发送无障碍事件
                trySendAccessibilityEvent(mToast.getView());
            } catch (Exception e) {
                // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
                // java.lang.IllegalStateException: View android.widget.TextView has already been added to the window manager.
                // 如果 WindowManager 绑定的 Activity 已经销毁，则会抛出异常
                // android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@ef1ccb6 is not valid; is your activity running?
                e.printStackTrace();
            }
        }
    };

    private final Runnable mCancelRunnable = new Runnable() {

        @Override
        public void run() {

            try {
                WindowManager windowManager = mWindowLifecycle.getWindowManager();
                if (windowManager == null) {
                    return;
                }

                windowManager.removeViewImmediate(mToast.getView());

            } catch (IllegalArgumentException e) {
                // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
                // java.lang.IllegalArgumentException: View=android.widget.TextView not attached to window manager
                e.printStackTrace();
            } finally {
                // 反注册生命周期管控
                mWindowLifecycle.unregister();
                // 当前没有显示
                setShow(false);
            }
        }
    };
}
