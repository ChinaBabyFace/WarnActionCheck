package com.shark.point;

import com.lody.whale.xposed.XC_MethodHook;

public interface OnHookListener {
    void onParamHooked(XC_MethodHook.MethodHookParam param);
}
