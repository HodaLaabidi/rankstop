package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RSTVMontserratBold extends TextView {
    public RSTVMontserratBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSTVMontserratBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSTVMontserratBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_bold.ttf");
            setTypeface(tf);
        }
    }
}
