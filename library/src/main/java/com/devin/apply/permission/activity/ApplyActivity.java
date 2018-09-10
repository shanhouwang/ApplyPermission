package com.devin.apply.permission.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.Toast;

import com.devin.apply.permission.ApplyPermission;
import com.devin.apply.permission.model.PermissionModel;
import com.devin.apply.permission.utils.PermissionTips;
import com.devin.apply.permission.utils.PermissionUtils;
import com.devin.apply.permission.utils.SPUtils;

import java.util.Map;

/**
 * Created by Devin on 2017/7/5.
 * <p>
 * 权限申请Activity
 */
public class ApplyActivity extends AppCompatActivity {

    public static Map<Integer, PermissionModel> permissions = new ArrayMap<>();

    public Activity mActivity;
    private int mRequestCode;
    private PermissionModel permission;
    private SPUtils sp;

    private static final String CLICKED = "clicked";
    private static final String NAME = "permission.sp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mRequestCode = getIntent().getIntExtra(ApplyPermission.KEY_PERMISSION_REQUEST_CODE, 0);
        if (mRequestCode == 0) {
            Toast.makeText(this, "内部发生了错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (permissions.get(mRequestCode) == null) {
            Toast.makeText(this, "内部发生了错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sp = new SPUtils(this, NAME);
        permission = permissions.get(mRequestCode);
        if (permission.onGrantedCallBack == null)
            throw new RuntimeException("OnGrantedCallBack 不能为 Null");
        mActivity = this;
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp == null) {
            return;
        }
        if (sp.getBoolean(CLICKED)) {
            sp.putBoolean(CLICKED, false);
            // 如果已经授权
            if (PermissionUtils.checkPermissions(mActivity, permission.name)) {
                permission.onGrantedCallBack.onGranted();
                this.finish();
            } else {
                if (permission.must) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("为了应用正常使用，请您确认打开");
                    String p = PermissionTips.get(permission.name);
                    sb.append(TextUtils.isEmpty(p) ? "相应权限" : p);
                    sb.append("\n设置路径：设置 → 应用 → " + ApplyPermission.APP_NAME + " → 权限");

                    new AlertDialog.Builder(mActivity)
                            .setTitle("提示")
                            .setMessage(sb.toString())
                            .setPositiveButton("跳转设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PermissionUtils.openPermissionSettings(mActivity);
                                    sp.putBoolean(CLICKED, true);
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    permission.onDeniedCallBack.onDenied();
                    this.finish();
                }
            }
        }
    }

    private void checkPermission() {
        // 如果已经授权
        if (PermissionUtils.checkPermissions(mActivity, permission.name)) {
            permission.onGrantedCallBack.onGranted();
            finish();
            return;
        }
        if (PermissionUtils.isOverMarshmallow()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission.name)) {
                if (permission.must) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("获取权限")
                            .setMessage(TextUtils.isEmpty(permission.tip) ? "我们需要" + PermissionTips.get(permission.name) : permission.tip)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(mActivity, new String[]{permission.name}, mRequestCode);
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("获取权限")
                            .setMessage(TextUtils.isEmpty(permission.tip) ? "我们需要" + PermissionTips.get(permission.name) : permission.tip)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(mActivity, new String[]{permission.name}, mRequestCode);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(null != permission.onDeniedCallBack) permission.onDeniedCallBack.onDenied();
                                    mActivity.finish();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{permission.name}, mRequestCode);
            }
            return;
        }
        // 说明 <M 版本的没有授权
        StringBuilder sb = new StringBuilder();
        sb.append("为了应用正常使用，请您确认打开");
        sb.append(PermissionTips.get(permission.name));

        if (permission.must) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("提示")
                    .setMessage(sb.toString())
                    .setPositiveButton("跳转设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.openPermissionSettings(mActivity);
                            sp.putBoolean(CLICKED, true);
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            AlertDialog d = new AlertDialog.Builder(mActivity)
                    .setTitle("提示")
                    .setMessage(sb.toString())
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(null != permission.onDeniedCallBack) permission.onDeniedCallBack.onDenied();
                            mActivity.finish();
                        }
                    })
                    .setPositiveButton("跳转设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.openPermissionSettings(mActivity);
                            sp.putBoolean(CLICKED, true);
                        }
                    })
                    .setCancelable(false)
                    .create();
            d.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == mRequestCode) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission.onGrantedCallBack.onGranted();
                mActivity.finish();
            } else if (permissions != null && permissions.length > 0) {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (showRationale) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("为了应用正常使用，请您确认打开");
                    String p = PermissionTips.get(permission.name);
                    sb.append(TextUtils.isEmpty(p) ? "相应权限" : p);
                    sb.append("\n设置路径：设置 → 应用 → " + ApplyPermission.APP_NAME + " → 权限");
                    if (permission.must) {
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage(sb.toString())
                                .setPositiveButton("跳转设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionUtils.openPermissionSettings(mActivity);
                                        sp.putBoolean(CLICKED, true);
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    } else {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("提示")
                                .setMessage(sb.toString())
                                .setPositiveButton("跳转设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PermissionUtils.openPermissionSettings(mActivity);
                                        sp.putBoolean(CLICKED, true);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(null != permission.onDeniedCallBack) permission.onDeniedCallBack.onDenied();
                                        mActivity.finish();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                } else {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("获取权限")
                            .setMessage(TextUtils.isEmpty(permission.tip) ? "我们需要" + PermissionTips.get(permission.name) : permission.tip)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(mActivity, new String[]{permission.name}, mRequestCode);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (null != permission.onDeniedCallBack) permission.onDeniedCallBack.onDenied();
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        }
    }
}