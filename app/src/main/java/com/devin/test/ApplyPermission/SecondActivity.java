package com.devin.test.ApplyPermission;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.devin.apply.permission.ApplyPermission;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_apply).setOnClickListener((view) -> {
            for (int i = 0; i < 10; i++) {
                ApplyPermission.build()
                        .context(SecondActivity.this)
                        .tip("王八蛋权限")
                        .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .setOnGrantedCallBack(() -> Toast.makeText(SecondActivity.this, "授权了", Toast.LENGTH_SHORT).show())
                        .setOnDeniedCallBack(() -> Toast.makeText(SecondActivity.this, "没有授权", Toast.LENGTH_SHORT).show())
                        .apply();
            }
        });

        findViewById(R.id.tvApply).setOnClickListener(view -> {
            ApplyPermission.build()
                    .context(SecondActivity.this)
                    .tip("哈哈哈")
                    .permission(Manifest.permission.READ_PHONE_STATE)
                    .setOnGrantedCallBack(() -> Toast.makeText(SecondActivity.this, "授权了", Toast.LENGTH_SHORT).show())
                    .setOnDeniedCallBack(() -> Toast.makeText(SecondActivity.this, "没有授权", Toast.LENGTH_SHORT).show())
                    .apply();
        });
    }
}
