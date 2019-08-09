package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import rankstop.steeringit.com.rankstop.R;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_SCREEN_TIMER = 5000;
    private static final String TAG = SplashScreen.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% //

        // checking density of the device
        // return 0.75 if it's LDPI
        // return 1.0 if it's MDPI
        // return 1.3 if it's TVDPI
        // return 1.5 if it's HDPI
        // return 2.0 if it's XHDPI
        // return 3.0 if it's XXHDPI
        // return 4.0 if it's XXXHDPI

        float density = getResources().getDisplayMetrics().density;
        Log.e(TAG, " device density = "+density);

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% //

        // Waiting 5s before starting the app

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, ContainerActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_SCREEN_TIMER);
    }
}
