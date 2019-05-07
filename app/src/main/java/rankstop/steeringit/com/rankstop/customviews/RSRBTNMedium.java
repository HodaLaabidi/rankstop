package rankstop.steeringit.com.rankstop.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

public class RSRBTNMedium extends AppCompatRadioButton {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RSRBTNMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSRBTNMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSRBTNMedium(Context context) {
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
