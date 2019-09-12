package com.steeringit.rankstop.utils;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class HorizontalSpace extends RecyclerView.ItemDecoration {

    int space;

    public HorizontalSpace(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = space;
        }else {
            outRect.left = 0;
        }
        outRect.right = space;
        outRect.top = space;
        outRect.bottom = space;
    }
}
