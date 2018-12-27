package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RSTVMontserratRegular extends TextView {

    public RSTVMontserratRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSTVMontserratRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSTVMontserratRegular(Context context) {
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
