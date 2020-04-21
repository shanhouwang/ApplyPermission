package com.devin.apply.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.devin.apply.permission.activity.ApplyActivity;
import com.devin.apply.permission.model.PermissionModel;
import com.devin.apply.permission.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Devin on 2017/7/4.
 * <p>
 * 单例模式
 */

public class ApplyPermission {

    public static final String KEY_PERMISSION_REQUEST_CODE = "key_permission_request_code";
    public static int REQUEST_PERMISSION_CODE = 1;
    public static Handler mHandler = new Handler(Looper.getMainLooper());
    public static final int BEGIN_INDEX = 0;

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

    private List<PermissionModel> permissions = new ArrayList<>();

    private Map<Integer, Boolean> permissionsMap = new HashMap<>();

    private int count;

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

    /**
     * @param permissionName 权限名称
     * @return
     */
    public synchronized ApplyPermission permission(String permissionName) {
        this.add(permissionName, null, false);
        return this;
    }

    /**
     * @param permissionName 权限名称
     * @param tip            提示文案
     * @param must           不授权弹窗不可以取消
     * @return
     */
    public synchronized ApplyPermission permission(String permissionName, String tip, boolean must) {
        this.add(permissionName, tip, must);
        return this;
    }

    private void add(String permissionName, String tip, boolean must) {
        final PermissionModel p = new PermissionModel();
        p.onGrantedCallBack = new OnGrantedCallBack() {
            @Override
            public void onGranted() {
                permissionsMap.put(p.index, true);
                boolean granted = true;
                for (Map.Entry<Integer, Boolean> entry : permissionsMap.entrySet()) {
                    if (!entry.getValue()) {
                        granted = false;
                    }
                }
                if (p.index == BEGIN_INDEX) {
                    if (granted) {
                        if (null != mOnGrantedCallBack) {
                            mOnGrantedCallBack.onGranted();
                        }
                    } else {
                        if (null != mOnDeniedCallBack) {
                            mOnDeniedCallBack.onDenied();
                        }
                    }
                }
            }
        };
        p.onDeniedCallBack = new OnDeniedCallBack() {
            @Override
            public void onDenied() {
                permissionsMap.put(p.index, false);
                if (p.index == BEGIN_INDEX) {
                    mOnDeniedCallBack.onDenied();
                }
            }
        };
        p.name = permissionName;
        p.tip = tip;
        p.must = must;
        p.requestCode = createRequestCode();
        this.permissions.add(p);
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
        Collections.reverse(permissions);
        boolean granted = true;
        for (int i = 0; i < permissions.size(); i++) {
            if (!PermissionUtils.checkPermissions(context, permissions.get(i).name)) {
                granted = false;
            }
            permissions.get(i).delay = i * 10;
            permissions.get(i).index = i;
        }
        if (granted) {
            mOnGrantedCallBack.onGranted();
            return;
        }
        for (final PermissionModel permission : permissions) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ApplyActivity.permissions.put(permission.requestCode, permission);
                    // 跳转透明申请权限页面
                    Intent i = new Intent(context, ApplyActivity.class);
                    i.putExtra(KEY_PERMISSION_REQUEST_CODE, permission.requestCode);
                    context.startActivity(i);
                }
            }, permission.delay);
        }
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
