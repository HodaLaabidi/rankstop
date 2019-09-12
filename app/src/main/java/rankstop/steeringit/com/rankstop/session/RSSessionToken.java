package com.steeringit.rankstop.session;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import com.steeringit.rankstop.RankStop;
import com.steeringit.rankstop.data.model.db.FakeUser;
import com.steeringit.rankstop.data.model.db.User;
import com.steeringit.rankstop.data.model.db.UserInfo;
import com.steeringit.rankstop.data.model.network.RSLocalStorage;
import com.steeringit.rankstop.data.model.network.RSLocalTokenGest;
import com.steeringit.rankstop.utils.RSJWTParser;

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
