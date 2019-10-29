package rankstop.steeringit.com.rankstop.ui.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import rankstop.steeringit.com.rankstop.R;

import static com.crashlytics.android.Crashlytics.log;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_SCREEN_TIMER = 5000;
    private static final String TAG = SplashScreen.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        launchSplashScreen();
        checkingDeviceDensity();



    }



    private void checkingDeviceDensity() {

        // return 0.75 if it's LDPI
        // return 1.0 if it's MDPI
        // return 1.3 if it's TVDPI
        // return 1.5 if it's HDPI
        // return 2.0 if it's XHDPI
        // return 3.0 if it's XXHDPI
        // return 4.0 if it's XXXHDPI

        float density = getResources().getDisplayMetrics().density;
        Log.e(TAG, " device density = "+density);

    }

    private void launchSplashScreen() {


        // Waiting 5s before starting the app

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, ContainerActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_SCREEN_TIMER);
    }
}
