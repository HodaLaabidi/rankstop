package rankstop.steeringit.com.rankstop.session;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.RankStop;
import rankstop.steeringit.com.rankstop.data.model.db.User;
import rankstop.steeringit.com.rankstop.data.model.db.UserInfo;
import rankstop.steeringit.com.rankstop.data.model.network.RSLocalStorage;
import rankstop.steeringit.com.rankstop.utils.RSJWTParser;

import static android.content.Context.MODE_PRIVATE;

public class RSSession {

    private final static String SHARED_PREFERENCES_FILE = "local_storage";

    public static void startSession(String token) {
       // RSJWTParser.getPayload(token);
        User user = new Gson().fromJson(RSJWTParser.getPayload(token).toString(), User.class);
        UserInfo userInfo = new UserInfo(user);
        RSLocalStorage rsLocalStorage = new RSLocalStorage(token, userInfo);

        saveIntoSharedPreferences(rsLocalStorage);
    }

    public static void refreshLocalStorage(UserInfo userInfo) {
        RSLocalStorage rsLocalStorage = getLocalStorage();
        rsLocalStorage.setUserInfo(userInfo);
        saveIntoSharedPreferences(rsLocalStorage);
    }

    private static void saveIntoSharedPreferences(RSLocalStorage rsLocalStorage) {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_FILE, new Gson().toJson(rsLocalStorage));
        editor.commit();
    }

    public static User getCurrentUser() {
        return getLocalStorage().getUserInfo().getUser();
    }

    public static UserInfo getCurrentUserInfo() {
        return getLocalStorage().getUserInfo();
    }

    private static RSLocalStorage getLocalStorage() {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String localStorage = sharedPreferences.getString(SHARED_PREFERENCES_FILE, "");
        return new Gson().fromJson(localStorage, RSLocalStorage.class);
    }

    public static void cancelSession() {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SHARED_PREFERENCES_FILE);
        editor.commit();
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String token = sharedPreferences.getString(SHARED_PREFERENCES_FILE, "");
        return !token.equals("");
    }
}
