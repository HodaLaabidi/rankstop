package rankstop.steeringit.com.rankstop.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class RSRBMedium extends AppCompatRadioButton {
    public RSRBMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSRBMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSRBMedium(Context context) {
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
