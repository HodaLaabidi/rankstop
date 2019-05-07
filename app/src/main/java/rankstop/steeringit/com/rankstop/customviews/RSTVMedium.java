package rankstop.steeringit.com.rankstop.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RSTVMedium extends AppCompatTextView {

    // LOLLIPOP version implementation
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RSTVMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RSTVMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSTVMedium(Context context) {
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
