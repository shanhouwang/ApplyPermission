package com.devin.test.ApplyPermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.devin.apply.permission.ApplyPermission;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tvApply).setOnClickListener(view -> {
            ApplyPermission.build()
                    .context(this)
                    .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setOnGrantedCallBack(() -> {
                        startActivity(new Intent(MainActivity.this, SecondActivity.class));
                    })
                    .setOnDeniedCallBack(() -> Toast.makeText(MainActivity.this, "没有授权", Toast.LENGTH_SHORT).show())
                    .apply();
        });
    }
}
