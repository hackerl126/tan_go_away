package com.ljx.tangoaway;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;

public class TouchTool {
    public static AccessibilityService service;
    public void touch(int x, int y){
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x , y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0, 1));
        GestureDescription gesture = builder.build();
        service.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e("Tag", "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e("Tag", "onCancelled: 取消..........");
            }
        }, null);
    }
    public static void setService(AccessibilityService service){
        TouchTool.service = service;
    }
}
