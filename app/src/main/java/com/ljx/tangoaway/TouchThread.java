package com.ljx.tangoaway;

import android.util.Log;

public class TouchThread implements Runnable{
    private final Object lock;
    private boolean locked = false;
    private final PointLayout pointLayout;
    private int targetColor;
    private int[] targetLocation = new int[2];
    private final int[] location = new int[2];
    private final TouchTool touchTool;
    public TouchThread(Object lock,PointLayout pointLayout){
        this.lock = lock;
        this.pointLayout = pointLayout;
        touchTool = new TouchTool();
    }
    @Override
    public void run() {
        synchronized (lock){
            while (true) {
                if(locked){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                locked = false;

                pointLayout.getLocationOnScreen(location);
                int color = GBData.getColor(location[0],location[1]);

                if(color <= targetColor + 1000 && color >= targetColor - 1000) {
                    touchTool.touch(targetLocation[0],targetLocation[1]);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    public void stop(){
        locked = true;
    }
    public void updateTargetColor(int targetColor){
        this.targetColor = targetColor;
    }
    public void updateTargetLocation(int[] targetLocation){
        this.targetLocation = targetLocation;
    }
}
