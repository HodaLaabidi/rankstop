package com.steeringit.rankstop.utils;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class RSJWTParser {

    public static JSONObject getHeader(String JWT) {
        try {
            validateJWT(JWT);
            int HEADER = 0;
            byte[] sectionDecoded = Base64.decode(JWT.split("\\.")[HEADER], Base64.URL_SAFE);
            return new JSONObject(new String(sectionDecoded, StandardCharsets.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getPayload(String JWT) {
        try {
            validateJWT(JWT);
            int PAYLOAD = 1;
            byte[] sectionDecoded = Base64.decode(JWT.split("\\.")[PAYLOAD], Base64.URL_SAFE);
            return new JSONObject(new String(sectionDecoded, StandardCharsets.UTF_8));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSignature(String JWT) {
        validateJWT(JWT);
        int SIGNATURE = 2;
        byte[] sectionDecoded = Base64.decode(JWT.split("\\.")[SIGNATURE], Base64.URL_SAFE);
        return new String(sectionDecoded, StandardCharsets.UTF_8);
    }

    public static String getClaim(String JWT, String claim) {
        try {
            JSONObject payload = getPayload(JWT);
            Object claimValue = null;
            claimValue = payload.get(claim);
            if (claimValue != null) {
                return claimValue.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void validateJWT(String JWT) {
        // Check if the the JWT has the three parts
        String[] jwtParts = JWT.split("\\.");
        if (jwtParts.length != 3) {
            //throw new CognitoParameterInvalidException("not a JSON Web Token");
        }
    }
}
