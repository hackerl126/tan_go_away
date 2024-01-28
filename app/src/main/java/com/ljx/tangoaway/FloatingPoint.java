package com.ljx.tangoaway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatingPoint {
    private final WindowManager pointManager;
    private final WindowManager.LayoutParams layoutParams;
    private final PointLayout pointLayout;
    private final int[] location = new int[2];
    private boolean pointShowed = false;
    private boolean colorSampled = false;
    private boolean locationSampled = false;
    private boolean serviceStarted = false;
    private int targetColor ;
    private final Thread thread;
    private final TouchThread touchThread;
    private final Object lock;
    @SuppressLint("ClickableViewAccessibility")
    public FloatingPoint(Context context, int x, int y){
        pointManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        pointLayout = new PointLayout(context);
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
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        pointManager.addView(pointLayout, layoutParams);
        pointLayout.setOnTouchListener(new FloatingPoint.PointOnTouchListener());
        pointLayout.setVisibility(View.GONE);

        lock = new Object();
        touchThread = new TouchThread(lock,pointLayout);
        thread = new Thread(touchThread);
        thread.start();
        touchThread.stop();
    }
    public void showPoint(){
        pointLayout.setVisibility(View.VISIBLE);
        pointShowed = true;
    }
    public void hidePoint(){
        pointLayout.setVisibility(View.INVISIBLE);
        pointShowed = false;
    }
    public void startService(){
        if(!pointShowed){
            return;
        }
        if(!colorSampled){
            return;
        }
        if(!locationSampled){
            return;
        }
        if(serviceStarted){
            return;
        }
        synchronized (lock){
            lock.notifyAll();
        }
        serviceStarted = true;
    }
    public void stopService(){
        touchThread.stop();
        serviceStarted = false;
    }
    public void colorSample(){
        if(!pointShowed){
            return;
        }
        pointLayout.getLocationOnScreen(location);
        targetColor = ScreenReader.getColor(location[0],location[1]);
        touchThread.updateTargetColor(targetColor);
        colorSampled = true;
    }
    public void locationSample(){
        if(!pointShowed){
            return;
        }
        pointLayout.getLocationOnScreen(location);
        touchThread.updateTargetLocation(location);
        locationSampled = true;
    }

    private class PointOnTouchListener implements View.OnTouchListener {
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
                    pointManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
