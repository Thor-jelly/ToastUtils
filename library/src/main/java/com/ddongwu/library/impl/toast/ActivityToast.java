package com.ddongwu.library.impl.toast;

import android.app.Activity;

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 15:39 <br/>
 */
public class ActivityToast extends AbsToast {
    private final ToastImpl mToastImpl;

    public ActivityToast(Activity activity) {
        mToastImpl = new ToastImpl(activity, this);
    }

    @Override
    public void show() {
        // 替换成 WindowManager 来显示
        mToastImpl.show();
    }

    @Override
    public void cancel() {
        // 取消 WindowManager 的显示
        mToastImpl.cancel();
    }
}
