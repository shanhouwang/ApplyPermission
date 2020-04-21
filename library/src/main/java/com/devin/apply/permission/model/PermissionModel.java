package com.devin.apply.permission.model;

import com.devin.apply.permission.ApplyPermission;

/**
 * Created by Devin on 2017/7/6.
 */

public class PermissionModel {

    /**
     * 成功回调
     */
    public ApplyPermission.OnGrantedCallBack onGrantedCallBack;

    /**
     * 失败回调
     */
    public ApplyPermission.OnDeniedCallBack onDeniedCallBack;

    /**
     * 权限名称
     */
    public String name;

    public String tip;

    public boolean must = false;

    /**
     * 0 < requestCode < 65536
     */
    public int requestCode;

    public int delay;
}
