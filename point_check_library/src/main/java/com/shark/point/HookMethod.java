package com.shark.point;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedHelpers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HookMethod {

    public static void hookMethod(Context app, String className, String methodName, OnHookListener listener, Object... param) {
        ClassLoader cl = app.getClassLoader();
        List<Object> list = new ArrayList<>(Arrays.asList(param));
        list.add(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (listener == null) return;
                listener.onParamHooked(param);
            }
        });
        try {
            XposedHelpers.findAndHookMethod(
                    className,
                    cl,
                    methodName,
                    list.toArray()
            );
        } catch (Exception e) {
            Log.e("shark", "hook result:" + e.getMessage());
        }
    }

    public static String getStackTrace(String method, StackTraceElement[] s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("函数调用栈：\n");
        for (int i = 0; i < s.length; i++) {
            String traceName = s[i].getClassName() + "." + s[i].getMethodName();
            if (traceName == null) continue;
            if (traceName.contains("com.lody.whale")) continue;
            if (traceName.contains("com.shark.checkwarnaction")) continue;
            if (method.equals(traceName)) continue;

            sb.append(s[i].toString() + "\n");
        }
        return sb.toString();
    }

    public static String getProcessInfo(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName + ",Pid:" + pid;
            }
        }
        return "暂无";
    }
}
