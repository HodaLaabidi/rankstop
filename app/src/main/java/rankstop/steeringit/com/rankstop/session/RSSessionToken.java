package rankstop.steeringit.com.rankstop.session;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.data.model.db.FakeUser;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.db.UserInfo;
import rankstop.steeringit.com.rankstop.data.model.network.RSLocalStorage;
import rankstop.steeringit.com.rankstop.data.model.network.RSLocalTokenGest;
import rankstop.steeringit.com.rankstop.utils.RSJWTParser;

import static android.content.Context.MODE_PRIVATE;

public class RSSessionToken {
    private final static String SHARED_PREFERENCES_TOKEN = "local_token";

    public static void startSession(String token, Boolean statut_gest) {

        RSLocalTokenGest rsLocalStorage = new RSLocalTokenGest(token, statut_gest);
        saveIntoSharedPreferences(rsLocalStorage);
    }

    public static void refreshLocalStorage(String token, Boolean statut_gest) {
        RSLocalTokenGest rsLocalStorage = new RSLocalTokenGest(token, statut_gest);
        saveIntoSharedPreferences(rsLocalStorage);
    }

    private static void saveIntoSharedPreferences(RSLocalTokenGest rsLocalStorage) {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_TOKEN, new Gson().toJson(rsLocalStorage));
        editor.commit();
    }

    public static String getUsergestToken() {
        return getLocalStorage().getToken_geust();
    }

    public static boolean getStatutGestConnected() {
        return getLocalStorage().isStatut_geust_conected();
    }

    public static RSLocalTokenGest getLocalStorage() {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_TOKEN, MODE_PRIVATE);
        String localStorage = sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null);
        return new Gson().fromJson(localStorage, RSLocalTokenGest.class);
    }

    public static void cancelSession() {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SHARED_PREFERENCES_TOKEN);
        editor.commit();
    }
}
