package rankstop.steeringit.com.rankstop.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import rankstop.steeringit.com.rankstop.data.model.User;
import rankstop.steeringit.com.rankstop.data.model.UserInfo;
import rankstop.steeringit.com.rankstop.data.model.custom.RSLocalStorage;
import rankstop.steeringit.com.rankstop.utils.RSJWTParser;

import static android.content.Context.MODE_PRIVATE;

public class RSSession {

    private final static String SHARED_PREFERENCES_FILE = "local_storage";

    public static void startSession(Context context, String token) {
        RSJWTParser.getPayload(token);
        User user = new Gson().fromJson(RSJWTParser.getPayload(token).toString(), User.class);
        UserInfo userInfo = new UserInfo(user);
        RSLocalStorage rsLocalStorage = new RSLocalStorage(token, userInfo);

        saveIntoSharedPreferences(context, rsLocalStorage);
    }

    public static void refreshLocalStorage(UserInfo userInfo, Context context) {
        RSLocalStorage rsLocalStorage = getLocalStorage(context);
        rsLocalStorage.setUserInfo(userInfo);

        saveIntoSharedPreferences(context, rsLocalStorage);
    }

    private static void saveIntoSharedPreferences(Context context, RSLocalStorage rsLocalStorage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_FILE, new Gson().toJson(rsLocalStorage));
        editor.commit();
    }

    public static User getCurrentUser(Context context) {
        return getLocalStorage(context).getUserInfo().getUser();
    }

    public static UserInfo getCurrentUserInfo(Context context) {
        return getLocalStorage(context).getUserInfo();
    }

    private static RSLocalStorage getLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String localStorage = sharedPreferences.getString(SHARED_PREFERENCES_FILE, "");
        return new Gson().fromJson(localStorage, RSLocalStorage.class);
    }

    public static void cancelSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SHARED_PREFERENCES_FILE);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String token = sharedPreferences.getString(SHARED_PREFERENCES_FILE, "");
        return !token.equals("");
    }
}
