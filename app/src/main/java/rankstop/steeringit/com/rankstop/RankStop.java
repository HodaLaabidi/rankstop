package rankstop.steeringit.com.rankstop;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import rankstop.steeringit.com.rankstop.utils.ConnectivityReceiver;

public class RankStop extends Application {

    private RefWatcher refWatcher;

    private static RankStop mInstance;

    public static RefWatcher getRefWatcher(Context context) {
        RankStop application = (RankStop) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
        mInstance = this;
    }

    public static synchronized RankStop getInstance(){
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
