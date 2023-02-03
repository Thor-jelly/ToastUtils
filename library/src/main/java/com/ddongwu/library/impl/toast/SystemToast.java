package com.ddongwu.library.impl.toast;

import android.app.Application;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ddongwu.library.iinterface.IToast;

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 15:49 <br/>
 */
public class SystemToast extends Toast implements IToast {

    /**
     * Toast 消息 View
     */
    private TextView mMessageView;

    public SystemToast(Application application) {
        super(application);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        if (view == null) {
            mMessageView = null;
            return;
        }
        mMessageView = findMessageView(view);
    }

    @Override
    public void setText(CharSequence text) {
        super.setText(text);
        if (mMessageView == null) {
            return;
        }
        mMessageView.setText(text);
    }
}
