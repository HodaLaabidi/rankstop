package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RSTVSemiBold extends AppCompatTextView {
    public RSTVSemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSTVSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSTVSemiBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/montserrat_semi_bold.ttf");
            setTypeface(tf);
        }
    }
}
