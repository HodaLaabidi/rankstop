package rankstop.steeringit.com.rankstop;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import rankstop.steeringit.com.rankstop.utils.LocaleManager;

public class RankStop extends Application {

    private RefWatcher refWatcher;
    private final String TAG = "App";
    public static LocaleManager localeManager;

    private static RankStop mInstance;
    public static String currentLanguage;

    public static RefWatcher getRefWatcher(Context context) {
        RankStop application = (RankStop) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        //refWatcher = LeakCanary.install(this);
        mInstance = this;
        currentLanguage = localeManager.getLanguage();
    }

    public static synchronized RankStop getInstance() {
        return mInstance;
    }

    public static synchronized String getDeviceLanguage() {
        return currentLanguage;
    }

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
        Toast.makeText(mInstance, "langue = "+newConfig.locale.getLanguage(), Toast.LENGTH_LONG).show();
    }
}
