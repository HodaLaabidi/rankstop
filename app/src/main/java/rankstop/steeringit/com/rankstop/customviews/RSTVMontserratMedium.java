package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RSTVMontserratMedium extends TextView {

    public RSTVMontserratMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSTVMontserratMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSTVMontserratMedium(Context context) {
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
