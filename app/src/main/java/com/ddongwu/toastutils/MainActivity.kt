package com.ddongwu.toastutils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ddongwu.library.ToastParams
import com.ddongwu.library.ToastUtils
import com.ddongwu.library.impl.style.CustomViewToastStyle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ToastUtils.init(this.application)
    }

    fun showToast(view: View) {
        //展示默认样式
        ToastUtils.show("我是默认展示样式")
    }

    fun showLongToast(view: View) {
        ToastUtils.showLong("我是长Toast： 我的内容是啊啊啊啊啊")
    }

    fun showShortToast(view: View) {
        ToastUtils.showShort("我是短Toast")
    }

    fun showCustomToast(view: View) {
        val toastParams = ToastParams()
        toastParams.text = "我是自定义toast"
        toastParams.style = CustomViewToastStyle(R.layout.toast_custom, Gravity.CENTER)
        ToastUtils.show(toastParams)
    }
}