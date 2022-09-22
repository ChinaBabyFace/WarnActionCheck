package com.shark.warnactioncheck.demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.google.gson.reflect.TypeToken;
import com.lody.whale.xposed.XC_MethodHook;
import com.shark.point.HookMethod;
import com.shark.point.OnHookListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, Object> map = new HashMap<>();
        HookMethod.hookMethod(
                getApplication(),
                "com.shark.warnactioncheck.demo.DataTrackManager",
                "sendHit",
                param -> new AlertDialog.Builder(ActivityUtils.getTopActivity())
                        .setTitle(param.args[0].toString())
                        .setMessage(param.args[1].toString())
                        .setCancelable(false)
                        .setPositiveButton("关闭", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show(),
                String.class, Map.class);
        setContentView(R.layout.activity_main);

        map.put("Shark", "shark1");
        map.put("Shark2", "shark2");
        DataTrackManager.sendHit("test", map);
    }
}