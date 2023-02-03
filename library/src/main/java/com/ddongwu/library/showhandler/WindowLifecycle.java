package com.ddongwu.library.showhandler;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.ddongwu.library.impl.toast.ToastImpl;


/**
 * 类描述：WindowManager生命周期  <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 15:29 <br/>
 */
public class WindowLifecycle implements Application.ActivityLifecycleCallbacks {

    /**
     * 当前 Activity 对象
     */
    private Activity mActivity;

    /**
     * 当前 Application 对象
     */
    private Application mApplication;

    /**
     * 自定义 Toast 实现类
     */
    private ToastImpl mToastImpl;

    public WindowLifecycle(Activity activity) {
        mActivity = activity;
    }

    public WindowLifecycle(Application application) {
        mApplication = application;
    }

    /**
     * 获取 WindowManager 对象
     */
    public WindowManager getWindowManager() {
        if (mActivity != null) {
            if (mActivity.isDestroyed()) {
                return null;
            }
            return mActivity.getWindowManager();
        } else if (mApplication != null) {
            return (WindowManager) mApplication.getSystemService(Context.WINDOW_SERVICE);
        }

        return null;
    }

    /**
     * {@link Application.ActivityLifecycleCallbacks}
     */

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mActivity != activity) {
            return;
        }

        if (mToastImpl == null) {
            return;
        }

        // 不能放在 onStop 或者 onDestroyed 方法中，因为此时新的 Activity 已经创建完成，必须在这个新的 Activity 未创建之前关闭这个 WindowManager
        // 调用取消显示会直接导致新的 Activity 的 onCreate 调用显示吐司可能显示不出来的问题，又或者有时候会立马显示然后立马消失的那种效果
        mToastImpl.cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (mActivity != activity) {
            return;
        }

        if (mToastImpl != null) {
            mToastImpl.cancel();
        }

        unregister();
        mActivity = null;
    }

    /**
     * 注册
     */
    public void register(ToastImpl impl) {
        mToastImpl = impl;
        if (mActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.registerActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * 反注册
     */
    public void unregister() {
        mToastImpl = null;
        if (mActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.unregisterActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }
}
