package com.ljx.tangoaway;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MainAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        /*if(String.valueOf(event.getPackageName()).equals("com.ljx.tangoaway") && event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
            Log.d("MainAccessibilityService", "onAccessibilityEvent: " + event.getPackageName() + event.getEventType());
        }*/
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onCreate(){
        TouchTool.setService(this);
    }
}
