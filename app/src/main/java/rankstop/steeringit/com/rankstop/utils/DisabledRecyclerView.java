package com.steeringit.rankstop.utils;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DisabledRecyclerView extends RecyclerView {
    public DisabledRecyclerView(Context context) {
        super(context);
    }

    public DisabledRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisabledRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}