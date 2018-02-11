package com.devin.apply.permission.utils;

import android.Manifest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Devin on 2017/3/17.
 */
public class PermissionTips {

    /**
     * 权限列表
     */
    private static Map<String, String> permissionTips = new HashMap<>();

    static {
        permissionTips.put(Manifest.permission.READ_PHONE_STATE, "读取手机状态权限");
        permissionTips.put(Manifest.permission.CALL_PHONE, "拨打电话权限");
        permissionTips.put(Manifest.permission.CAMERA, "相机/摄像权限");
        permissionTips.put(Manifest.permission.RECORD_AUDIO, "录音权限");
        permissionTips.put(Manifest.permission.READ_CONTACTS, "读取联系人信息权限");
        permissionTips.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储空间权限");
        permissionTips.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储空间权限");
        permissionTips.put(Manifest.permission.ACCESS_FINE_LOCATION, "获取位置权限");
    }

    public static String get(String key) {
        return permissionTips.get(key);
    }

}
