package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

public class RSETMontserratRegular extends TextInputEditText {

    public RSETMontserratRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSETMontserratRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSETMontserratRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_regular.ttf");
            setTypeface(tf);
        }
    }

}
