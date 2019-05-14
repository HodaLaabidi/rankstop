package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import rankstop.steeringit.com.rankstop.R;


public class SplashScreenActivity extends BaseActivity {

    private static final long SPLASH_SCREEN_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        // Waiting 3s before starting the app
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, ContainerActivity.class));
                finish();
            }
        }, SPLASH_SCREEN_TIMER);

    }
}
