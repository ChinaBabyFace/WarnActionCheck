package com.shark.checkwarnaction;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.lody.whale.xposed.XC_MethodHook;
import com.lody.whale.xposed.XposedHelpers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AppCheck {
    public static final String TAG = "WarnAction";
    public static long APP_START_TIME = System.currentTimeMillis();
    public static DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public static void start(Context app) {
        hookMethod(app, "java.net.NetworkInterface", "getHardwareAddress", "获取硬件MAC地址");
        hookMethod(app, "android.net.wifi.WifiInfo", "getMacAddress", "获取WIFI MAC地址");
        hookMethod(app, "java.net.NetworkInterface", "getInetAddresses", "获取IP地址");
        hookMethod(app, "android.net.wifi.WifiInfo", "getIpAddress", "获取WIFI IP地址");
        hookMethod(app, "android.telephony.TelephonyManager", "getDeviceId", "获取IMEI");
        hookMethod(app, "android.content.ClipboardManager", "getPrimaryClip", "读取剪贴板内容");
        hookMethod(app, "android.location.LocationManager", "getLastKnownLocation", "获取上次定位", String.class);
        hookMethod(app, "android.provider.Settings$Secure", "getStringForUser", "获取Android ID", ContentResolver.class,String.class,int.class);
        hookMethod(app, "android.app.ActivityManager", "getRunningAppProcesses", "读取手机应用列表");

        //定位 相机 存储 拨打打电话 短信
    }

    public static void hookMethod(Context app, String className, String methodName, String warn, Object... param) {
        ClassLoader cl = app.getClassLoader();
        List<Object> list = new ArrayList<>(Arrays.asList(param));
        list.add(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.e(TAG, "应用启动时间: " + dateFormat.format(new Date(APP_START_TIME)));
                Log.e(TAG, "行为触发时间: " + dateFormat.format(new Date(System.currentTimeMillis())));
                Log.e(TAG, "触发敏感行为: " + warn);
                Log.e(TAG, "触发敏感函数: " + className + "." + methodName);
                Log.e(TAG, getStackTrace(className + "." + methodName, new Exception("SgccHook").getStackTrace()));

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
