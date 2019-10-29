package rankstop.steeringit.com.rankstop.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import rankstop.steeringit.com.rankstop.RankStop;

public class RSNetwork {

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
