package com.steeringit.rankstop.session;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.data.model.db.FakeUser;
import com.steeringit.rankstop.data.model.db.User;
import com.steeringit.rankstop.data.model.db.UserInfo;
import com.steeringit.rankstop.data.model.network.RSLocalStorage;
import com.steeringit.rankstop.data.model.network.RSResponse;
import com.steeringit.rankstop.data.model.network.RSResponseLogin;
import com.steeringit.rankstop.data.webservices.WebService;
import com.steeringit.rankstop.utils.RSJWTParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (rsLocalStorage != null){
            rsLocalStorage.setUserInfo(userInfo);
            saveIntoSharedPreferences(rsLocalStorage);
        }

    }

    private static void saveIntoSharedPreferences(RSLocalStorage rsLocalStorage) {
        SharedPreferences sharedPreferences = RankStop.getInstance().getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_FILE, new Gson().toJson(rsLocalStorage));
        editor.commit();
    }

    public static User getCurrentUser() {
        // -------------- Old Code -------------------------

       // return getLocalStorage().getUserInfo().getUser();

        // -------------- New Code -------------------------
        if (getLocalStorage() != null)
        return getLocalStorage().getUserInfo().getUser();
        else
            return new User();
    }

    public static UserInfo getCurrentUserInfo() {
        return getLocalStorage().getUserInfo();
    }

    public static String getUsergetToken() {
        return getLocalStorage().getToken();
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

    public static void Reconnecter() {
        FakeUser fakeUser = new FakeUser();
        Call<RSResponse> callLogin = WebService.getInstance().getApi().REloginUser(fakeUser);
        callLogin.enqueue(new Callback<RSResponse>() {
            @Override
            public void onResponse(Call<RSResponse> call, Response<RSResponse> response) {
                if (response.body().getStatus() == 1) {
                    RSResponseLogin loginResponse = new Gson().fromJson(new Gson().toJson(response.body().getData()), RSResponseLogin.class);
                    String token = loginResponse.getToken();
                    RSSessionToken.refreshLocalStorage(token, true);
                }
            }

            @Override
            public void onFailure(Call<RSResponse> call, Throwable t) {

                if (call.isCanceled())
                    Log.i("err", t.getMessage() + "");
            }
        });
    }
}
