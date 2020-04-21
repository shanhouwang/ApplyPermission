package com.devin.test.ApplyPermission;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.devin.apply.permission.ApplyPermission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tvApply).setOnClickListener(view -> {
            ApplyPermission.build()
                    .context(MainActivity.this)
                    .permission(Manifest.permission.ACCESS_FINE_LOCATION, null, false)
                    .permission(Manifest.permission.READ_PHONE_STATE, null, false)
                    .permission(Manifest.permission.CAMERA, null, false)
                    .setOnGrantedCallBack(() -> Toast.makeText(MainActivity.this, "授权了", Toast.LENGTH_SHORT).show())
                    .setOnDeniedCallBack(() -> Toast.makeText(MainActivity.this, "没有授权", Toast.LENGTH_SHORT).show())
                    .apply();
        });
    }
}
