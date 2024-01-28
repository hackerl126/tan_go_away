package com.ljx.tangoaway;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;


public class FloatingWindow {
    private final WindowManager windowManager;
    private final WindowManager.LayoutParams layoutParams;
    private final View windowLayout;
    private final ArrayList<FloatingPoint> pointsList = new ArrayList<FloatingPoint>();

    public FloatingWindow(Context context) {
        //新建WindowManager
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 新建悬浮窗控件
        windowLayout = View.inflate(context, R.layout.window_layout, null);


        // 设置LayoutParam
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.x = 100;
        layoutParams.y = 800;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(windowLayout, layoutParams);
        windowLayout.setOnTouchListener(new WindowOnTouchListener());
        windowLayout.setVisibility(View.GONE);

        //注册按钮监听器
        Button showPointsButton = windowLayout.findViewById(R.id.showPointsButton);
        Button hidePointsButton = windowLayout.findViewById(R.id.hidePointsButton);
        Button startButton = windowLayout.findViewById(R.id.startButton);
        Button stopButton = windowLayout.findViewById(R.id.stopButton);
        Button colorSampleButton = windowLayout.findViewById(R.id.colorSampleButton);
        Button locationSampleButton = windowLayout.findViewById(R.id.locationSampleButton);

        showPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPoints();
            }
        });

        hidePointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePoints();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });

        colorSampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampleColor();
            }
        });

        locationSampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampleLocation();
            }
        });

        //在pointsList中创建3个FloatingPoint对象
        while(pointsList.size()<3){
            pointsList.add(new FloatingPoint(context,(pointsList.size()*100+100),1500));
        }
    }

    public void showWindow() {
        windowLayout.setVisibility(View.VISIBLE);
    }

    public void hideWindow() {
        stopService();
        hidePoints();
        windowLayout.setVisibility(View.INVISIBLE);
    }

    private void showPoints(){
        for (FloatingPoint point:pointsList) {
            point.showPoint();
        }
    }
    private void hidePoints(){
        stopService();
        for (FloatingPoint point:pointsList) {
            point.hidePoint();
        }
    }
    private void startService(){
        for (FloatingPoint point:pointsList) {
            point.startService();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void stopService(){
        for (FloatingPoint point:pointsList) {
            point.stopService();
        }
    }
    private void sampleColor(){
        for (FloatingPoint point:pointsList) {
            point.colorSample();
        }
    }
    private void sampleLocation(){
        for (FloatingPoint point:pointsList) {
            point.locationSample();
        }
    }
    private class WindowOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
