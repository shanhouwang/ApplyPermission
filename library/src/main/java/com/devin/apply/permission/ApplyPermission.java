package com.devin.apply.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.devin.apply.permission.activity.ApplyActivity;
import com.devin.apply.permission.model.PermissionModel;
import com.devin.apply.permission.utils.PermissionUtils;

/**
 * Created by Devin on 2017/7/4.
 * <p>
 * 单例模式
 */

public class ApplyPermission {

    public static final String KEY_PERMISSION_REQUEST_CODE = "key_permission_request_code";
    public static int REQUEST_PERMISSION_CODE = 1;

    /**
     * 成功回调
     */
    private OnGrantedCallBack mOnGrantedCallBack;

    /**
     * 失败回调
     */
    private OnDeniedCallBack mOnDeniedCallBack;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 权限名称
     */
    private String permission;

    /**
     * 不授权弹窗不可以取消
     */
    private boolean must;

    private String tip;

    private ApplyPermission() {
    }

    public static ApplyPermission build() {
        ApplyPermission p = new ApplyPermission();
        return p;
    }

    /**
     * App的名称
     */
    public static String APP_NAME;

    public synchronized ApplyPermission context(Context context) {
        this.context = context;
        return this;
    }

    public synchronized ApplyPermission permission(String permission) {
        this.permission = permission;
        return this;
    }

    public synchronized ApplyPermission tip(String tip) {
        this.tip = tip;
        return this;
    }

    public synchronized ApplyPermission must(boolean must) {
        this.must = must;
        return this;
    }

    public synchronized ApplyPermission setOnGrantedCallBack(OnGrantedCallBack callBack) {
        this.mOnGrantedCallBack = callBack;
        return this;
    }

    public synchronized ApplyPermission setOnDeniedCallBack(OnDeniedCallBack callBack) {
        this.mOnDeniedCallBack = callBack;
        return this;
    }

    /**
     * 权限申请
     */
    public synchronized void apply() {

        if (TextUtils.isEmpty(APP_NAME)) {
            APP_NAME = getAppName(context);
        }

        if (PermissionUtils.checkPermissions(context, permission)) {
            mOnGrantedCallBack.onGranted();
            return;
        }

        PermissionModel p = new PermissionModel();
        p.onGrantedCallBack = mOnGrantedCallBack;
        p.onDeniedCallBack = mOnDeniedCallBack;
        p.name = permission;
        p.tip = tip;
        p.must = must;
        p.requestCode = createRequestCode();
        ApplyActivity.permissions.put(p.requestCode, p);

        // 跳转透明申请权限页面
        Intent i = new Intent(context, ApplyActivity.class);
        i.putExtra(KEY_PERMISSION_REQUEST_CODE, p.requestCode);
        context.startActivity(i);
    }

    private static final int RANGE = 65535;

    private int createRequestCode() {
        if (REQUEST_PERMISSION_CODE < RANGE) {
            ++REQUEST_PERMISSION_CODE;
        } else {
            REQUEST_PERMISSION_CODE = 1;
        }
        return REQUEST_PERMISSION_CODE;
    }

    public interface OnGrantedCallBack {

        void onGranted(); // 授权
    }

    public interface OnDeniedCallBack {

        void onDenied(); // 拒绝
    }

    /**
     * 获取App名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return (String) pm.getApplicationLabel(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
