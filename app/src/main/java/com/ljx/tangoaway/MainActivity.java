package com.ljx.tangoaway;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        FloatingWindow floatingWindow = new FloatingWindow(MainActivity.this);

        Button getOverlayPermissionButton = findViewById(R.id.getOverlayPermissionButton);
        Button getAccessibilityPermissionButton = findViewById(R.id.getAccessibilityPermissionButton);
        Button getMediaProjectionButton = findViewById(R.id.getMediaProjectionButton);
        Button showWindowButton = findViewById(R.id.showWindowButton);
        Button hideWindowButton = findViewById(R.id.hideWindowButton);

        getOverlayPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "已有悬浮窗权限", Toast.LENGTH_SHORT).show();
                }
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        });

        getAccessibilityPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAccessibilitySettingsOn(MainActivity.this, MainAccessibilityService.class.getName())) {
                    Toast.makeText(MainActivity.this, "已有无障碍权限", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        getMediaProjectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                Intent intent = new Intent(MainActivity.this, CaptureService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }else {
                    Toast.makeText(MainActivity.this, "安卓系统版本过低，无法运行！", Toast.LENGTH_SHORT).show();
                }
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1);
            }
        });

        showWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "请授权悬浮窗权限然后重新运行", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
                } else if (mediaProjection == null) {
                    Toast.makeText(MainActivity.this, "请允许屏幕录制然后重新运行", Toast.LENGTH_SHORT).show();
                    mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    Intent intent = new Intent(MainActivity.this, CaptureService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }else {
                        Toast.makeText(MainActivity.this, "安卓系统版本过低，无法运行！", Toast.LENGTH_SHORT).show();
                    }
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1);
                } else if (!isAccessibilitySettingsOn(MainActivity.this, MainAccessibilityService.class.getName())) {
                    Toast.makeText(MainActivity.this, "请授权无障碍权限然后重新运行", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } else floatingWindow.showWindow();
            }
        });

        hideWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingWindow.hideWindow();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "已取消授权！", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "录屏权限已获取", Toast.LENGTH_SHORT).show();

            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            setUpVirtualDisplay();
        }
    }

    private void setUpVirtualDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        ImageReader imageReader = ImageReader.newInstance(dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 16);
        mediaProjection.createVirtualDisplay("ScreenCapture",
                dm.widthPixels, dm.heightPixels, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
        ScreenReader.reader = imageReader;
    }

    public static boolean isAccessibilitySettingsOn(Context context, String className) {
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的服务列表
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);

        for (int i = 0; i < runningServices.size(); i++) {
            ComponentName service = runningServices.get(i).service;
            if (service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}