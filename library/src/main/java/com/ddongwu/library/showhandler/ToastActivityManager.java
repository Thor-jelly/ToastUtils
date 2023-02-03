package com.ddongwu.library.showhandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 类描述：Toast activity 管理<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/1 17:45 <br/>
 */
public class ToastActivityManager implements Application.ActivityLifecycleCallbacks {
    private ToastActivityManager() {
    }

    @SuppressLint("StaticFieldLeak")
    private static volatile ToastActivityManager sInstance;

    public static ToastActivityManager getInstance() {
        if (sInstance == null) {
            synchronized (ToastActivityManager.class) {
                if (sInstance == null) {
                    sInstance = new ToastActivityManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 注册 Activity 生命周期监听
     */
    public void register(Application application) {
        if (application == null) {
            return;
        }
        application.registerActivityLifecycleCallbacks(this);
    }

    /**
     * 前台 Activity 对象
     */
    private Activity mForegroundActivity;

    public Activity getForegroundActivity() {
        return mForegroundActivity;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mForegroundActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (mForegroundActivity != activity) {
            return;
        }
        mForegroundActivity = null;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
