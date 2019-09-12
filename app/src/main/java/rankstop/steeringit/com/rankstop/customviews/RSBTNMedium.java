package com.steeringit.rankstop.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import com.google.android.material.button.MaterialButton;
import android.util.AttributeSet;

public class RSBTNMedium extends MaterialButton {

    // LOLLIPOP version implementation
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RSBTNMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSBTNMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSBTNMedium(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_medium.ttf");
            setTypeface(tf);
        }
    }
}
