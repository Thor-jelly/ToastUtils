package com.ddongwu.library.impl.toast;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/2/2 16:00 <br/>
 */
public class NotificationToast extends SystemToast {

    /**
     * 是否已经 Hook 了一次通知服务
     */
    private static boolean sHookService;

    public NotificationToast(Application application) {
        super(application);
    }

    @Override
    public void show() {
        hookNotificationService();
        super.show();
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    @SuppressWarnings({"JavaReflectionMemberAccess", "SoonBlockedPrivateApi"})
    private static void hookNotificationService() {
        if (sHookService) {
            return;
        }
        sHookService = true;
        try {
            // 获取到 Toast 中的 getService 静态方法
            Method getService = Toast.class.getDeclaredMethod("getService");
            getService.setAccessible(true);
            // 执行方法，会返回一个 INotificationManager$Stub$Proxy 类型的对象
            final Object iNotificationManager = getService.invoke(null);
            if (iNotificationManager == null) {
                return;
            }
            // 如果这个对象已经被动态代理过了，并且已经 Hook 过了，则不需要重复 Hook
            if (Proxy.isProxyClass(iNotificationManager.getClass()) &&
                    Proxy.getInvocationHandler(iNotificationManager) instanceof NotificationServiceProxy) {
                return;
            }
            Object iNotificationManagerProxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{Class.forName("android.app.INotificationManager")},
                    new NotificationServiceProxy(iNotificationManager));
            // 将原来的 INotificationManager$Stub$Proxy 替换掉
            Field sService = Toast.class.getDeclaredField("sService");
            sService.setAccessible(true);
            sService.set(null, iNotificationManagerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

final class NotificationServiceProxy implements InvocationHandler {
    /**
     * 被代理的对象
     */
    private final Object mSource;

    public NotificationServiceProxy(Object source) {
        mSource = source;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "enqueueToast":
            case "enqueueToastEx":
            case "cancelToast":
                // 将包名修改成系统包名，这样就可以绕过系统的拦截
                // 部分华为机将 enqueueToast 修改成了 enqueueToastEx
                args[0] = "android";
                break;
            default:
                break;
        }
        // 使用动态代理
        return method.invoke(mSource, args);
    }
}