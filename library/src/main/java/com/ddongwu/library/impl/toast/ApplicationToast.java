package com.ddongwu.library.impl.toast;

import android.app.Application;

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 15:52 <br/>
 */
public class ApplicationToast extends AbsToast {
    private final ToastImpl mToastImpl;

    public ApplicationToast(Application application) {
        mToastImpl = new ToastImpl(application, this);
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
