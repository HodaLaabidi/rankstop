package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rankstop.steeringit.com.rankstop.R;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_SCREEN_TIMER = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Waiting 5s before starting the app
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, ContainerActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_SCREEN_TIMER);
    }
}
