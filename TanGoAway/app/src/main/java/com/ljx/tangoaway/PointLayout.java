package com.ljx.tangoaway;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PointLayout extends FrameLayout{
    public PointLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public PointLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PointLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.point_layout,this,true);
    }
    /*
    public void sendEvent(){
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
    }
     */
}
